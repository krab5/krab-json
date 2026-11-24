package org.krab.json.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.krab.json.JsonObject;
import org.krab.json.JsonValue;
import org.krab.json.util.JsonVisitor;

class JsonObjectImpl implements JsonObject {
	private Map<String, JsonValue> content = new HashMap<>();
	private List<String> keyOrder = new ArrayList<>();
	
	public JsonObjectImpl() {}
	public JsonObjectImpl(Map<String, ? extends JsonValue> content) {
		this.content.putAll(content);
		this.keyOrder.addAll(content.keySet());
	}
	
	@Override
	public boolean has(String key) {
		return this.content.containsKey(key);
	}
	
	@Override
	public JsonValue get(String key) {
		if (this.content.containsKey(key))
			return this.content.get(key);
		else
			return new JsonNullImpl();
	}
	
	@Override
	public List<String> keyList() {
		return List.copyOf(this.keyOrder);
	}
	
	@Override
	public void add(String key, JsonValue value) {
		if (!this.content.containsKey(key))
			this.keyOrder.add(key);
		this.content.put(key, value);
	}
	
	@Override
	public <P> void accept(JsonVisitor<P> visitor, P param) throws Throwable {
		visitor.visitObject(param);
		for (String key : this.keyOrder) {
			visitor.visitKey(param, key);
			this.content.get(key).accept(visitor, param);
		}
		visitor.visitEndObject(param);
	}
	
	@Override
	public boolean equals(Object other) {
		return this == other || (other != null && other instanceof JsonObject obj &&
				this.equals(obj));
	}
	
	public boolean equals(JsonObject other) {
		boolean result = true;
		
		for (String key1 : this.keyList())
			result = result && (this.get(key1).equals(other.get(key1)));
		for (String key2 : other.keyList())
			result = result && (this.get(key2).equals(other.get(key2)));
		
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{ ");
		
		Iterator<String> it = this.keyOrder.iterator();
		while (it.hasNext()) {
			String key = it.next();
			builder.append('"');
			builder.append(key);
			builder.append('"');
			builder.append(" : ");
			builder.append(this.get(key).toString());
			if (it.hasNext())
				builder.append(", ");
		}
		
		builder.append(" }");
		return builder.toString();
	}
}
