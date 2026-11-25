package org.krab.json.impl;

import org.krab.json.JsonInteger;
import org.krab.json.util.JsonVisitor;

class JsonIntegerImpl implements JsonInteger {
	private int value;
	
	public JsonIntegerImpl(int value) {
		this.value = value;
	}
	
	@Override
	public int getValue() {
		return this.value;
	}
	
	@Override
	public void setValue(int value) {
		this.value = value;
	}
	
	@Override
	public <P> void accept(JsonVisitor<P> visitor, P param) throws Throwable {
		visitor.visitValue(param, this.value);
	}
	
	@Override
	public boolean equals(Object other) {
		return this == other || (other != null && other instanceof JsonInteger inte &&
				inte.getValue() == this.getValue());
	}
	
	@Override
	public String toString() {
		return Integer.toString(this.value);
	}
}
