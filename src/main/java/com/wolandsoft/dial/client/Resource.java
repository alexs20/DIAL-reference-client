package com.wolandsoft.dial.client;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Scanner;

public class Resource {

	public static String read(String name, Object ... args) {
		InputStream is = Resource.class.getResourceAsStream("/" + name);
		try (Scanner s = new Scanner(is, "utf-8")) {
			return MessageFormat.format(s.useDelimiter("\\Z").next(), args);
		}
	}
}
