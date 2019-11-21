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
package com.wolandsoft.dial.client.rest;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.wolandsoft.dial.client.common.InternalException;

public class SystemSleepCallable implements Callable<Void> {
	private final Builder config;

	public SystemSleepCallable(Builder config) {
		this.config = config;
		if (config.applicationURL == null) {
			throw new InternalException("Application URL is null");
		}
	}

	public Void call() throws Exception {
		URL url = new URL(config.applicationURL.toExternalForm() + "system?action=sleep" + (config.key == null ? "" : "&key=" + config.key));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout((int) config.connectTimeout);
		conn.setReadTimeout((int) config.readTimeout);
		conn.setRequestMethod("POST");
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
		private URL applicationURL = null;
		private String key = null;

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

		public Builder withKey(String key) {
			this.key = key;
			return this;
		}

		public SystemSleepCallable build() {
			return new SystemSleepCallable(this);
		}
	}
}
