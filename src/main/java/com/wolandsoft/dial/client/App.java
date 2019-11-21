package com.wolandsoft.dial.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import com.wolandsoft.dial.client.discovery.DeviceWOLCallback;
import com.wolandsoft.dial.client.discovery.DeviceWOLService;
import com.wolandsoft.dial.client.discovery.DiscoveredDevice;
import com.wolandsoft.dial.client.discovery.SSDPMSearchService;
import com.wolandsoft.dial.client.discovery.UPnPDescriptionListener;
import com.wolandsoft.dial.client.discovery.UPnPDescriptionService;
import com.wolandsoft.dial.client.rest.ApplicationHideCallable;
import com.wolandsoft.dial.client.rest.ApplicationLaunchCallable;
import com.wolandsoft.dial.client.rest.ApplicationQueryCallable;
import com.wolandsoft.dial.client.rest.ApplicationQueryResponce;
import com.wolandsoft.dial.client.rest.ApplicationStopCallable;

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
						if (device.getDescriptionResponce().getFriendlyName().equals("DIAL server sample")) {
							try {
								ApplicationQueryCallable aq =  new ApplicationQueryCallable.Builder()
										.withApplicationURL(device.getApplicationURL())
										.withApplicationName("YouTube")
										.build();
								ApplicationQueryResponce aqr = aq.call();
								System.out.println("appQuery " + aqr);
								
								ApplicationLaunchCallable al = new ApplicationLaunchCallable.Builder()
										.withApplicationURL(device.getApplicationURL())
										.withApplicationName("YouTube")										
										.build();
								URL appInstanceUrl = al.call();
								System.out.println("appInstance " + appInstanceUrl);
								
								ApplicationHideCallable ah = new ApplicationHideCallable.Builder()
										.withApplicationInstanceURL(appInstanceUrl)
										.build();
								
								ah.call();
								
								ApplicationStopCallable as = new ApplicationStopCallable.Builder()
										.withApplicationInstanceURL(appInstanceUrl)										
										.build();
								as.call();

								
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
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
    	
//    	wol.wakeup("10:dd:b1:c9:00:e4", 10, null, "uuid:4248037a-d4b5-6c7b-0000-000062dc019d::urn:dial-multiscreen-org:service:dial:1", new DeviceWOLCallback() {
//
//			@Override
//			public void onDeviceWOLSucceed() {
//				System.out.println("onDeviceWOLSucceed");
//			}
//
//			@Override
//			public void onDeviceWOLFail() {
//				System.out.println("onDeviceWOLFail");
//			}});
    	
    	synchronized(Thread.currentThread()) {
    		Thread.currentThread().wait();
    	}
    	//ds.close();
    	//Thread.sleep(13000);
    }
}
