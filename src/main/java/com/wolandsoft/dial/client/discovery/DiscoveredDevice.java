package com.wolandsoft.dial.client.discovery;

import java.util.Date;

public class DiscoveredDevice {
	private DeviceDescriptionResponce deviceDescritption = null;
	private MSearchResponce mSearch = null;
	private Date updatedAt = null;
	public DeviceDescriptionResponce getDeviceDescritption() {
		return deviceDescritption;
	}
	protected void setDeviceDescritption(DeviceDescriptionResponce deviceDescritption) {
		this.deviceDescritption = deviceDescritption;
	}
	public MSearchResponce getMSearch() {
		return mSearch;
	}
	protected void setMSearch(MSearchResponce mSearch) {
		this.mSearch = mSearch;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	protected void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

}
