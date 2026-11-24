package org.krab.json.annotation.processor.impl;

import java.util.function.Function;

import javax.lang.model.element.PackageElement;

import org.krab.json.annotation.processor.builder.FileBuilder;
import org.krab.json.annotation.processor.builder.MethodBuilder;

public final class ArrayType extends ContainerType {
	public ArrayType(FieldType innerType) {
		super(innerType);
	}
	
	@Override
	public String asJavaCode() {
		return this.getInnerType().asJavaCode() + "[]";
	}
	
	@Override
	public void addImports(FileBuilder builder, PackageElement currentPackage) {
		this.getInnerType().addImports(builder, currentPackage);
	}
	
	@Override
	public void addFromJsonInstructions(MethodBuilder builder, int level, String source, Function<String,String> setter) {
		int uniqueId = builder.getInstructionNumber();
		builder.addInstruction(level, String.format(
				"JsonArray jsonArray%d = Json.valueAs(%s, JsonArray.class);"
				, uniqueId, source))
		       .addInstruction(level, String.format(
				"%1$s[] array%2$d = new %1$s[jsonArray%2$d.getSize()];"
				, this.getInnerType().asJavaCode(), uniqueId))
		       .addInstruction(level, String.format(
				"for (int i%d = 0; i < array%d.length; i++) {"
				, level, uniqueId));
	
		this.getInnerType().addFromJsonInstructions(builder, level + 1, 
				String.format("jsonArray%d.get(i%d)", uniqueId, level),
				(String arg) -> String.format("array%d[i%d] = %s", uniqueId, level, arg));
		
		builder.addInstruction(level, "}")
		       .addInstruction(level, setter.apply("array" + uniqueId) + ";")
		       .addNewline();
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
