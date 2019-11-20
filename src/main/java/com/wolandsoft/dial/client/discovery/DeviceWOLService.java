package com.wolandsoft.dial.client.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.wolandsoft.dial.client.InternalException;

public class DeviceWOLService implements SSDPMSearchListener {
	private static final int PORT = 9;
	private Map<String, Long> usns = Collections.synchronizedMap(new HashMap<>());

	private final ScheduledExecutorService scheduler;

	public DeviceWOLService() {
		this.scheduler = Executors.newScheduledThreadPool(1);
	}

	public void wakeup(String mac, int timeout, String broadcastAddress, String usn, DeviceWOLCallback callback) {
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
		byte[] macBytes = getMacBytes(mac);
		if (macBytes == null) {
			throw new InternalException("Invalid MAC address");
		}
		byte[] bytes = new byte[6 + 16 * macBytes.length];
		for (int i = 0; i < 6; i++) {
			bytes[i] = (byte) 0xff;
		}
		for (int i = 6; i < bytes.length; i += macBytes.length) {
			System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
		}
		List<DatagramPacket> magicPackets = new ArrayList<>();
		for (InetAddress bcAddr : bcAddress) {
			DatagramPacket packet = new DatagramPacket(bytes, bytes.length, bcAddr, PORT);
			magicPackets.add(packet);
		}
		usns.put(usn, System.currentTimeMillis());
		scheduler.schedule(new Runnable() {

			@Override
			public void run() {
				if (!usns.containsKey(usn)) {
					if (callback != null) {
						scheduler.submit(new Runnable() {
							@Override
							public void run() {
								callback.onDeviceWOLSucceed();
							}
						});
					}
				} else {
					Long startTime = usns.get(usn);
					if (startTime.longValue() + TimeUnit.SECONDS.toMillis(timeout) < System.currentTimeMillis()) {
						usns.remove(usn);
						if (callback != null) {
							scheduler.submit(new Runnable() {
								@Override
								public void run() {
									callback.onDeviceWOLFail();
								}
							});
						}
						return;
					} else {
						try (DatagramSocket socket = new DatagramSocket()) {
							for (DatagramPacket packet : magicPackets) {
								socket.send(packet);
							}
						} catch (IOException e) {

						}
						scheduler.schedule(this, 50, TimeUnit.MILLISECONDS);
					}
				}

			}
		}, 50, TimeUnit.MILLISECONDS);
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
		usns.remove(message.getUsn());

	}
}
