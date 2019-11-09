package com.wolandsoft.dial.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import com.wolandsoft.dial.client.discovery.MSearchService;
import com.wolandsoft.dial.client.discovery.MSearchServiceListener;
import com.wolandsoft.dial.client.discovery.DeviceDescribeListener;
import com.wolandsoft.dial.client.discovery.DeviceDescribeService;
import com.wolandsoft.dial.client.discovery.DiscoveredDevice;
import com.wolandsoft.dial.client.discovery.MSearchResponce;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, URISyntaxException, InterruptedException
    {

    	DeviceDescribeService dds = new DeviceDescribeService.Builder()
    			.withListener(new DeviceDescribeListener() {

					@Override
					public void onDeviceDiscovered(DiscoveredDevice device) {
						System.out.println("onDeviceDiscovered " + device);
						
					}

					@Override
					public void onDeviceRemoved(DiscoveredDevice device) {
						System.out.println("onDeviceRemoved " + device);
						
					}})
    			.build();
    	
    	//System.out.println( Resource.read("m-search.msg", System.getProperty("os.name"), System.getProperty("os.version"), "test", "test") );
    	MSearchService ds = new MSearchService.Builder()
    			.withListener(dds)
    			.build();
    	
    	synchronized(Thread.currentThread()) {
    		Thread.currentThread().wait();
    	}
    	//ds.close();
    	//Thread.sleep(13000);
    }
}
