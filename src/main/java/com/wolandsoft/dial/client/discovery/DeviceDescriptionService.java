package com.wolandsoft.dial.client.discovery;

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

public class DeviceDescriptionService implements MSearchServiceListener {

	private Map<String, DiscoveredDevice> devicesMap = Collections.synchronizedMap(new HashMap<String, DiscoveredDevice>());
	private final ScheduledExecutorService scheduler;
	private final long connectTimeout;
	private final long readTimeout;
	private final List<DeviceDescriptionListener> listeners;

	private DeviceDescriptionService(long revalidationPeriod, long connectTimeout, long readTimeout,
			List<DeviceDescriptionListener> listeners) {
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.listeners = listeners;
		this.scheduler = Executors.newScheduledThreadPool(1);

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
								scheduler.submit(new Runnable() {

									@Override
									public void run() {
										for (DeviceDescriptionListener listener : listeners) {
											listener.onDeviceRemoved(device);
										}
									}
								});
							}
						}
					}
				}

			}
		}, revalidationPeriod, revalidationPeriod, TimeUnit.MILLISECONDS);
	}

	private boolean revalidateDevice(DiscoveredDevice device) {
		try {
			URL url = device.getMSearch().getLocation();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout((int) connectTimeout);
			conn.setReadTimeout((int) readTimeout);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			conn.connect();
			int retCode = conn.getResponseCode();
			if (retCode == 200) {
				try (Scanner s = new Scanner(conn.getInputStream()).useDelimiter("\\A")) {
					String data = s.hasNext() ? s.next().trim() : "";
					s.close();
					if (data.length() > 0) {
						DeviceDescriptionResponce devDesc = DeviceDescriptionParser.parse(data);
						if (devDesc != null) {
							device.setDeviceDescritption(devDesc);
							device.setUpdatedAt(new Date());
							return true;
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
	public void onDeviceDiscovery(MSearchResponce message) {
		DiscoveredDevice device = devicesMap.get(message.getUsn());
		if (device == null) {
			device = new DiscoveredDevice();
			device.setMSearch(message);
			if (revalidateDevice(device)) {
				devicesMap.put(device.getMSearch().getUsn(), device);
				final DiscoveredDevice addedDevice = device;
				scheduler.submit(new Runnable() {

					@Override
					public void run() {
						for (DeviceDescriptionListener listener : listeners) {
							listener.onDeviceDiscovered(addedDevice);
						}
					}
				});
			}
		} else {
			device.setUpdatedAt(new Date());
		}
	}

	public static class Builder {

		private long revalidationPeriod = TimeUnit.SECONDS.toMillis(25);
		private long connectTimeout = TimeUnit.SECONDS.toMillis(2);
		private long readTimeout = TimeUnit.SECONDS.toMillis(2);
		private List<DeviceDescriptionListener> listeners = new ArrayList<>();

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

		public Builder withListener(DeviceDescriptionListener listener) {
			this.listeners.add(listener);
			return this;
		}

		public DeviceDescriptionService build() {
			return new DeviceDescriptionService(revalidationPeriod, connectTimeout, readTimeout, listeners);
		}
	}
}
