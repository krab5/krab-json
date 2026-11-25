package org.krab.json.impl;

import org.krab.json.JsonBoolean;
import org.krab.json.util.JsonVisitor;

class JsonBooleanImpl implements JsonBoolean {
	private boolean value;
	
	public JsonBooleanImpl(boolean value) {
		this.value = value;
	}
	
	@Override
	public boolean getValue() {
		return this.value;
	}
	
	@Override
	public void setValue(boolean value) {
		this.value = value;
	}
	
	@Override
	public <P> void accept(JsonVisitor<P> visitor, P param) throws Throwable {
		visitor.visitValue(param, this.value);
	}
	
	@Override
	public boolean equals(Object other) {
		return this == other || (other != null && other instanceof JsonBoolean bool &&
				bool.getValue() == this.getValue());
	}
	
	@Override
	public String toString() {
		return "" + this.value;
	}
}
