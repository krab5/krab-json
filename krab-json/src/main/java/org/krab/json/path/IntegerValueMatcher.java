package org.krab.json.path;

import java.util.Optional;

import org.krab.json.JsonInteger;
import org.krab.json.JsonValue;

final class IntegerValueMatcher implements ValueMatcher {
	private final Optional<Integer> lower, upper;
	
	public IntegerValueMatcher(Optional<Integer> lower, Optional<Integer> upper) {
		this.lower = lower;
		this.upper = upper;
	}
	
	@Override
	public boolean matches(JsonValue value) {
		return (value instanceof JsonInteger intval) &&
			(this.lower.isEmpty() || this.lower.get() <= intval.getValue())
		 && (this.upper.isEmpty() || intval.getValue() <= this.upper.get());
	}
	
	@Override
	public String toString() {
		return "[" + (this.lower.isEmpty() ? "" : Integer.toString(this.lower.get())) + "-"
			 + (this.upper.isEmpty() ? "" : Integer.toString(this.upper.get())) + "]";
	}
	
	public static IntegerValueMatcher fromString(String input) {
		if (input.startsWith("[")) {
			if (!input.endsWith("]"))
				throw new IllegalArgumentException("integer ranges are surrounded by square brackets [..-..]");
			
			int carretid = input.indexOf('-');
			
			if (carretid < 0)
				throw new IllegalArgumentException("integer ranges must contain a carret [..-..]");
			
			String left = input.substring(1, carretid);
			String right = input.substring(carretid + 1, input.length() - 1);
			
			return new IntegerValueMatcher(
					left.equals("*") || left.isEmpty() ? Optional.empty() : Optional.of(Integer.parseInt(left)),
					right.equals("*") || right.isEmpty() ? Optional.empty() : Optional.of(Integer.parseInt(right))
				);
		} else {
			int value = Integer.parseInt(input);
			return new IntegerValueMatcher(Optional.of(value), Optional.of(value));
		}
	}
	
}
