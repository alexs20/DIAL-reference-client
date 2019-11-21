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
public class UPnPDescriptionParser {

	public static UPnPDescriptionResponce parse(String message) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		XPath xpath = XPathFactory.newInstance().newXPath();
		InputSource inputSource = new InputSource(new StringReader(message));
		UPnPDescriptionResponce ret = new UPnPDescriptionResponce();
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
