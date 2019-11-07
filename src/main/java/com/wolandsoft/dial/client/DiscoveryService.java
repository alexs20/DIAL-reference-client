package com.wolandsoft.dial.client;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.wolandsoft.dial.client.SSDPParser.SSDPResponce;

public class DiscoveryService implements Closeable {

	private final static String MC_ADDRESS = "239.255.255.250";
	private final static int MC_PORT = 1900;
	private final static String M_SEARCH_RESOURCE = "m-search.msg";
	private final static String THREAD_NAME = "DiscoveryServer";
	private final static long DISCOVERY_INTERVAL = TimeUnit.SECONDS.toMillis(10);
	
	private final String discoverMessage;
	private final byte[] recvBuffer = new byte[1400];
	private final ScheduledExecutorService scheduler;
	private Thread serverThread;
	private MulticastSocket serverSocket;
	
	public DiscoveryService (String productName, String productVersion) {
		this(System.getProperty("os.name"), System.getProperty("os.version"), productName, productVersion);
	}
	
	public DiscoveryService (String osName, String osVersion, String productName, String productVersion) {
		discoverMessage = Resource.read(M_SEARCH_RESOURCE, osName, osVersion, productName, productVersion);
		scheduler = Executors.newScheduledThreadPool(1);
		run();
	}

	private void run() {
		try {
		InetAddress multicastAddress = InetAddress.getByName(MC_ADDRESS);
		serverSocket = new MulticastSocket(0);
		//serverSocket.setReuseAddress(true);
		//serverSocket.setSoTimeout(0);
		//serverSocket.joinGroup(multicastAddress);
		DatagramPacket packet = new DatagramPacket(recvBuffer, recvBuffer.length);
		serverThread = new Thread(new Runnable () {

			@Override
			public void run() {
				while(!serverSocket.isClosed()) {
					try {
						System.out.println("Entering in blocking mode");
						serverSocket.receive(packet);
						String response = new String(packet.getData());
						
						SSDPResponce resp = SSDPParser.parse(response);
						System.out.println(resp);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}}, THREAD_NAME);
		serverThread.start();
		scheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				byte[] requestMessage = discoverMessage.getBytes(StandardCharsets.UTF_8);
				DatagramPacket datagramPacket = new DatagramPacket(requestMessage,
				 requestMessage.length, multicastAddress, MC_PORT);
				try {
					System.out.println("Sending M-SEARCH");
					serverSocket.send(datagramPacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}}, 0, DISCOVERY_INTERVAL, TimeUnit.MILLISECONDS);
		} catch (Exception ex) {
			throw new InternalException(ex.getMessage(), ex);
		}
//		recvBuffer

//		byte[] requestMessage = discoverMessage.getBytes(StandardCharsets.UTF_8);
//		DatagramPacket datagramPacket = new DatagramPacket(requestMessage,
//		           requestMessage.length, multicastAddress, MC_PORT);
//
//		// Send the package.
//		usedSocket.send(datagramPacket);
//
//		// Listen for the response.
//		byte[] buf = new byte[9000];
//		DatagramPacket packet = new DatagramPacket(buf, buf.length);
//		usedSocket.receive(packet);
//
//		response = new String(packet.getData());
	}

	@Override
	public void close() throws IOException {
		serverSocket.close();
		scheduler.shutdown();
	}

}
