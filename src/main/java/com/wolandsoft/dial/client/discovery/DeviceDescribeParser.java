package com.wolandsoft.dial.client.discovery;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
public class DeviceDescribeParser {

	public static DeviceDescribeResponce parse(String message) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		XPath xpath = XPathFactory.newInstance().newXPath();
		InputSource inputSource = new InputSource(new StringReader(message));
		DeviceDescribeResponce ret = new DeviceDescribeResponce();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document xml = db.parse(inputSource);
			ret.setFriendlyName(
					(String) xpath.evaluate("/*[local-name()='root']/*[local-name()='device']/*[local-name()='friendlyName']", xml, XPathConstants.STRING));
			ret.setManufacturer(
					(String) xpath.evaluate("/*[local-name()='root']/*[local-name()='device']/*[local-name()='manufacturer']", xml, XPathConstants.STRING));
			return ret;
		} catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
			return null;
		}
	}

}
