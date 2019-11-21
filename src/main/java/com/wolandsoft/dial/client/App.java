package com.wolandsoft.dial.client;

import java.io.IOException;
import java.net.URISyntaxException;

import com.wolandsoft.dial.client.discovery.DeviceWOLCallback;
import com.wolandsoft.dial.client.discovery.DeviceWOLService;
import com.wolandsoft.dial.client.discovery.DiscoveredDevice;
import com.wolandsoft.dial.client.discovery.SSDPMSearchService;
import com.wolandsoft.dial.client.discovery.UPnPDescriptionListener;
import com.wolandsoft.dial.client.discovery.UPnPDescriptionService;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, URISyntaxException, InterruptedException
    {


    	
    	UPnPDescriptionService dds = new UPnPDescriptionService.Builder()
    			.withListener(new UPnPDescriptionListener() {

					@Override
					public void onDeviceDiscovered(DiscoveredDevice device) {
						System.out.println("onDeviceDiscovered " + device);
//						try {
//							ApplicationQueryResponce aqr = appQuery.query(device.getApplicationURL(), "YouTube").get();
//							System.out.println("appQuery " + aqr);
//							//if (aqr != null && device.getDescriptionResponce().getFriendlyName().equals("DIAL server sample")) {
//								URL appInstance = appLaunch.launch(device.getApplicationURL(), "YouTube", "Java test", "test-payload").get();
//								System.out.println("appInstance " + appInstance);	
//							//}
//							
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						} catch (ExecutionException e) {
//							e.printStackTrace();
//						}
					}

					@Override
					public void onDeviceRemoved(DiscoveredDevice device) {
						System.out.println("onDeviceRemoved " + device);
						
					}})
    			.build();
    	
    	DeviceWOLService wol = new DeviceWOLService.Builder().build();
    	
    	//System.out.println( Resource.read("m-search.msg", System.getProperty("os.name"), System.getProperty("os.version"), "test", "test") );
    	SSDPMSearchService ds = new SSDPMSearchService.Builder()
    			.withListener(dds)
    			.withListener(wol)
    			.build();
    	
    	wol.wakeup("10:dd:b1:c9:00:e4", 10, null, "uuid:4248037a-d4b5-6c7b-0000-000062dc019d::urn:dial-multiscreen-org:service:dial:1", new DeviceWOLCallback() {

			@Override
			public void onDeviceWOLSucceed() {
				System.out.println("onDeviceWOLSucceed");
			}

			@Override
			public void onDeviceWOLFail() {
				System.out.println("onDeviceWOLFail");
			}});
    	
    	synchronized(Thread.currentThread()) {
    		Thread.currentThread().wait();
    	}
    	//ds.close();
    	//Thread.sleep(13000);
    }
}
