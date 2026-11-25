package org.krab.json.path;

import java.util.Optional;

import org.krab.json.JsonDecimal;
import org.krab.json.JsonValue;

final class DecimalValueMatcher implements ValueMatcher {
	private final static double EPSILON = 1e-6;
	private final Optional<Double> lower, upper;
	
	public DecimalValueMatcher(Optional<Double> lower, Optional<Double> upper) {
		this.lower = lower;
		this.upper = upper;
	}
	
	@Override
	public boolean matches(JsonValue value) {
		return (value instanceof JsonDecimal decval) &&
			(this.lower.isEmpty() || this.lower.get() <= decval.getValue())
		 && (this.upper.isEmpty() || decval.getValue() <= this.upper.get());
	}
	
	@Override
	public String toString() {
		return "[[" + (this.lower.isEmpty() ? "" : Double.toString(this.lower.get())) + "-"
			 + (this.upper.isEmpty() ? "" : Double.toString(this.upper.get())) + "]]";
	}
	
	public static DecimalValueMatcher fromString(String input) {
		if (input.startsWith("[[")) {
			if (!input.endsWith("]]"))
				throw new IllegalArgumentException("double ranges are surrounded by double square brackets [[..-..]]");
			
			int carretid = input.indexOf('-');
			
			if (carretid < 0)
				throw new IllegalArgumentException("double ranges must contain a carret [[..-..]]");
			
			String left = input.substring(2, carretid);
			String right = input.substring(carretid + 1, input.length() - 2);
			
			return new DecimalValueMatcher(
					left.equals("*") || left.isEmpty() ? Optional.empty() : Optional.of(Double.parseDouble(left)),
					right.equals("*") || right.isEmpty() ? Optional.empty() : Optional.of(Double.parseDouble(right))
				);
		} else {
			double value = Double.parseDouble(input);
			return new DecimalValueMatcher(Optional.of(value - EPSILON), Optional.of(value + EPSILON));
		}
	}

}
