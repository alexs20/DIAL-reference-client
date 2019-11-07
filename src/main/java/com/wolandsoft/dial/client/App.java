package com.wolandsoft.dial.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, URISyntaxException, InterruptedException
    {

    	//System.out.println( Resource.read("m-search.msg", System.getProperty("os.name"), System.getProperty("os.version"), "test", "test") );
    	DiscoveryService ds = new DiscoveryService("x", "y");
    	
    	Thread.sleep(99999);
    }
}
