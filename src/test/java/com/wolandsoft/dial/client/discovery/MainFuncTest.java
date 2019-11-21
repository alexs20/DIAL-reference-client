package com.wolandsoft.dial.client.discovery;

import java.net.URL;

import org.junit.Test;

import com.wolandsoft.dial.client.rest.ApplicationLaunchCallable;
import com.wolandsoft.dial.client.rest.ApplicationQueryCallable;
import com.wolandsoft.dial.client.rest.ApplicationQueryResponce;
import com.wolandsoft.dial.client.rest.ApplicationStopCallable;

public class MainFuncTest {
	@SuppressWarnings("unused")
	@Test
	public void testApp() throws Exception {

		UPnPDescriptionService dds = new UPnPDescriptionService.Builder().withListener(new UPnPDescriptionListener() {

			@Override
			public void onDeviceDiscovered(DiscoveredDevice device) {
				System.out.println("onDeviceDiscovered " + device);
				if (device.getDescriptionResponce().getFriendlyName().equals("DIAL server sample")) {
					try {
						ApplicationQueryCallable aq = new ApplicationQueryCallable.Builder().withApplicationURL(device.getApplicationURL())
								.withApplicationName("YouTube").build();
						ApplicationQueryResponce aqr = aq.call();
						System.out.println("appQuery " + aqr);

						ApplicationLaunchCallable al = new ApplicationLaunchCallable.Builder().withApplicationURL(device.getApplicationURL())
								.withApplicationName("YouTube").build();
						URL appInstanceUrl = al.call();
						System.out.println("appInstance " + appInstanceUrl);

						ApplicationStopCallable as = new ApplicationStopCallable.Builder().withApplicationInstanceURL(appInstanceUrl).build();
						as.call();

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onDeviceRemoved(DiscoveredDevice device) {
				System.out.println("onDeviceRemoved " + device);

			}
		}).build();

		DeviceWOLService wol = new DeviceWOLService.Builder().build();

		SSDPMSearchService ds = new SSDPMSearchService.Builder().withListener(dds).withListener(wol).build();

		wol.wakeup("10:dd:b1:c9:00:e4", 10, null, "uuid:4248037a-d4b5-6c7b-0000-000062dc019d::urn:dial-multiscreen-org:service:dial:1",
				new DeviceWOLCallback() {

					@Override
					public void onDeviceWOLSucceed() {
						System.out.println("onDeviceWOLSucceed");
					}

					@Override
					public void onDeviceWOLFail() {
						System.out.println("onDeviceWOLFail");
					}
				});

		Thread.sleep(15000);
	}
}
