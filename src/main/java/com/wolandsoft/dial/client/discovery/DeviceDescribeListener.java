package com.wolandsoft.dial.client.discovery;

public interface DeviceDescribeListener {

	void onDeviceDiscovered(DiscoveredDevice device);
	
	void onDeviceRemoved(DiscoveredDevice device);
}
