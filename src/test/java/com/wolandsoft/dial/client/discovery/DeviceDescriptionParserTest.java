package com.wolandsoft.dial.client.discovery;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.MalformedURLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DeviceDescriptionParserTest {
	@Before
	public void onBefore() throws Exception {

	}

	@After
	public void onAfter() throws Exception {

	}

	@Test
	public void testApp() throws MalformedURLException {
		String message = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?><root xmlns=\"urn:schemas-upnp-org:device-1-0\"><specVersion><major>1</major><minor>0</minor></specVersion><device><deviceType>urn:dial-multiscreen-org:device:dial:1</deviceType><friendlyName>Fire TV 1</friendlyName><manufacturer>Amazon</manufacturer><modelName>AFTMM</modelName><UDN>uuid:4248037a-d4b5-6c7b-0000-000062dc019d</UDN><serviceList><service><serviceType>urn:dial-multiscreen-org:service:dial:1</serviceType><serviceId>urn:dial-multiscreen-org:serviceId:dial</serviceId><SCPDURL>/upnp/dev/4248037a-d4b5-6c7b-0000-000062dc019d/svc/dial-multiscreen-org/dial/desc</SCPDURL><controlURL>/upnp/dev/4248037a-d4b5-6c7b-0000-000062dc019d/svc/dial-multiscreen-org/dial/action</controlURL><eventSubURL>/upnp/dev/4248037a-d4b5-6c7b-0000-000062dc019d/svc/dial-multiscreen-org/dial/event</eventSubURL></service></serviceList></device></root>";
		UPnPDescriptionResponce resp = UPnPDescriptionParser.parse(message);
		assertNotNull(resp);
		assertEquals(resp.getFriendlyName(), "Fire TV 1");
		assertEquals(resp.getManufacturer(), "Amazon");

	}
}
