package org.krab.json.impl;

import org.krab.json.JsonString;
import org.krab.json.util.JsonVisitor;

class JsonStringImpl implements JsonString {
	private String value;
	
	public JsonStringImpl(String value) {
		this.value = value;
	}
	
	@Override
	public String getValue() {
		return this.value;
	}
	
	@Override
	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public <P> void accept(JsonVisitor<P> visitor, P param) throws Throwable {
		visitor.visitValue(param, this.value);
	}
	
	@Override
	public boolean equals(Object other) {
		return this == other || (other != null && other instanceof JsonString str &&
				this.getValue().equals(str.getValue()));
	}
	
	@Override
	public String toString() {
		return "\"" + this.value + "\"";
	}
}
