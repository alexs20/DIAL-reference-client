package com.wolandsoft.dial.client.rest;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.wolandsoft.dial.client.common.InternalException;

public class ApplicationStopCallable implements Callable<Void> {
	private final Builder config;

	public ApplicationStopCallable(Builder config) {
		this.config = config;
		if (config.applicationInstanceURL == null) {
			throw new InternalException("Application Instance URL is null");
		}
	}

	public Void call() throws Exception {
		HttpURLConnection conn = (HttpURLConnection) config.applicationInstanceURL.openConnection();
		conn.setConnectTimeout((int) config.connectTimeout);
		conn.setReadTimeout((int) config.readTimeout);
		conn.setRequestMethod("DELETE");
		conn.connect();
		int retCode = conn.getResponseCode();
		if (retCode != 200) {
			throw new RestException(retCode, conn.getResponseMessage());
		}
		return null;

	}

	public static class Builder {
		private long connectTimeout = TimeUnit.SECONDS.toMillis(2);
		private long readTimeout = TimeUnit.SECONDS.toMillis(2);
		private URL applicationInstanceURL = null;

		public Builder withConnectTimeout(long connectTimeout) {
			this.connectTimeout = connectTimeout;
			return this;
		}

		public Builder withReadTimeout(long readTimeout) {
			this.readTimeout = readTimeout;
			return this;
		}

		public Builder withApplicationInstanceURL(URL applicationInstanceURL) {
			this.applicationInstanceURL = applicationInstanceURL;
			return this;
		}

		public ApplicationStopCallable build() {
			return new ApplicationStopCallable(this);
		}
	}
}
