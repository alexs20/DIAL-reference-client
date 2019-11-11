package com.wolandsoft.dial.client.rest;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ApplicationQueryService implements Closeable {
	private final long connectTimeout;
	private final long readTimeout;
	private final ExecutorService executor;

	public ApplicationQueryService(long connectTimeout, long readTimeout) {
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.executor = new ThreadPoolExecutor(1, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	public Future<ApplicationQueryResponce> query(URL applicationURL, String applicationName) {
		return query(applicationURL, applicationName, null);
	}
	public Future<ApplicationQueryResponce> query(URL applicationURL, String applicationName, RestServiceCallback<ApplicationQueryResponce> callback) {
		return executor.submit(new Callable<ApplicationQueryResponce>() {

			@Override
			public ApplicationQueryResponce call() throws Exception {
				try {
					URL url = new URL(applicationURL.toExternalForm() + applicationName + "?clientDialVer=2.1");
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
							System.out.println(data);
							s.close();
							if (data.length() > 0) {
								ApplicationQueryResponce devDesc = ApplicationQueryParser.parse(data);
								if (callback != null) {
									callback.onResult(devDesc);
								}
								return devDesc;
							}
						}
					}
				} catch (Exception e) {
					// ignore
				}
				if (callback != null) {
					callback.onResult(null);
				}
				return null;
			}
		});

	}

	@Override
	public void close() throws IOException {
		executor.shutdown();
	}

	public static class Builder {
		private long connectTimeout = TimeUnit.SECONDS.toMillis(2);
		private long readTimeout = TimeUnit.SECONDS.toMillis(2);

		public Builder withConnectTimeout(long connectTimeout) {
			this.connectTimeout = connectTimeout;
			return this;
		}

		public Builder withReadTimeout(long readTimeout) {
			this.readTimeout = readTimeout;
			return this;
		}

		public ApplicationQueryService build() {
			return new ApplicationQueryService(connectTimeout, readTimeout);
		}
	}
}
