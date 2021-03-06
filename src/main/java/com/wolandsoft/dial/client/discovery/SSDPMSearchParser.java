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

import java.io.BufferedReader;
import java.io.StringReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SSDPMSearchParser {

	private static final String[] EXPECTED_HEADERS = {"HTTP/1.1 200 OK", "HTTP/1.0 200 OK"};
	private static final String LOCATION = "LOCATION:";
	private static final String ST = "ST:";
	private static final String EXPECTED_ST = "urn:dial-multiscreen-org:service:dial:1";
	private static final String WAKEUP = "WAKEUP:";
	private static final String WAKEUP_PARAMS_REGEX = "MAC=([0-9a-fA-F:]*);Timeout=([0-9]*)";
	private static final Pattern WAKEUP_PARAMS_PATTERN = Pattern.compile(WAKEUP_PARAMS_REGEX);
	private static final String USN = "USN:";

	public static SSDPMSearchResponce parse(String message) {
		boolean isHeaderRead = false;
		SSDPMSearchResponce ret = new SSDPMSearchResponce();
		StringReader sr = new StringReader(message);
		try (BufferedReader reader = new BufferedReader(sr)) {
			String line = reader.readLine();
			while (line != null) {
				if (!isHeaderRead) {
					for (String expectedHeader : EXPECTED_HEADERS) {
						if (line.equals(expectedHeader)) {
							isHeaderRead = true;
							break;
						}
					}
					if (!isHeaderRead) {
						return null;
					}
				} else {
					if (line.startsWith(LOCATION)) {
						ret.setLocation(new URL(line.substring(LOCATION.length()).trim()));
					} else if (line.startsWith(ST)) {
						String st = line.substring(ST.length()).trim();
						if (!EXPECTED_ST.equals(st)) {
							return null;
						}
					} else if (line.startsWith(USN)) {
						ret.setUsn(line.substring(USN.length()).trim());
					} else if (line.startsWith(WAKEUP)) {
						String combined = line.substring(WAKEUP.length()).trim();
						Matcher matcher = WAKEUP_PARAMS_PATTERN.matcher(combined);
						if (matcher.matches()) {
							String mac = matcher.group(1);
							if (mac != null) {
								ret.setWakeupMac(mac);
							}
							String timeout = matcher.group(2);
							if (timeout != null) {
								ret.setWakeupTimeout(Integer.valueOf(timeout));
							}
						}
					}
				}
				line = reader.readLine();
			}
		} catch (Exception ignore) {
			return null;
		}
		return ret;
	}

}
