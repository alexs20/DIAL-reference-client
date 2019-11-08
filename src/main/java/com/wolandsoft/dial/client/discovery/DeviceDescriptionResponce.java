package com.wolandsoft.dial.client.discovery;

public class DeviceDescriptionResponce {
	private String friendlyName = null;
	private String manufacturer = null;

	public String getFriendlyName() {
		return friendlyName;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	protected void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	protected void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	@Override
	public String toString() {
		return "DeviceDescriptionResponce [friendlyName=" + friendlyName + ", manufacturer=" + manufacturer + "]";
	}

}
