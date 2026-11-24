package org.krab.json.util;

import org.krab.json.*;

public abstract class JsonComparator {
	private JsonComparator() {}
	
	public static boolean jsonEquals(JsonValue val1, JsonValue val2) {
		return val1.equals(val2);
	}
}
