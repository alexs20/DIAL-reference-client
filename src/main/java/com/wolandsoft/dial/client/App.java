package com.wolandsoft.dial.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import com.wolandsoft.dial.client.discovery.DiscoveryService;
import com.wolandsoft.dial.client.discovery.DiscoveryServiceListener;
import com.wolandsoft.dial.client.discovery.SSDPResponce;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, URISyntaxException, InterruptedException
    {

    	//System.out.println( Resource.read("m-search.msg", System.getProperty("os.name"), System.getProperty("os.version"), "test", "test") );
    	DiscoveryService ds = new DiscoveryService.Builder()
    			.withListener(new DiscoveryServiceListener() {

					@Override
					public void onDeviceDiscovery(SSDPResponce message) {
						System.out.println(message);
						
					}})
    			.build();
    	
    	Thread.sleep(13000);
    	ds.close();
    	Thread.sleep(13000);
    }
}
