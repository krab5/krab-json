package org.krab.json.path;

final class AnyStringValueMatcher extends StringValueMatcher {
	
	public AnyStringValueMatcher() {}

	@Override
	public boolean matches(String input) {
		return true;
	}
	
	@Override
	public String toString() {
		return "<any string>";
	}

}
