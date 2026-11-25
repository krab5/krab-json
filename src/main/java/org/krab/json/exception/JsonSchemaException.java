package org.krab.json.exception;

import org.krab.json.JsonValue;

public class JsonSchemaException extends Exception {

	private static final long serialVersionUID = -4890154113622803296L;
	
	public JsonSchemaException(String message) {
		super(message);
	}
	
	public JsonSchemaException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public JsonSchemaException(Class<? extends JsonValue> klass1, Class<? extends JsonValue> klass2) {
		this("trying to coerce a value of type %s and a value of type %s"
				.formatted(klass1.getSimpleName(), klass2.getSimpleName()));
	}
	
	public JsonSchemaException(JsonValue value1, JsonValue value2) {
		this(value1.getClass(), value2.getClass());
	}
	
	public JsonSchemaException(JsonValue value1, Class<? extends JsonValue> klass2) {
		this(value1.getClass(), klass2);
	}
}
