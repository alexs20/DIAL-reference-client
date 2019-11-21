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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UPnPDescriptionService implements SSDPMSearchListener, Closeable {

	private Map<String, DiscoveredDevice> devicesMap = Collections.synchronizedMap(new HashMap<String, DiscoveredDevice>());
	private final ScheduledExecutorService scheduler;
	private final long revalidationPeriod;
	private final long connectTimeout;
	private final long readTimeout;
	private final List<UPnPDescriptionListener> listeners;

	private UPnPDescriptionService(long revalidationPeriod, long connectTimeout, long readTimeout, List<UPnPDescriptionListener> listeners) {
		this.revalidationPeriod = revalidationPeriod;
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.listeners = listeners;
		this.scheduler = Executors.newScheduledThreadPool(1);
		run();
	}

	private void run() {
		scheduler.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				Set<String> s = devicesMap.keySet();
				synchronized (devicesMap) {
					Iterator<String> i = s.iterator();
					while (i.hasNext()) {
						String key = i.next();
						DiscoveredDevice device = devicesMap.get(key);
						if ((device.getUpdatedAt().getTime() + revalidationPeriod) < System.currentTimeMillis()) {
							if (!revalidateDevice(device)) {
								i.remove();
								for (UPnPDescriptionListener listener : listeners) {
									scheduler.submit(new Runnable() {
										@Override
										public void run() {
											listener.onDeviceRemoved(device);
										}
									});
								}
							}
						}
					}
				}

			}
		}, revalidationPeriod, revalidationPeriod, TimeUnit.MILLISECONDS);
	}

	private boolean revalidateDevice(DiscoveredDevice device) {
		try {
			URL url = device.getMSearchResponce().getLocation();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout((int) connectTimeout);
			conn.setReadTimeout((int) readTimeout);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			conn.connect();
			int retCode = conn.getResponseCode();
			if (retCode == 200) {
				String appUrlStr = conn.getHeaderField("Application-URL");
				if (appUrlStr != null) {
					try (Scanner s = new Scanner(conn.getInputStream()).useDelimiter("\\A")) {
						String data = s.hasNext() ? s.next().trim() : "";
						s.close();
						if (data.length() > 0) {
							UPnPDescriptionResponce devDesc = UPnPDescriptionParser.parse(data);
							if (devDesc != null) {
								device.setApplicationURL(new URL(appUrlStr));
								device.setDescriptionResponce(devDesc);
								device.setUpdatedAt(new Date());
								return true;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// ignore
		}
		return false;
	}

	@Override
	public void onDeviceDiscovery(SSDPMSearchResponce message) {
		DiscoveredDevice device = devicesMap.get(message.getUsn());
		if (device == null) {
			device = new DiscoveredDevice();
			device.setMSearchResponce(message);
			if (revalidateDevice(device)) {
				devicesMap.put(device.getMSearchResponce().getUsn(), device);
				DiscoveredDevice addedDevice = device;
				for (UPnPDescriptionListener listener : listeners) {
					scheduler.submit(new Runnable() {
						@Override
						public void run() {
							listener.onDeviceDiscovered(addedDevice);
						}
					});
				}
			}
		} else {
			device.setUpdatedAt(new Date());
		}
	}

	public List<DiscoveredDevice> getDiscoveredDevices() {
		synchronized (devicesMap) {
			return Collections.unmodifiableList(new ArrayList<>(devicesMap.values()));
		}
	}

	@Override
	public void close() throws IOException {
		scheduler.shutdown();
	}

	public static class Builder {
		private long revalidationPeriod = TimeUnit.SECONDS.toMillis(25);
		private long connectTimeout = TimeUnit.SECONDS.toMillis(2);
		private long readTimeout = TimeUnit.SECONDS.toMillis(2);
		private List<UPnPDescriptionListener> listeners = new ArrayList<>();

		public Builder withRevalidationPeriod(long revalidationPeriod) {
			this.revalidationPeriod = revalidationPeriod;
			return this;
		}

		public Builder withConnectTimeout(long connectTimeout) {
			this.connectTimeout = connectTimeout;
			return this;
		}

		public Builder withReadTimeout(long readTimeout) {
			this.readTimeout = readTimeout;
			return this;
		}

		public Builder withListener(UPnPDescriptionListener listener) {
			this.listeners.add(listener);
			return this;
		}

		public UPnPDescriptionService build() {
			return new UPnPDescriptionService(revalidationPeriod, connectTimeout, readTimeout, listeners);
		}
	}
}
