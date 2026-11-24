package org.krab.json.impl;

import org.krab.json.JsonDecimal;
import org.krab.json.util.JsonVisitor;

class JsonDecimalImpl implements JsonDecimal {
	private double value;
	
	public JsonDecimalImpl(double value) {
		this.value = value;
	}
	
	@Override
	public double getValue() {
		return this.value;
	}

	@Override
	public void setValue(double value) {
		this.value = value;
	}
	
	@Override
	public <P> void accept(JsonVisitor<P> visitor, P param) throws Throwable {
		visitor.visitValue(param, this.value);
	}
	
	@Override
	public boolean equals(Object other) {
		return this == other || (other != null && other instanceof JsonDecimal dec &&
				Math.abs(dec.getValue() - this.getValue()) < 1e-6);
	}

	@Override
	public String toString() {
		return Double.toString(this.value);
	}
}
