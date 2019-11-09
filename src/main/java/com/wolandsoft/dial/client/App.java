package com.wolandsoft.dial.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import com.wolandsoft.dial.client.discovery.SSDPMSearchService;
import com.wolandsoft.dial.client.discovery.SSDPMSearchListener;
import com.wolandsoft.dial.client.discovery.UPnPDescriptionListener;
import com.wolandsoft.dial.client.discovery.UPnPDescriptionService;
import com.wolandsoft.dial.client.rest.ApplicationQueryResponce;
import com.wolandsoft.dial.client.rest.ApplicationQueryService;
import com.wolandsoft.dial.client.discovery.DiscoveredDevice;
import com.wolandsoft.dial.client.discovery.SSDPMSearchResponce;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, URISyntaxException, InterruptedException
    {

    	ApplicationQueryService appQuery = new ApplicationQueryService.Builder().build();
    	
    	
    	UPnPDescriptionService dds = new UPnPDescriptionService.Builder()
    			.withListener(new UPnPDescriptionListener() {

					@Override
					public void onDeviceDiscovered(DiscoveredDevice device) {
						System.out.println("onDeviceDiscovered " + device);
						try {
							ApplicationQueryResponce aqr = appQuery.query(device.getApplicationURL(), "YouTube").get();
							System.out.println("appQuery " + aqr);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onDeviceRemoved(DiscoveredDevice device) {
						System.out.println("onDeviceRemoved " + device);
						
					}})
    			.build();
    	
    	//System.out.println( Resource.read("m-search.msg", System.getProperty("os.name"), System.getProperty("os.version"), "test", "test") );
    	SSDPMSearchService ds = new SSDPMSearchService.Builder()
    			.withListener(dds)
    			.build();
    	
    	synchronized(Thread.currentThread()) {
    		Thread.currentThread().wait();
    	}
    	//ds.close();
    	//Thread.sleep(13000);
    }
}
