package org.krab.json.impl;

import org.krab.json.JsonNull;
import org.krab.json.util.JsonVisitor;

class JsonNullImpl implements JsonNull {
	@Override
	public <P> void accept(JsonVisitor<P> visitor, P param) throws Throwable {
		visitor.visitNull(param);
	}
	
	@Override
	public boolean equals(Object other) {
		return this == other || (other != null && other instanceof JsonNull);
	}
	
	@Override
	public String toString() {
		return "<<null>>";
	}
}
