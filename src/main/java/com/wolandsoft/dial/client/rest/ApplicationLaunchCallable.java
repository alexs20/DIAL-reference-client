package com.wolandsoft.dial.client.rest;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.wolandsoft.dial.client.common.InternalException;

public class ApplicationLaunchCallable implements Callable<URL> {
	private final Builder config;

	public ApplicationLaunchCallable(Builder config) {
		this.config = config;
		if (config.applicationURL == null) {
			throw new InternalException("Application URL is null");
		}
		if (config.applicationName == null) {
			throw new InternalException("Application Name is null");
		}
	}

	public URL call() throws Exception {
		URL url = new URL(config.applicationURL.toExternalForm() + config.applicationName
				+ (config.friendlyName == null ? "" : "?friendlyName=" + config.friendlyName));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout((int) config.connectTimeout);
		conn.setReadTimeout((int) config.readTimeout);
		conn.setRequestMethod("POST");
		if (config.payload != null) {
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
			conn.setRequestProperty("Content-Length", String.valueOf(config.payload.getBytes().length));
			try (OutputStream os = conn.getOutputStream(); OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8")) {
				osw.write(config.payload);
				osw.flush();
			}
		}
		conn.connect();
		int retCode = conn.getResponseCode();
		if (retCode == 200 || retCode == 201) {
			String appInstanceUrl = conn.getHeaderField("LOCATION");
			if (appInstanceUrl != null) {
				URL retUrl = null;
				try {
					retUrl = new URL(appInstanceUrl);
				} catch (MalformedURLException mue) {
					retUrl = new URL(config.applicationURL.getProtocol(), config.applicationURL.getHost(), config.applicationURL.getPort(), appInstanceUrl);
				}
				return retUrl;
			}
		} else {
			throw new RestException(retCode, conn.getResponseMessage());
		}
		return null;

	}

	public static class Builder {
		private long connectTimeout = TimeUnit.SECONDS.toMillis(2);
		private long readTimeout = TimeUnit.SECONDS.toMillis(2);
		private URL applicationURL = null;
		private String applicationName = null;
		private String friendlyName = null;
		private String payload = null;

		public Builder withConnectTimeout(long connectTimeout) {
			this.connectTimeout = connectTimeout;
			return this;
		}

		public Builder withReadTimeout(long readTimeout) {
			this.readTimeout = readTimeout;
			return this;
		}

		public Builder withApplicationURL(URL applicationURL) {
			this.applicationURL = applicationURL;
			return this;
		}

		public Builder withApplicationName(String applicationName) {
			this.applicationName = applicationName;
			return this;
		}

		public Builder withFriendlyName(String friendlyName) {
			this.friendlyName = friendlyName;
			return this;
		}

		public Builder withPayload(String payload) {
			this.payload = payload;
			return this;
		}

		public ApplicationLaunchCallable build() {
			return new ApplicationLaunchCallable(this);
		}
	}
}
