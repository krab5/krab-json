package org.krab.json.annotation.processor.impl;

import java.util.function.Function;

import javax.lang.model.element.PackageElement;

import org.krab.json.annotation.processor.builder.FileBuilder;
import org.krab.json.annotation.processor.builder.MethodBuilder;

public final class OptionalType extends ContainerType {
	public OptionalType(FieldType type) {
		super(type);
	}
	
	@Override
	public String asJavaCode() {
		if (this.getInnerType() instanceof ScalarType)
			return "Optional<" + ((ScalarType) this.getInnerType()).boxedType() + ">";
		else
			return "Optional<" + this.getInnerType().asJavaCode() + ">";
	}
	
	@Override
	public void addImports(FileBuilder builder, PackageElement currentPackage) {
		builder.addImport("java.util.Optional");
		this.getInnerType().addImports(builder, currentPackage);
	}
	
	@Override
	public void addFromJsonInstructions(MethodBuilder builder, int level, String source, Function<String,String> setter) {
		builder.addInstruction(level, String.format("if (!(%s instanceof JsonNull)) {", source));
		
		this.getInnerType().addFromJsonInstructions(builder, level + 1, source,
				(String s) -> setter.apply("Optional.of(" + s + ")"));
		
		builder.addInstruction(level, "}");
	}
	
	public void addToJsonInstructions(MethodBuilder builder, int level, String factory, String source,
			Function<String, String> setter) {
		builder.addInstruction(level, String.format("if (%s.isPresent()) {", source));
		
		this.getInnerType().addToJsonInstructions(builder, level + 1, factory, source + ".get()", setter);
		
		builder.addInstruction(level, "}");
	}
}
