package org.krab.json.path;

import java.util.Collection;

import org.krab.json.JsonValue;

sealed interface PathFragment permits ArrayPathFragment, ObjectPathFragment, 
		ValuePathFragment {
	Collection<? extends JsonValue> matches(JsonValue value);
	@Override String toString();
	
	public static PathFragment fromString(String input) {
		if (input.startsWith("=") || input.startsWith("::")) { // value
			return ValuePathFragment.fromString(input);
		} else if (input.startsWith("[")) { // array
			return ArrayPathFragment.fromString(input);
		} else {
			return ObjectPathFragment.fromString(input);
		}
	}
}
