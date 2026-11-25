package org.krab.json.util;

import org.krab.json.*;

public class JsonObjectBuilder {
	@FunctionalInterface
	public static interface ContinueObjectBuilding {
		JsonObjectBuilder doContinue(JsonObjectBuilder builder);
	}
	
	private JsonFactory factory;
	private JsonObject object;
	
	public JsonObjectBuilder(JsonFactory factory) {
		this.factory = factory;
		this.object = this.factory.createObject();
	}
	
	public JsonObject build() {
		return this.object;
	}
	
	public JsonObjectBuilder add(String key, int value) {
		this.object.add(key, this.factory.makeValue(value));
		return this;
	}
	
	public JsonObjectBuilder add(String key, double value) {
		this.object.add(key, this.factory.makeValue(value));
		return this;
	}
	
	public JsonObjectBuilder add(String key, boolean value) {
		this.object.add(key, this.factory.makeValue(value));
		return this;
	}
	
	public JsonObjectBuilder add(String key, String value) {
		this.object.add(key, this.factory.makeValue(value));
		return this;
	}
	
	public JsonObjectBuilder addObject(String key, ContinueObjectBuilding cont) {
		this.object.add(key, cont.doContinue(new JsonObjectBuilder(factory)).build());
		return this;
	}
	
	public JsonObjectBuilder addArray(String key, JsonArrayBuilder.ContinueArrayBuilding cont) {
		this.object.add(key, cont.doContinue(new JsonArrayBuilder(factory)).build());
		return this;
	}
}
