package com.wolandsoft.dial.client.discovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.wolandsoft.dial.client.discovery.MSearchParser;
import com.wolandsoft.dial.client.discovery.MSearchResponce;

public class MSearchParserTest
{
	@Before
	public void onBefore() throws Exception {

	}

	@After
	public void onAfter() throws Exception {

	}


	@Test
    public void testApp() throws MalformedURLException
    {
        String message = 
        		"HTTP/1.1 200 OK\n" + 
        		"LOCATION: http://192.168.1.1:52235/dd.xml\n" + 
        		"CACHE-CONTROL: max-age=1800\n" + 
        		"EXT:\n" + 
        		"BOOTID.UPNP.ORG: 1\n" + 
        		"SERVER: OS/version UPnP/1.1 product/version\n" + 
        		"USN: device UUID\n" + 
        		"ST: urn:dial-multiscreen-org:service:dial:1\n" + 
        		"WAKEUP: MAC=10:dd:b1:c9:00:e4;Timeout=10\n";
        MSearchResponce resp = MSearchParser.parse(message);
        assertNotNull(resp);
        assertEquals(resp.getLocation(), new URL("http://192.168.1.1:52235/dd.xml"));
        assertEquals(resp.getUsn(), "device UUID");
        assertEquals(resp.getWakeupMac(), "10:dd:b1:c9:00:e4");
        assertEquals(resp.getWakeupTimeout(), Integer.valueOf(10));
        
    }
}
