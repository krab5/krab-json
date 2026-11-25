package org.krab.json.path;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class RegexStringValueMatcher extends StringValueMatcher {
	public static final char BEGIN_REGEX = '!';
	public static final char END_REGEX = '!';
	
	private final Pattern pattern;
	
	public RegexStringValueMatcher(Pattern pattern) {
		this.pattern = pattern;
	}
	
	public RegexStringValueMatcher(String pattern) {
		this.pattern = Pattern.compile(pattern);
	}

	@Override
	public boolean matches(String input) {
		Matcher m = this.pattern.matcher(input);
		return m.matches();
	}
	
	@Override
	public String toString() {
		return "" + BEGIN_REGEX + this.pattern.pattern() + END_REGEX;
	}

}
