package org.krab.json.annotation.processor.impl;

import java.util.function.Function;

import javax.lang.model.element.PackageElement;

import org.krab.json.JsonBoolean;
import org.krab.json.JsonDecimal;
import org.krab.json.JsonInteger;
import org.krab.json.JsonString;
import org.krab.json.annotation.processor.builder.FileBuilder;
import org.krab.json.annotation.processor.builder.MethodBuilder;

public final class ScalarType implements FieldType {
	public static enum Type {
		STRING, INTEGER, DECIMAL, BOOLEAN
	}
	
	private final Type type;
	
	public ScalarType(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return this.type;
	}
	
	public String preciseJsonType() {
		return switch (this.type) {
			case STRING: yield JsonString.class.getSimpleName();
			case INTEGER: yield JsonInteger.class.getSimpleName();
			case DECIMAL: yield JsonDecimal.class.getSimpleName();
			case BOOLEAN: yield JsonBoolean.class.getSimpleName();
		};
	}
	
	@Override
	public String asJavaCode() {
		return switch (this.type) {
			case STRING: yield "String";
			case INTEGER: yield "int";
			case DECIMAL: yield "double";
			case BOOLEAN: yield "boolean";
		};
	}
	
	public String boxedType() {
		return switch (this.type) {
		case STRING: yield "String";
		case INTEGER: yield "Integer";
		case DECIMAL: yield "Double";
		case BOOLEAN: yield "Boolean";
	};
	}
	
	@Override
	public void addImports(FileBuilder builder, PackageElement currentPackage) {}
	
	@Override
	public void addFromJsonInstructions(MethodBuilder builder, int level, String source, Function<String, String> setter) {
		builder.addInstruction(level,
				setter.apply(String.format("Json.valueAs(%s, %s.class).getValue()", source, this.preciseJsonType())) + ";");
	}
	
	@Override
	public void addToJsonInstructions(MethodBuilder builder, int level, String factory, String source,
			Function<String, String> setter) {
		builder.addInstruction(level, setter.apply(String.format(
				"%s.makeValue(%s)"
				, factory, source)) + ";");
	}
}

