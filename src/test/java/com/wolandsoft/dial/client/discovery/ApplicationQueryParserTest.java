package com.wolandsoft.dial.client.discovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.wolandsoft.dial.client.rest.ApplicationQueryParser;
import com.wolandsoft.dial.client.rest.ApplicationQueryResponce;

public class ApplicationQueryParserTest {
	@Before
	public void onBefore() throws Exception {

	}

	@After
	public void onAfter() throws Exception {

	}

	@Test
	public void testApp() throws MalformedURLException {
		String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<service xmlns=\"urn:dial-multiscreen-org:schemas:dial\" dialVer=\"1.7\">\n" + 
				"<name>YouTube</name>\n" + 
				"<options allowStop=\"true\"/>\n" + 
				"<state>running</state>\n" + 
				"<link rel=\"run\" href=\"run\"/>\n" + 
				"</service>";
		ApplicationQueryResponce resp = ApplicationQueryParser.parse(message);
		assertNotNull(resp);
		assertEquals(resp.getDialVer(), "1.7");
		assertEquals(resp.getName(), "YouTube");
		assertEquals(resp.getAllowStop(), Boolean.valueOf(true));
		assertEquals(resp.getState(), "running");
		assertEquals(resp.getLinkHref(), "run");

	}
}
