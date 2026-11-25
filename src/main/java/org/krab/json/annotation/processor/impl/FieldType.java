package org.krab.json.annotation.processor.impl;

import java.util.function.Function;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;

import org.krab.json.annotation.processor.builder.FileBuilder;
import org.krab.json.annotation.processor.builder.MethodBuilder;

public sealed interface FieldType permits ScalarType, JsonifiableType, ContainerType, EnumType {
	String asJavaCode();
	void addImports(FileBuilder builder, PackageElement currentPackage);
	void addFromJsonInstructions(MethodBuilder builder, int level, String source,
			Function<String,String> setter);
	void addToJsonInstructions(MethodBuilder builder, int level, String factory, String source,
			Function<String, String> setter);
	
	public static PackageElement getPackage(Element element) {
		if (element.getKind() == ElementKind.PACKAGE)
			return (PackageElement) element;
		return getPackage(element.getEnclosingElement());
	}
}
