package org.krab.json.util;

import org.krab.json.*;

public class JsonArrayBuilder {
	@FunctionalInterface
	public static interface ContinueArrayBuilding {
		JsonArrayBuilder doContinue(JsonArrayBuilder builder);
	}
	
	private JsonFactory factory;
	private JsonArray array;
	
	public JsonArrayBuilder(JsonFactory factory) {
		this.factory = factory;
		this.array = this.factory.createArray();
	}
	
	public JsonArray build() {
		return this.array;
	}
	
	public JsonArrayBuilder add(int value) {
		this.array.add(factory.makeValue(value));
		return this;
	}
	
	public JsonArrayBuilder add(double value) {
		this.array.add(factory.makeValue(value));
		return this;
	}
	
	public JsonArrayBuilder add(boolean value) {
		this.array.add(factory.makeValue(value));
		return this;
	}
	
	public JsonArrayBuilder add(String value) {
		this.array.add(factory.makeValue(value));
		return this;
	}
	
	public JsonArrayBuilder addObject(JsonObjectBuilder.ContinueObjectBuilding cont) {
		this.array.add(cont.doContinue(new JsonObjectBuilder(factory)).build());
		return this;
	}
	
	public JsonArrayBuilder addArray(ContinueArrayBuilding cont) {
		this.array.add(cont.doContinue(new JsonArrayBuilder(factory)).build());
		return this;
	}
}
