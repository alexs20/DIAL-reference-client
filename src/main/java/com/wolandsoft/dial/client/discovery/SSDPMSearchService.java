/*
    Copyright © 2019 Alexander Shulgin
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
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

import com.wolandsoft.dial.client.common.InternalException;
import com.wolandsoft.dial.client.common.Resource;

public class SSDPMSearchService implements Closeable {

	private final static String MC_ADDRESS = "239.255.255.250";
	private final static int MC_PORT = 1900;
	private final static String M_SEARCH_RESOURCE = "m-search.msg";

	private final Builder config;
	private final String discoverMessage;
	private final byte[] recvBuffer = new byte[1400];
	private final ScheduledExecutorService scheduler;
	private MulticastSocket serverSocket;

	private SSDPMSearchService(Builder config) {
		this.config = config;
		this.discoverMessage = Resource.read(M_SEARCH_RESOURCE, config.osName, config.osVersion, config.productName, config.productVersion);
		this.scheduler = Executors.newScheduledThreadPool(2);
		run();
	}

	private void run() {
		try {
			InetAddress multicastAddress = InetAddress.getByName(MC_ADDRESS);
			serverSocket = new MulticastSocket(0);
			DatagramPacket packet = new DatagramPacket(recvBuffer, recvBuffer.length);
			scheduler.submit(new Runnable() {

				@Override
				public void run() {
					while (!serverSocket.isClosed()) {
						try {
							serverSocket.receive(packet);
							if (packet.getLength() > 0) {
								String response = new String(packet.getData(), packet.getOffset(), packet.getLength());
								SSDPMSearchResponce sspdResp = SSDPMSearchParser.parse(response);
								if (sspdResp != null) {
									for (SSDPMSearchListener listener : config.listeners) {
										scheduler.submit(new Runnable() {
											@Override
											public void run() {
												listener.onDeviceDiscovery(sspdResp);
											}
										});
									}
								}
							}
						} catch (SocketException ex) {
							// ignore
						} catch (IOException ex) {
							throw new InternalException(ex.getMessage(), ex);
						}
					}
				}
			});
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
			}, 0, config.discoveryInterval, TimeUnit.MILLISECONDS);
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
		private final List<SSDPMSearchListener> listeners = new ArrayList<>();
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

		public Builder withListener(SSDPMSearchListener listener) {
			this.listeners.add(listener);
			return this;
		}

		public Builder withDiscoveryInterval(long milliSeconds) {
			this.discoveryInterval = milliSeconds;
			return this;
		}

		public SSDPMSearchService build() {
			return new SSDPMSearchService(this);
		}
	}
}
