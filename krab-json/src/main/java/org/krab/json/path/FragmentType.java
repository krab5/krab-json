package org.krab.json.path;

import org.krab.json.JsonBoolean;
import org.krab.json.JsonDecimal;
import org.krab.json.JsonInteger;
import org.krab.json.JsonString;
import org.krab.json.JsonValue;

enum FragmentType {
	ANY_TYPE, INTEGER, BOOLEAN, DECIMAL, STRING;
	
	public boolean checkType(JsonValue value) {
		return (this == ANY_TYPE)
			|| (this == INTEGER && value instanceof JsonInteger)
			|| (this == BOOLEAN && value instanceof JsonBoolean)
			|| (this == DECIMAL && value instanceof JsonDecimal)
			|| (this == STRING && value instanceof JsonString);
	}
	
	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
	
	public static FragmentType fromString(String input) {
		return switch (input) {
			case "string": yield STRING;
			case "integer": yield INTEGER;
			case "decimal": yield DECIMAL;
			case "boolean": yield BOOLEAN;
			case "*": yield ANY_TYPE;
			default: throw new IllegalArgumentException("unrecognized type " + input);
		};
	}
}
