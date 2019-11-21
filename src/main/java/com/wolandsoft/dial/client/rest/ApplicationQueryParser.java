/*
    Copyright © 2019 Alexander Shulgin
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
        http://www.apache.org/licenses/LICENSE-2.0
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/
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
			ret.setLinkHref(
					(String) xpath.evaluate("/*[local-name()='service']/*[local-name()='link']/@href", xml, XPathConstants.STRING));
			ret.setAdditionalData(
					(String) xpath.evaluate("/*[local-name()='service']/*[local-name()='additionalData']", xml, XPathConstants.STRING));
			return ret;
		} catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException e) {
			return null;
		}
	}
}
