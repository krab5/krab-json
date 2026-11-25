package org.krab.json.annotation.processor.impl;

import java.util.function.Function;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.krab.json.annotation.processor.builder.FileBuilder;
import org.krab.json.annotation.processor.builder.MethodBuilder;

public final class JsonifiableType implements FieldType {
	private final TypeMirror type;
	
	public JsonifiableType(TypeMirror type) {
		this.type = type;
	}
	
	public TypeMirror getTypeName() {
		return this.type;
	}
	
	@Override
	public void addImports(FileBuilder builder, PackageElement currentPackage) {
		if (!FieldType.getPackage(((DeclaredType) this.type).asElement()).equals(currentPackage)) {
			builder.addImport(((TypeElement) ((DeclaredType) this.type).asElement()).getQualifiedName().toString());
		}
	}
	
	@Override
	public String asJavaCode() {
		return this.type.toString();
	}
	
	@Override
	public void addFromJsonInstructions(MethodBuilder builder, int level, String source, Function<String, String> setter) {
		builder.addInstruction(level,
				setter.apply(String.format("%s.fromJson(Json.valueAs(%s, JsonObject.class))", 
						((DeclaredType) this.type).asElement().getSimpleName(), source)) + ";");
		//               ^^^^^^^^^^^^^ the type *must* be a declared type because to be a
		//                             jsonifiable one must implement the related interface
		//                             This is supposedly ensured by the annotation processor 
	}
	
	@Override
	public void addToJsonInstructions(MethodBuilder builder, int level, String factory, String source,
			Function<String,String> setter) {
		builder.addInstruction(level,
				setter.apply(String.format("%s.toJson()", source)) + ";");
	}
}
