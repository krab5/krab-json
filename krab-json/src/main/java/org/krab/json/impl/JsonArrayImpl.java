package org.krab.json.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.krab.json.JsonArray;
import org.krab.json.JsonValue;
import org.krab.json.util.JsonVisitor;

class JsonArrayImpl implements JsonArray {
	private List<JsonValue> content = new ArrayList<>();
	
	public JsonArrayImpl() {}
	public JsonArrayImpl(Collection<? extends JsonValue> content) {
		this.content.addAll(content);
	}
	
	@Override
	public int getSize() {
		return this.content.size();
	}
	
	@Override
	public JsonValue get(int i) {
		if (i < 0 || i >= this.content.size())
			return new JsonNullImpl();
		else
			return this.content.get(i);
	}
	
	@Override
	public void add(JsonValue value) {
		this.content.add(value);
	}
	
	@Override
	public <P> void accept(JsonVisitor<P> visitor, P param) throws Throwable {
		visitor.visitArray(param);
		for (JsonValue value : this.content)
			value.accept(visitor, param);
		visitor.visitEndArray(param);
	}
	
	@Override
	public boolean equals(Object other) {
		return this == other || (other != null && other instanceof JsonArray arr &&
				this.equals(arr));
	}
	
	public boolean equals(JsonArray other) {
		boolean result = other.getSize() == this.getSize();
		
		for (int i = 0; i < this.getSize() && result; i++)
			result = result && (this.get(i).equals(other.get(i)));
		
		return result;
	}
	
	@Override
	public String toString() {
		return "[" + this.content.stream().map(JsonValue::toString).collect(Collectors.joining(", ")) + "]";
	}
	
	@Override
	public Iterator<JsonValue> iterator() {
		return this.content.iterator();
	}
}
