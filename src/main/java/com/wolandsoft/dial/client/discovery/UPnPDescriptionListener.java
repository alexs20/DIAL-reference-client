package com.wolandsoft.dial.client.discovery;

public interface UPnPDescriptionListener {

	void onDeviceDiscovered(DiscoveredDevice device);
	
	void onDeviceRemoved(DiscoveredDevice device);
}
