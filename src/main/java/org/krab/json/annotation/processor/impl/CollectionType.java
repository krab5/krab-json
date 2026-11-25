package org.krab.json.annotation.processor.impl;

import java.util.function.Function;

import javax.lang.model.element.PackageElement;

import org.krab.json.annotation.processor.builder.FileBuilder;
import org.krab.json.annotation.processor.builder.MethodBuilder;

public final class CollectionType extends ContainerType {
	public static enum Type {
		LIST, SET;
		
		public String asJavaCode() {
			return switch (this) {
				case LIST -> "List";
				case SET -> "Set";
			};
		}
		
		public String defaultImplementation() {
			return switch (this) {
				case LIST -> "ArrayList";
				case SET -> "HashSet";
			};
		}
	}
	
	private final Type collectionType;
	
	public CollectionType(Type collType, FieldType innerType) {
		super(innerType);
		this.collectionType = collType;
	}
	
	public Type getCollectionType() {
		return this.collectionType;
	}
	
	@Override
	public void addImports(FileBuilder builder, PackageElement currentPackage) {
		this.getInnerType().addImports(builder, currentPackage);
		
		builder.addImport("java.util." + this.collectionType.asJavaCode())
		       .addImport("java.util." + this.collectionType.defaultImplementation());
	}

	@Override
	public String asJavaCode() {
		if (this.getInnerType() instanceof ScalarType)
			return this.collectionType.asJavaCode() + "<" + ((ScalarType) this.getInnerType()).boxedType() + ">";
		else
			return this.collectionType.asJavaCode() + "<" + this.getInnerType().asJavaCode() + ">";
	}
	
	@Override
	public void addFromJsonInstructions(MethodBuilder builder, int level, String source, Function<String,String> setter) {
		int uniqueId = builder.getInstructionNumber();
		builder.addInstruction(level, String.format(
				"JsonArray jsonArray%d = Json.valueAs(%s, JsonArray.class);"
				, uniqueId, source));
		builder.addInstruction(level, String.format(
				"%s list%d = new %s<>();"
				, this.asJavaCode()
				, uniqueId
				, this.collectionType.defaultImplementation()));
		
		builder.addInstruction(level, String.format(
				"for (JsonValue e%d : jsonArray%d) {"
				, level, uniqueId));
		
		this.getInnerType().addFromJsonInstructions(builder, level + 1, "e" + level,
				(String arg) -> String.format("list%d.add(%s)", uniqueId, arg));
		
		builder.addInstruction(level, "}");
		
		builder.addInstruction(level, setter.apply("list" + uniqueId) + ";").addNewline();
	}
	
	@Override
	public void addToJsonInstructions(MethodBuilder builder, int level, String factory,
			String source, Function<String,String> setter) {
		int uniqueId = builder.getInstructionNumber();
		builder.addInstruction(level, String.format(
				"JsonArray array%d = %s.createArray();"
				, uniqueId, factory));
		builder.addInstruction(level, String.format(
				"for (%s e%d : %s) {"
				, this.getInnerType().asJavaCode(), level, source));
		
		this.getInnerType().addToJsonInstructions(builder, level + 1, factory, "e" + level,
				(String arg) -> String.format("array%d.add(%s)", uniqueId, arg));
		
		builder.addInstruction(level, "}")
		       .addInstruction(level, setter.apply("array" + uniqueId) + ";")
		       .addNewline();
	}
}
