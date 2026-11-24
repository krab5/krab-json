package org.krab.json.impl;

import java.io.Reader;
import java.util.Collection;
import java.util.Map;

import org.krab.json.JsonArray;
import org.krab.json.JsonBoolean;
import org.krab.json.JsonDecimal;
import org.krab.json.JsonFactory;
import org.krab.json.JsonInteger;
import org.krab.json.JsonObject;
import org.krab.json.JsonString;
import org.krab.json.JsonValue;
import org.krab.json.impl.parsing.Parser;
import org.krab.json.io.JsonParser;

public class JsonFactoryImpl implements JsonFactory {
	
	public JsonFactoryImpl() {}

	@Override
	public JsonInteger makeValue(int value) {
		return new JsonIntegerImpl(value);
	}
	
	@Override
	public JsonDecimal makeValue(double value) {
		return new JsonDecimalImpl(value);
	}

	@Override
	public JsonBoolean makeValue(boolean value) {
		return new JsonBooleanImpl(value);
	}

	@Override
	public JsonString makeValue(String value) {
		return new JsonStringImpl(value);
	}
	
	@Override
	public JsonValue makeNull() {
		return new JsonNullImpl();
	}

	@Override
	public JsonObject createObject() {
		return new JsonObjectImpl();
	}

	@Override
	public JsonObject createObject(Map<String, ? extends JsonValue> input) {
		return new JsonObjectImpl(input);
	}

	@Override
	public JsonArray createArray() {
		return new JsonArrayImpl();
	}

	@Override
	public JsonArray createArray(Collection<? extends JsonValue> input) {
		return new JsonArrayImpl(input);
	}
	
	@Override
	public JsonParser createParser(String source, Reader reader) {
		return new Parser(this, source, reader);
	}

}
