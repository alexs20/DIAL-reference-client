package com.wolandsoft.dial.client.rest;

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

public class ApplicationQueryParser {
	public static ApplicationQueryResponce parse(String message) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		XPath xpath = XPathFactory.newInstance().newXPath();
		InputSource inputSource = new InputSource(new StringReader(message));
		ApplicationQueryResponce ret = new ApplicationQueryResponce();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document xml = db.parse(inputSource);
			ret.setDialVer(
					(String) xpath.evaluate("/*[local-name()='service']/@dialVer", xml, XPathConstants.STRING));
			ret.setName(
					(String) xpath.evaluate("/*[local-name()='service']/*[local-name()='name']", xml, XPathConstants.STRING));
			ret.setAllowStop(
					(Boolean) xpath.evaluate("/*[local-name()='service']/*[local-name()='options']/@allowStop", xml, XPathConstants.BOOLEAN));
			ret.setState(
					(String) xpath.evaluate("/*[local-name()='service']/*[local-name()='state']", xml, XPathConstants.STRING));
			return ret;
		} catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
			return null;
		}
	}
}
