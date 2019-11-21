package com.wolandsoft.dial.client.rest;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.wolandsoft.dial.client.common.InternalException;

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
public class ApplicationQueryCallable implements Callable<ApplicationQueryResponce> {
	private final Builder config;

	private ApplicationQueryCallable(Builder config) {
		this.config = config;
		if (config.applicationURL == null) {
			throw new InternalException("Application URL is null");
		}
		if (config.applicationName == null) {
			throw new InternalException("Application Name is null");
		}
	}

	public ApplicationQueryResponce call() throws Exception {
		URL url = new URL(
				config.applicationURL.toExternalForm() + config.applicationName + (config.dialVersion == null ? "" : "?clientDialVer=" + config.dialVersion));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout((int) config.connectTimeout);
		conn.setReadTimeout((int) config.readTimeout);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		conn.connect();
		int retCode = conn.getResponseCode();
		if (retCode == 200) {
			try (@SuppressWarnings("resource")
			Scanner s = new Scanner(conn.getInputStream()).useDelimiter("\\A")) {
				String data = s.hasNext() ? s.next().trim() : "";
				s.close();
				if (data.length() > 0) {
					ApplicationQueryResponce devDesc = ApplicationQueryParser.parse(data);
					return devDesc;
				}
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
		private String dialVersion = null;

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

		public Builder withDialVersion(String dialVersion) {
			this.dialVersion = dialVersion;
			return this;
		}

		public ApplicationQueryCallable build() {
			return new ApplicationQueryCallable(this);
		}
	}

}
