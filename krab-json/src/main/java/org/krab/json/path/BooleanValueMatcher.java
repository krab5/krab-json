package org.krab.json.path;

import org.krab.json.JsonBoolean;
import org.krab.json.JsonValue;

final class BooleanValueMatcher implements ValueMatcher {
	private final boolean expected;
	
	public BooleanValueMatcher(boolean expected) {
		this.expected = expected;
	}
	
	@Override
	public boolean matches(JsonValue value) {
		return value instanceof JsonBoolean bool && bool.getValue() == this.expected;
	}
	
	@Override
	public String toString() {
		return Boolean.toString(this.expected);
	}
	
	public static BooleanValueMatcher fromString(String input) {
		return new BooleanValueMatcher(Boolean.parseBoolean(input));
	}

}
