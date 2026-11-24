package org.krab.json.path;

import org.krab.json.JsonValue;

sealed interface ValueMatcher permits AnyValueMatcher, BooleanValueMatcher,
		IntegerValueMatcher, DecimalValueMatcher, StringValueMatcher {
	boolean matches(JsonValue value);
	
	public static ValueMatcher fromString(String input, FragmentType expectedType) {
		if (input.startsWith("[[")) {
			if (expectedType != FragmentType.DECIMAL && expectedType != FragmentType.ANY_TYPE)
				throw new IllegalArgumentException("unepxected decimal value matcher");
			return DecimalValueMatcher.fromString(input);
		} else if (input.startsWith("[")) {
			if (expectedType != FragmentType.INTEGER && expectedType != FragmentType.ANY_TYPE)
				throw new IllegalArgumentException("unexpected integer value matcher");
			return IntegerValueMatcher.fromString(input);
		} else if (input.startsWith("'") || input.startsWith("\"") || input.startsWith("!")) {
			if (expectedType != FragmentType.STRING && expectedType != FragmentType.ANY_TYPE)
				throw new IllegalArgumentException("unexpected string value matcher");
			return StringValueMatcher.fromString(input, true);
		} else if (input.equals("true") || input.equals("false")) {
			if (expectedType != FragmentType.BOOLEAN && expectedType != FragmentType.ANY_TYPE)
				throw new IllegalArgumentException("unexpected boolean value matcher");
			return BooleanValueMatcher.fromString(input);
		} else if (Character.isDigit(input.charAt(0)) || input.charAt(0) == '-') {
			if (input.indexOf('.') >= 0)
				return DecimalValueMatcher.fromString(input);
			else
				return IntegerValueMatcher.fromString(input);
		} else if (input.equals("*")) {
			return new AnyValueMatcher(expectedType);
		} else {
			throw new IllegalArgumentException("value '" + input + "' is ill-formed");
		}
	}
}
