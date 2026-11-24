package org.krab.json.annotation.processor.impl;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import org.krab.json.Jsonifiable;
import org.krab.json.annotation.JsonClass;
import org.krab.json.annotation.JsonField;
import org.krab.json.annotation.JsonRecord;
import org.krab.json.annotation.processor.builder.AttributeBuilder;
import org.krab.json.annotation.processor.builder.FileBuilder;
import org.krab.json.annotation.processor.builder.MethodBuilder;
import org.krab.json.annotation.processor.builder.Reach;
import org.krab.json.annotation.processor.builder.TypeBuilder;
import org.krab.json.annotation.processor.exception.JsonAnnotationException;
import org.krab.json.annotation.processor.exception.UnsupportedTypeException;

public class Field {
	private final FieldType jsonType;
	private final String jsonName;
	private final TypeMirror javaType;
	private final String javaName;
	private final String getter;
	
	private Field(FieldType jsonType, String jsonName, TypeMirror javaType, String javaName, String getter) {
		this.jsonType = jsonType;
		this.jsonName = jsonName;
		this.javaType = javaType;
		this.javaName = javaName;
		this.getter = getter;
	}
	
	public FieldType getJsonType() {
		return this.jsonType;
	}
	
	public String getJsonName() {
		return this.jsonName;
	}
	
	public TypeMirror getJavaType() {
		return this.javaType;
	}
	
	public String getJavaName() {
		return this.javaName;
	}
	
	public String getGetter() {
		return this.getter;
	}
	
	public AttributeBuilder asAttribute() {
		AttributeBuilder builder = new AttributeBuilder()
			.addJavadoc(String.format("<p>Builder's version of field %s</p>", this.javaName))
			.setName(this.javaName)
			.setReach(Reach.PRIVATE)
			.setType(this.javaType.toString());
		
		if (this.jsonType instanceof CollectionType ct)
			builder.setValue("new " + ct.getCollectionType().defaultImplementation() + "<>()");
		
		if (this.jsonType instanceof OptionalType)
			builder.setValue("Optional.empty()");
		
		return builder;
	}
	
	public void addRelevantImports(FileBuilder builder, PackageElement currentPackage) {
		this.jsonType.addImports(builder, currentPackage);
	}
	
	public void addSetterMethods(TypeBuilder builder, String className) {
		
		if (this.jsonType instanceof CollectionType) {
			String adderName = "add" + this.javaName.substring(0, 1).toUpperCase()
					+ this.javaName.substring(1);
			
			TypeMirror innerType = ((DeclaredType) this.javaType).getTypeArguments().getFirst();
			
			MethodBuilder addMethodBuilder = new MethodBuilder()
					.setSignature(sigBuilder -> sigBuilder
							.addJavadoc(String.format("""
									<p>Adds an element to %s</p>
									@param e value to add
									@return this builder
									""", this.javaName))
							.setReach(Reach.PUBLIC)
							.setType(className)
							.setName(adderName)
							.addParameter(innerType.toString(), "e"))
					.addInstruction(String.format(
							"this.%s.add(e);"
							, this.javaName))
					.addInstruction("return this;");
			builder.addMethod(addMethodBuilder);
		}
		
		String setterName = "set" + this.javaName.substring(0, 1).toUpperCase()
				+ this.javaName.substring(1);
		
		if (this.jsonType instanceof OptionalType) {
			TypeMirror innerType = ((DeclaredType) this.javaType).getTypeArguments().getFirst();
			
			MethodBuilder setOptionMethodBuilder = new MethodBuilder()
					.setSignature(sigBuilder -> sigBuilder
							.addJavadoc(String.format("""
									<p>Sets an optional attribute with an element, making it non-empty</p>
									@param e value to set the field to
									@return this builder
									""", this.javaName))
							.setReach(Reach.PUBLIC)
							.setType(className)
							.setName(setterName)
							.addParameter(innerType.toString(), "e"))
					.addInstruction(String.format(
							"this.%s = Optional.of(e);"
							, this.javaName))
					.addInstruction("return this;");
			builder.addMethod(setOptionMethodBuilder);
		}
		
		MethodBuilder setMethodBuilder = new MethodBuilder()
				.setSignature(sigBuilder -> sigBuilder
						.addJavadoc(String.format("""
								<p>Sets the value of %s</p>
								@param e value to set the attribute to
								@return this builder
								""", this.javaName))
						.setReach(Reach.PUBLIC)
						.setType(className)
						.setName(setterName)
						.addParameter(this.javaType.toString(), "e"))
				.addInstruction(String.format(
						"this.%s = e;"
						, this.javaName))
				.addInstruction("return this;");
		builder.addMethod(setMethodBuilder);
	}
	
	public void addFromJsonInstructions(MethodBuilder builder, String argument) {
		String setterName = "set" + this.javaName.substring(0, 1).toUpperCase()
				+ this.javaName.substring(1);
		
		this.jsonType.addFromJsonInstructions(builder, 0,
				String.format("%s.get(\"%s\")", argument, this.jsonName),
				(String arg) -> String.format("this.%s(%s)", setterName, arg));
	}
	
	public void addToJsonInstructions(MethodBuilder builder, String factory, String argument, String target) {
		String getterName = this.javaName; // Because it is a record
		
		this.jsonType.addToJsonInstructions(builder, 0,
				factory,
				String.format("%s.%s()", argument, getterName),
				(String arg) -> String.format("%s.add(\"%s\", %s)", target, this.jsonName, arg));
	}
	
	public static Field createField(ProcessingEnvironment env, 
			JsonField annot, TypeMirror javaType, String javaName, String getter) throws JsonAnnotationException {
		return new Field(getFieldTypeOf(env, annot, javaType), annot.value(), javaType, javaName,
				getter.isBlank() ? javaName : getter);
	}
	
	private static FieldType getFieldTypeOf(ProcessingEnvironment env, JsonField annot, TypeMirror type)
			throws JsonAnnotationException {
		if (type.getKind() == TypeKind.DECLARED) {
			DeclaredType decType = (DeclaredType) type;
			TypeElement typeElt = (TypeElement) decType.asElement();
			
			if (typeElt.equals(env.getElementUtils().getTypeElement(String.class.getCanonicalName()))) { // String
				return new ScalarType(ScalarType.Type.STRING);
			} else if (typeElt.equals(env.getElementUtils().getTypeElement(Integer.class.getCanonicalName()))) {
				return new ScalarType(ScalarType.Type.INTEGER);
			} else if (typeElt.equals(env.getElementUtils().getTypeElement(Boolean.class.getCanonicalName()))) {
				return new ScalarType(ScalarType.Type.BOOLEAN);
			} else if (typeElt.equals(env.getElementUtils().getTypeElement(Double.class.getCanonicalName()))) {
				return new ScalarType(ScalarType.Type.DECIMAL);
			} else if (typeElt.equals(env.getElementUtils().getTypeElement(Optional.class.getCanonicalName()))) {
				TypeMirror innerType = decType.getTypeArguments().getFirst();
				return new OptionalType(getFieldTypeOf(env, annot, innerType));
			} else if (typeElt.getKind() == ElementKind.ENUM) {
				return new EnumType(typeElt, annot.mode());
			} else {
				Set<TypeElement> interfaces = calculateAllInterfaces(typeElt);
				
				TypeElement listType = env.getElementUtils().getTypeElement(List.class.getCanonicalName());
				TypeElement setType = env.getElementUtils().getTypeElement(Set.class.getCanonicalName());
				TypeElement jsonifiableType = env.getElementUtils().getTypeElement(Jsonifiable.class.getCanonicalName());
				boolean isList = interfaces.stream().anyMatch(intf -> intf.equals(listType)),
						isSet  = interfaces.stream().anyMatch(intf -> intf.equals(setType)),
						isJsonifiable = interfaces.stream().anyMatch(intf -> intf.equals(jsonifiableType));
				
				if (isList || isSet) {
					TypeMirror arg = decType.getTypeArguments().getFirst();
					return new CollectionType(
							isList ? CollectionType.Type.LIST : CollectionType.Type.SET,
							getFieldTypeOf(env, annot, arg)
						);
				} else if (isJsonifiable) {
					return new JsonifiableType(type);
				}
			}
		} else if (type.getKind() == TypeKind.ARRAY) {
			javax.lang.model.type.ArrayType arrType = (javax.lang.model.type.ArrayType) type;
			return new ArrayType(getFieldTypeOf(env, annot, arrType.getComponentType()));
		} else if (type.getKind() == TypeKind.BOOLEAN) {
			return new ScalarType(ScalarType.Type.BOOLEAN);
		} else if (type.getKind() == TypeKind.INT) {
			return new ScalarType(ScalarType.Type.INTEGER);
		} else if (type.getKind() == TypeKind.DOUBLE) {
			return new ScalarType(ScalarType.Type.DECIMAL);
		}
		
		throw new UnsupportedTypeException("type " + type.toString() + " is not supported");
	}
	
	public static Set<TypeElement> calculateAllInterfaces(TypeElement typeElt) {
		Set<TypeElement> interfaces = new HashSet<>();
		
		Deque<TypeElement> toBeTreated = new LinkedList<>();
		toBeTreated.push(typeElt);
		
		while (!toBeTreated.isEmpty()) {
			TypeElement current = toBeTreated.pop();
			
			if (current.getKind() == ElementKind.INTERFACE)
				interfaces.add(current);
			
			for (TypeMirror m : current.getInterfaces()) {
				if (m.getKind() == TypeKind.DECLARED)
					toBeTreated.push((TypeElement) ((DeclaredType) m).asElement());
			}
			
			if (current.getSuperclass().getKind() == TypeKind.DECLARED) {
				toBeTreated.push((TypeElement) ((DeclaredType) current.getSuperclass()).asElement());
			}
		}
		
		return interfaces;
	}
}
