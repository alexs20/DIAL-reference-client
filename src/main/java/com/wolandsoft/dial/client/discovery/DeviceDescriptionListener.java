package com.wolandsoft.dial.client.discovery;

public interface DeviceDescriptionListener {

	void onDeviceDiscovered(DiscoveredDevice device);
	
	void onDeviceRemoved(DiscoveredDevice device);
}
