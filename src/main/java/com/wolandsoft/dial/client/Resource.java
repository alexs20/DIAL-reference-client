package com.wolandsoft.dial.client;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Scanner;

public class Resource {

	public static String read(String name, Object ... args) {
		InputStream is = Resource.class.getResourceAsStream("/" + name);
		try (Scanner s = new Scanner(is, StandardCharsets.UTF_8.name())) {
			return MessageFormat.format(s.useDelimiter("\\Z").next(), args);
		}
	}
}
