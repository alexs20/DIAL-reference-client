package com.wolandsoft.dial.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SSDPParser {

	private static final String[] EXPECTED_HEADERS = {"HTTP/1.1 200 OK", "HTTP/1.0 200 OK"};
	private static final String LOCATION = "LOCATION:";
	private static final String ST = "ST:";
	private static final String EXPECTED_ST = "urn:dial-multiscreen-org:service:dial:1";
	private static final String WAKEUP = "WAKEUP:";
	private static final String WAKEUP_PARAMS_REGEX = "MAC=([0-9a-fA-F:]*);Timeout=([0-9]*)";
	private static final Pattern WAKEUP_PARAMS_PATTERN = Pattern.compile(WAKEUP_PARAMS_REGEX);
	private static final String USN = "USN:";

	public static SSDPResponce parse(String message) {
		boolean isHeaderRead = false;
		SSDPResponce ret = new SSDPResponce();
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
						ret.location = new URL(line.substring(LOCATION.length()).trim());
						InetAddress address = InetAddress.getByName(ret.location.getHost());
						if (address.getAddress().length > 4) {
							return null;
						}
						if (!address.getHostAddress().equals(ret.location.getHost())) {
							return null;
						}
					} else if (line.startsWith(ST)) {
						String st = line.substring(ST.length()).trim();
						if (!EXPECTED_ST.equals(st)) {
							return null;
						}
					} else if (line.startsWith(USN)) {
						ret.usn = line.substring(USN.length()).trim();
					} else if (line.startsWith(WAKEUP)) {
						String combined = line.substring(WAKEUP.length()).trim();
						Matcher matcher = WAKEUP_PARAMS_PATTERN.matcher(combined);
						if (matcher.matches()) {
							String mac = matcher.group(1);
							if (mac != null) {
								ret.wakeupMac = mac;
							}
							String timeout = matcher.group(2);
							if (timeout != null) {
								ret.wakeupTimeout = Integer.valueOf(timeout);
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

	public static class SSDPResponce {
		private URL location;
		private String usn;
		private String wakeupMac;
		private Integer wakeupTimeout;
		public URL getLocation() {
			return location;
		}
		public String getUsn() {
			return usn;
		}
		public String getWakeupMac() {
			return wakeupMac;
		}
		public Integer getWakeupTimeout() {
			return wakeupTimeout;
		}
		@Override
		public String toString() {
			return "SSDPResponce [location=" + location + ", usn=" + usn + ", wakeupMac=" + wakeupMac + ", wakeupTimeout=" + wakeupTimeout + "]";
		}
	}
}
