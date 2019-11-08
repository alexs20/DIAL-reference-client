package com.wolandsoft.dial.client.discovery;

import java.net.URL;

public class MSearchResponce {
	private URL location = null;
	private String usn = null;
	private String wakeupMac = null;
	private Integer wakeupTimeout = null;

	public URL getLocation() {
		return location;
	}

	protected void setLocation(URL location) {
		this.location = location;
	}

	public String getUsn() {
		return usn;
	}

	protected void setUsn(String usn) {
		this.usn = usn;
	}

	public String getWakeupMac() {
		return wakeupMac;
	}

	protected void setWakeupMac(String wakeupMac) {
		this.wakeupMac = wakeupMac;
	}

	public Integer getWakeupTimeout() {
		return wakeupTimeout;
	}

	protected void setWakeupTimeout(Integer wakeupTimeout) {
		this.wakeupTimeout = wakeupTimeout;
	}

	@Override
	public String toString() {
		return "MSearchResponce [location=" + location + ", usn=" + usn + ", wakeupMac=" + wakeupMac + ", wakeupTimeout=" + wakeupTimeout + "]";
	}
}
