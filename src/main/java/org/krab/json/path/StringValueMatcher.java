package org.krab.json.path;

import org.krab.json.JsonString;
import org.krab.json.JsonValue;

sealed abstract class StringValueMatcher implements ValueMatcher
		permits AnyStringValueMatcher, GlobStringValueMatcher, RegexStringValueMatcher {
	
	public abstract boolean matches(String input);
	
	@Override
	public boolean matches(JsonValue value) {
		return (value instanceof JsonString str) && this.matches(str.getValue());
	}
	
	public static StringValueMatcher fromString(String input, boolean quoted) {
		if (input.charAt(0) == RegexStringValueMatcher.BEGIN_REGEX) {
			if (input.charAt(input.length() - 1) != RegexStringValueMatcher.END_REGEX)
				throw new IllegalArgumentException("regex matching fragments must be surrounded by '" 
					+ RegexStringValueMatcher.BEGIN_REGEX + "','" + RegexStringValueMatcher.END_REGEX + "'");
			return new RegexStringValueMatcher(input.substring(1, input.length() - 1));
		} else if (quoted && (input.charAt(0) == '"' || input.charAt(0) == '\'')) {
			if (input.charAt(input.length() - 1) != input.charAt(0))
				throw new IllegalArgumentException("expected string to be surrounded by single or double quotes");
			return GlobStringValueMatcher.fromString(
					Utils.unescape(input.substring(1, input.length() - 1)));
		} else if (input.equals("*")) {
			return new AnyStringValueMatcher();
		} else if (!quoted) {
			return GlobStringValueMatcher.fromString(input);
		} else {
			throw new IllegalArgumentException("expected string or regex");
		}
	}
}
