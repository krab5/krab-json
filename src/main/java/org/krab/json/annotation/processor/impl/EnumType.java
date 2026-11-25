package org.krab.json.annotation.processor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import org.krab.json.JsonInteger;
import org.krab.json.JsonString;
import org.krab.json.annotation.JsonField;
import org.krab.json.annotation.JsonField.EnumMode;
import org.krab.json.annotation.processor.builder.FileBuilder;
import org.krab.json.annotation.processor.builder.MethodBuilder;

public final class EnumType implements FieldType {
	private final TypeElement enumType;
	private final JsonField.EnumMode enumMode;
	
	public EnumType(TypeElement enumType, JsonField.EnumMode enumMode) {
		this.enumType = enumType;
		this.enumMode = enumMode;
	}

	@Override
	public String asJavaCode() {
		return this.enumType.getSimpleName().toString();
	}

	@Override
	public void addImports(FileBuilder builder, PackageElement currentPackage) {
		if (!FieldType.getPackage(this.enumType).equals(currentPackage))
			builder.addImport(this.enumType.getQualifiedName().toString());
	}

	@Override
	public void addFromJsonInstructions(MethodBuilder builder, int level, String source,
			Function<String, String> setter) {
		int uniqueId = builder.getInstructionNumber();
		
		if (this.enumMode == EnumMode.BY_NAME) {
			builder.addInstruction(level, String.format(
					"String arg%d = Json.valueAs(%s, JsonString.class).getValue();"
					, uniqueId, source));
			builder.addInstruction("try {");
			builder.addInstruction(level + 1, setter.apply(String.format(
					"%s.valueOf(arg%d)"
					, this.enumType.getQualifiedName(), uniqueId)) + ";");
			builder.addInstruction(level, "} catch (IllegalArgumentException e) {");
			builder.addInstruction(level + 1, String.format("""
					throw new JsonSchemaException(String.format("unexpected string '%%s' in JSON value"
							+ "(incompatible with associated enum type %s)", arg%d), e);
					""", this.enumType.getQualifiedName(), uniqueId));
			builder.addInstruction(level, "}");
		} else {
			builder.addInstruction(level, String.format(
					"int arg%d = Json.valueAs(%s, JsonInteger.class).getValue();"
					, uniqueId, source));
			builder.addInstruction(level, String.format(
					"if (arg%1$d >= 0 && arg%1$d <= %2$s.values().length)"
					, uniqueId, this.enumType.getQualifiedName()));
			builder.addInstruction(level + 1, setter.apply(String.format(
					"%s.values()[arg%d]"
					, this.enumType.getQualifiedName(), uniqueId)) + ";");
			builder.addInstruction(level, "else");
			builder.addInstruction(level + 1, String.format("""
					throw new JsonSchemaException(String.format("unexpected integer value %%d for enum type %s "
							+ "(enum type contains %%d values)", arg%d, %s.values().length));
					""", this.enumType.getQualifiedName(), uniqueId, this.enumType.getQualifiedName()));
		}
	}

	@Override
	public void addToJsonInstructions(MethodBuilder builder, int level, String factory, String source,
			Function<String, String> setter) {
		builder.addInstruction(level, setter.apply(String.format(
				"%s.makeValue(%s.%s())"
				, factory, source, this.enumMode == EnumMode.BY_NAME ? "name" : "ordinal")) + ";");
	}

}
