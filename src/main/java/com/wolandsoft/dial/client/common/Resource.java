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
package com.wolandsoft.dial.client.common;

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
