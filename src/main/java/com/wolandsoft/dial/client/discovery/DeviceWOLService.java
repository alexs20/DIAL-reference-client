package com.wolandsoft.dial.client.discovery;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.wolandsoft.dial.client.InternalException;

public class DeviceWOLService implements SSDPMSearchListener {
	public static final int PORT = 9;

	private final ScheduledExecutorService scheduler;

	public DeviceWOLService() {
		this.scheduler = Executors.newScheduledThreadPool(1);
	}

	public void wakeup(String mac, String usn) {
		wakeup(mac, null, usn, null);
	}

	public void wakeup(String mac, String usn, DeviceWOLCallback callback) {
		wakeup(mac, null, usn, callback);
	}

	public void wakeup(String mac, String broadcastAddress, String usn) {
		wakeup(mac, broadcastAddress, usn, null);
	}

	public void wakeup(String mac, String broadcastAddress, String usn, DeviceWOLCallback callback) {
		List<InetAddress> bcAddress;
		if (broadcastAddress != null) {
			bcAddress = new ArrayList<>();
			try {
				bcAddress.add(InetAddress.getByName(broadcastAddress));
			} catch (UnknownHostException e) {
				throw new InternalException(e.getMessage(), e);
			}
		} else {
			bcAddress = getLocalBroadcastAddresses();
			if (bcAddress.isEmpty()) {
				throw new InternalException("Cannot determine local broadcast group(s)");
			}
		}
		byte [] macBytes = getMacBytes(mac);
		if (macBytes == null) {
			throw new InternalException("Invalid MAC address");
		}
	}

	private List<InetAddress> getLocalBroadcastAddresses() {
		ArrayList<InetAddress> ret = new ArrayList<>();
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isLoopback())
					continue;
				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null)
						continue;
					ret.add(broadcast);
				}
			}
		} catch (Exception ex) {

		}
		return ret;
	}

	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println("Usage: java WakeOnLan <broadcast-ip> <mac-address>");
			System.out.println("Example: java WakeOnLan 192.168.0.255 00:0D:61:08:22:4A");
			System.out.println("Example: java WakeOnLan 192.168.0.255 00-0D-61-08-22-4A");
			System.exit(1);
		}

		String ipStr = args[0];
		String macStr = args[1];

		try {
			byte[] macBytes = getMacBytes(macStr);
			byte[] bytes = new byte[6 + 16 * macBytes.length];
			for (int i = 0; i < 6; i++) {
				bytes[i] = (byte) 0xff;
			}
			for (int i = 6; i < bytes.length; i += macBytes.length) {
				System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
			}

			InetAddress address = InetAddress.getByName(ipStr);
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
			DatagramSocket socket = new DatagramSocket();
			socket.send(packet);
			socket.close();

			System.out.println("Wake-on-LAN packet sent.");
		} catch (Exception e) {
			System.out.println("Failed to send Wake-on-LAN packet: + e");
			System.exit(1);
		}

	}

	private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
		byte[] bytes = new byte[6];
		String[] hex = macStr.split("(\\:|\\-)");
		if (hex.length != 6) {
			return null;
		}
		try {
			for (int i = 0; i < 6; i++) {
				bytes[i] = (byte) Integer.parseInt(hex[i], 16);
			}
		} catch (NumberFormatException e) {
			return null;
		}
		return bytes;
	}

	@Override
	public void onDeviceDiscovery(SSDPMSearchResponce message) {
		// TODO Auto-generated method stub

	}
}
