package com.wolandsoft.dial.client.rest;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ApplicationLaunchService implements Closeable {
	private final long connectTimeout;
	private final long readTimeout;
	private final ExecutorService executor;

	public ApplicationLaunchService(long connectTimeout, long readTimeout) {
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
		this.executor = new ThreadPoolExecutor(1, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	public Future<URL> launch(URL applicationURL, String applicationName, String friendlyName, String payload) {
		return query(applicationURL, applicationName, friendlyName, payload, null);
	}
	public Future<URL> query(URL applicationURL, String applicationName, String friendlyName, String payload, RestServiceCallback<URL> callback) {
		return executor.submit(new Callable<URL>() {

			@Override
			public URL call() throws Exception {
				try {
					URL url = new URL(applicationURL.toExternalForm() + applicationName);// + "?friendlyName='" + friendlyName + "'");
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setConnectTimeout((int) connectTimeout);
					conn.setReadTimeout((int) readTimeout);
					conn.setRequestMethod("POST");
					if (payload != null) {
						conn.setDoOutput(true);
						conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
						conn.setRequestProperty("Content-Length", String.valueOf(payload.getBytes().length));
						try (OutputStream os = conn.getOutputStream(); OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8")) {
							osw.write(payload);
							osw.flush();
						}
					}
					conn.connect();
					int retCode = conn.getResponseCode();
					if (retCode == 200 || retCode == 201) {
						String appInstanceUrl = conn.getHeaderField("LOCATION");
						if (appInstanceUrl != null) { 
							return new URL(appInstanceUrl);
						}
					} else {
						System.out.println(conn.getResponseMessage());
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

		public ApplicationLaunchService build() {
			return new ApplicationLaunchService(connectTimeout, readTimeout);
		}
	}
}
