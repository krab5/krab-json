package org.krab.json.path;

import org.krab.json.*;

final class AnyValueMatcher implements ValueMatcher {
	private final FragmentType expectedType;
	
	public AnyValueMatcher(FragmentType type) {
		this.expectedType = type;
	}

	@Override
	public boolean matches(JsonValue value) {
		return switch (this.expectedType) {
			case INTEGER: yield (value instanceof JsonInteger);
			case DECIMAL: yield (value instanceof JsonDecimal);
			case BOOLEAN: yield (value instanceof JsonBoolean);
			case STRING: yield (value instanceof JsonString);
			case ANY_TYPE: yield true;
		};
	}
	
	@Override
	public String toString() {
		return "<any value>" + (this.expectedType == FragmentType.ANY_TYPE ? "" 
				: "::" + this.expectedType.toString());
	}

}
