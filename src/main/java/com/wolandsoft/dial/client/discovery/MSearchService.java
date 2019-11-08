package com.wolandsoft.dial.client.discovery;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.wolandsoft.dial.client.InternalException;
import com.wolandsoft.dial.client.Resource;

public class MSearchService implements Closeable {

	private final static String MC_ADDRESS = "239.255.255.250";
	private final static int MC_PORT = 1900;
	private final static String M_SEARCH_RESOURCE = "m-search.msg";
	private final static String THREAD_NAME = "MSearchListener";

	private final String discoverMessage;
	private final byte[] recvBuffer = new byte[1400];
	private final ScheduledExecutorService scheduler;
	private Thread serverThread;
	private MulticastSocket serverSocket;
	private final List<MSearchServiceListener> listeners;
	private final long discoveryInterval;

	private MSearchService(String osName, String osVersion, String productName, String productVersion, List<MSearchServiceListener> listeners,
			long discoveryInterval) {
		this.discoverMessage = Resource.read(M_SEARCH_RESOURCE, osName, osVersion, productName, productVersion);
		this.listeners = listeners;
		this.discoveryInterval = discoveryInterval;
		this.scheduler = Executors.newScheduledThreadPool(1);
		run();
	}

	private void run() {
		try {
			InetAddress multicastAddress = InetAddress.getByName(MC_ADDRESS);
			serverSocket = new MulticastSocket(0);
			DatagramPacket packet = new DatagramPacket(recvBuffer, recvBuffer.length);
			serverThread = new Thread(new Runnable() {

				@Override
				public void run() {
					while (!serverSocket.isClosed()) {
						try {
							serverSocket.receive(packet);
							if (packet.getLength() > 0) {
								String response = new String(packet.getData());
								MSearchResponce sspdResp = MSearchParser.parse(response);
								if (sspdResp != null) {
									scheduler.submit(new Runnable() {

										@Override
										public void run() {
											for (MSearchServiceListener listener : listeners) {
												listener.onDeviceDiscovery(sspdResp);
											}
										}
									});
								}
							}
						} catch (SocketException ex) {
							// ignore
						} catch (IOException ex) {
							throw new InternalException(ex.getMessage(), ex);
						}
					}
				}
			}, THREAD_NAME);
			serverThread.start();
			scheduler.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					if (!serverSocket.isClosed()) {
						byte[] requestMessage = discoverMessage.getBytes(StandardCharsets.UTF_8);
						DatagramPacket datagramPacket = new DatagramPacket(requestMessage, requestMessage.length, multicastAddress, MC_PORT);
						try {
							serverSocket.send(datagramPacket);
						} catch (SocketException ex) {
							// ignore
						} catch (IOException ex) {
							throw new InternalException(ex.getMessage(), ex);
						}
					}
				}
			}, 0, discoveryInterval, TimeUnit.MILLISECONDS);
		} catch (Exception ex) {
			throw new InternalException(ex.getMessage(), ex);
		}
	}

	@Override
	public void close() throws IOException {
		serverSocket.close();
		scheduler.shutdown();
	}

	public static class Builder {

		private String productName = "Unknown";
		private String productVersion = "0.0.0";
		private String osName = System.getProperty("os.name");
		private String osVersion = System.getProperty("os.version");
		private final List<MSearchServiceListener> listeners = new ArrayList<>();
		private long discoveryInterval = TimeUnit.SECONDS.toMillis(10);

		public Builder withProductName(String productName) {
			this.productName = productName;
			return this;
		}

		public Builder withProductVersion(String productVersion) {
			this.productVersion = productVersion;
			return this;
		}

		public Builder withOSName(String osName) {
			this.osName = osName;
			return this;
		}

		public Builder withOSVersion(String osVersion) {
			this.osVersion = osVersion;
			return this;
		}

		public Builder withListener(MSearchServiceListener listener) {
			this.listeners.add(listener);
			return this;
		}

		public Builder withDiscoveryInterval(long seconds) {
			this.discoveryInterval = seconds;
			return this;
		}

		public MSearchService build() {
			return new MSearchService(osName, osVersion, productName, productVersion, listeners, discoveryInterval);
		}
	}
}
