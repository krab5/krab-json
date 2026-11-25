/* krab-json - Krab's lightweight JSON library
 * Copyright (C) 2026  Krab
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */
package org.krab.json.annotation.processor;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.RecordComponentElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import org.krab.json.JsonValue;
import org.krab.json.Jsonifiable;
import org.krab.json.annotation.JsonClass;
import org.krab.json.annotation.JsonField;
import org.krab.json.annotation.JsonRecord;
import org.krab.json.annotation.processor.builder.*;
import org.krab.json.annotation.processor.exception.JsonAnnotationException;
import org.krab.json.annotation.processor.exception.UnsupportedTypeException;
import org.krab.json.annotation.processor.impl.Field;

/// This is the main processor for the annotations provided by this library.
/// 
/// See the documentation of each annotation for more information.
/// 
/// This class is not to be used directly; it is registered as a service
/// provider in the `META-INF` directory, and is used by the compiler (if
/// properly registered) to handle the annotated structures.
/// 
/// A more "in-depth" view at the processor's behavior is provided in this
/// package's documentation.
/// 
/// @see org.krab.json.annotation.processor
/// @see JsonRecord
/// @see JsonClass
/// @see JsonField
/// @author krab5
/// @version 1.0
@SupportedAnnotationTypes({
		"org.krab.json.annotation.JsonClass",
		"org.krab.json.annotation.JsonRecord",
		"org.krab.json.annotation.JsonField"
})
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class JsonAnnotationProcessor extends AbstractProcessor {

	/// Process the three annotations supported by this processor ([JsonRecord], [JsonClass]
	/// and [JsonField]). In practice, `JsonField` is not processed directly, and `JsonClass`
	/// is only checked for basic incompatibilities with the lib. Only records annotated with
	/// `JsonRecord` are processed in full and associated with a new file (builder class).
	/// 
	/// @param annotations set of annotations retrieved from the processor front end
	/// @param roundEnv environment associated with the retrieved annotations
	/// @return true iff the annotation were processed (this method always returns true)
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (Element elt : roundEnv.getElementsAnnotatedWith(JsonRecord.class)) {
			try {
				this.processRecord(elt);
			} catch (IOException e) {
				this.processingEnv.getMessager().printError(String.format(
						"I cannot generate the builder file: %s"
						, e.getMessage()));
			}
		}
		
		for (Element elt : roundEnv.getElementsAnnotatedWith(JsonClass.class)) {
			this.processClass(elt);
		}
		
		return true;
	}
	
	/// Checks that the given type (class or record) is suitable for working
	/// with in the library. A type `T` is consistent if it:
	///  - implements [Jsonifiable]
	///  - defines the public, static method `T fromJson(JsonValue)`
	/// 
	/// @param element the type element to check
	/// @return true if no error was encountered, false otherwise (errors are
	///         reported using the message of the processing environment)
	private boolean sanityCheck(TypeElement element) {
		boolean isOk = true;
		
		// Test element for Jsonifiable
		Set<TypeElement> interfaces = Field.calculateAllInterfaces(element);
		TypeElement jsonifiableType = this.processingEnv.getElementUtils()
				.getTypeElement(Jsonifiable.class.getName());
		
		if (!interfaces.stream().anyMatch(te -> te.equals(jsonifiableType))) {
			this.processingEnv.getMessager().printError(
					"This class must implement interface Jsonifiable"
					, element);
			isOk = false;
		}
		
		// Test element for fromJson static function
		Optional<ExecutableElement> fromJsonOpt = element.getEnclosedElements().stream()
			.filter(e -> e.getKind() == ElementKind.METHOD)
			.map((Element e) -> (ExecutableElement) e)
			.filter(e -> e.getSimpleName().toString().equals("fromJson"))
			.findFirst();
		
		if (fromJsonOpt.isPresent()) {
			ExecutableElement fromJson = fromJsonOpt.get();
			
			TypeMirror returnMirror = fromJson.getReturnType();
			
			if (returnMirror.getKind() == TypeKind.DECLARED) {
				TypeElement returnElt = (TypeElement) ((DeclaredType) returnMirror).asElement();
				if (!returnElt.equals(element)) {
					this.processingEnv.getMessager().printError(
							"Method 'fromJson' must have this class as return type"
							, element);
					isOk = false;
				}
			} else {
				this.processingEnv.getMessager().printError(
						"Method 'fromJson' must have this class as return type"
						, element);
				isOk = false;
			}
			
			List<? extends VariableElement> params = fromJson.getParameters();
			
			if (params.size() == 1) {
				TypeMirror paramMirror = params.get(0).asType();
				
				if (paramMirror instanceof DeclaredType) {
					TypeElement paramElt = (TypeElement) ((DeclaredType) paramMirror).asElement();
					
					if (!paramElt.equals(this.processingEnv.getElementUtils().getTypeElement(JsonValue.class.getCanonicalName()))) {
						this.processingEnv.getMessager().printError(
								"Method 'fromJson' must declare exactly 1 parameter of type JsonValue"
								, element);
						isOk = false;
					}
				} else {
					this.processingEnv.getMessager().printError(
							"Method 'fromJson' must declare exactly 1 parameter of type JsonValue"
							, element);
					isOk = false;
				}
			} else {
				this.processingEnv.getMessager().printError(
						"Method 'fromJson' must declare exactly 1 parameter"
						, element);
				isOk = false;
			}
			
			if (!fromJson.getModifiers().contains(Modifier.PUBLIC)) {
				this.processingEnv.getMessager().printError(
						"Method 'fromJson' must be public"
						, element);
				isOk = false;
			}
			
			if (!fromJson.getModifiers().contains(Modifier.STATIC)) {
				this.processingEnv.getMessager().printError(
						"Method 'fromJson' must be static"
						, element);
				isOk = false;
			}
		} else {
			this.processingEnv.getMessager().printError(
					"This class must define a public static 'fromJson' method"
					, element);
			isOk = false;
		}
		
		return isOk;
	}
	
	/// Process a [JsonClass]-annotated type (sanity check only).
	/// 
	/// @param rawElement the element annotated with `JsonClass`
	/// @return true iff no error occurred (errors are reported using the processing
	///         environment's messager)
	private boolean processClass(Element rawElement) {
		boolean isOk = true;
		
		// Test element for classship
		if (rawElement.getKind() != ElementKind.CLASS) {
			this.processingEnv.getMessager().printError(
					"This annotation only works on class types"
					, rawElement);
			isOk = false;
			return isOk;
		}
		
		TypeElement element = (TypeElement) rawElement;
		isOk = this.sanityCheck(element);
		
		return isOk;
	}
	
	/// Process a [JsonRecord]-annotated record type, checking its consistency,
	/// gathering its fields and outputting the associated builder class.
	/// 
	/// @param rawElement the annotated record
	/// @return true iff no error occurred during processing (errors are reported
	///         using the processing environment's messager)
	private boolean processRecord(Element rawElement)
			throws IOException {
		boolean isOk = true;
		
		// Test element for recordship
		if (rawElement.getKind() != ElementKind.RECORD) {
			this.processingEnv.getMessager().printError(
					"This annotation only works on record types"
					, rawElement);
			isOk = false;
			return isOk;
		}
		
		TypeElement element = (TypeElement) rawElement;
		
		isOk = this.sanityCheck(element);
		
		// Retrieve fields
		List<Field> fields = new ArrayList<>();
		
		for (RecordComponentElement elt : element.getRecordComponents()) {
			JsonField fieldAnnot = elt.getAnnotation(JsonField.class);
			
			if (fieldAnnot != null) {
				try {
					fields.add(Field.createField(this.processingEnv, 
							fieldAnnot, elt.asType(), 
							elt.getSimpleName().toString(),
							elt.getAccessor().getSimpleName().toString()));
				} catch (UnsupportedTypeException e) {
					this.processingEnv.getMessager().printError(String.format(
							"The type of component %s (%s) is unsupported. " +
							"Please check documentation for a list of accepted types."
							, elt.getSimpleName().toString(), elt.asType().toString()), elt);
					isOk = false;
				} catch (JsonAnnotationException e) {
					this.processingEnv.getMessager().printError(e.getMessage());
				}
			}
		}
		
		// Get package
		Element pkgElt = element.getEnclosingElement();
		
		if (pkgElt.getKind() != ElementKind.PACKAGE) {
			this.processingEnv.getMessager().printError(
					"Unfortunately, the annotation processor does not work yet " +
			        "for nested types."
					, element);
			return false;
		}
		
		String pkgName = ((PackageElement) pkgElt).getQualifiedName().toString();
		
		// Build class
		String className = element.getSimpleName() + "Builder";
		
		FileBuilder builder = new FileBuilder();
		builder.setName(className)
			   .setPackage(pkgName);
		
		TypeBuilder classBuilder = new TypeBuilder();
		classBuilder.addJavadoc(String.format("""
				This class is generated by [org.json.krab.annotation.processor.JsonAnnotationProcessor] from the annotated type %s.
				
				It is a builder for said type, and provide a [#toJson()] and [fromJson()] methods for translating the type from and to JSON.
				
				@generated
				""", element.getSimpleName()));
		classBuilder.setName(className)
		            .setReach(Reach.PUBLIC);
		
		for (Field field : fields)
			classBuilder.addAttribute(field.asAttribute());
		
		classBuilder.addMethod(mb -> mb
				.setSignature(sb -> sb
					.setReach(Reach.PUBLIC)
					.setName(className)));
		
		for (Field field : fields)
			field.addSetterMethods(classBuilder, className);
		
		classBuilder.addMethod(this.buildMethod(element.getSimpleName().toString(), fields));
		classBuilder.addMethod(this.fromJsonMethod(className, fields));
		classBuilder.addMethod(this.toJsonMethod(element.getSimpleName().toString(), fields));
		
		builder.setMainType(classBuilder);
		
		// Add imports
		builder.addImport("org.krab.json.*");
		builder.addImport("org.krab.json.exception.JsonSchemaException");
		
		for (Field field : fields)
			field.addRelevantImports(builder, (PackageElement) pkgElt);
		
		// Output file
		JavaFileObject file = this.processingEnv.getFiler()
				.createSourceFile(
					((PackageElement) pkgElt).getQualifiedName() + "." + className,
					element);
		
		Writer writer = file.openWriter();
		try {
			builder.output(writer);
		} finally {
			writer.close();
		}
		
		return isOk;
	}
	
	/// Creates the `build()` member method, which essential instantiates the record
	/// with all the relevant fields. Fields are sorted in order of appearance in the
	/// record. Only the fields that are annotated with [JsonField] are considered.
	/// 
	/// If not all fields are annotated, the record must define a compatible (non-
	/// default) constructor for the `build()` method to call.
	/// 
	/// @param className the class name of the annotated record type
	/// @param fields list of annotated fields to use, in order of appearance
	///        in the original annotated record
	/// @return a method in the form of a [MethodBuilder]
	private MethodBuilder buildMethod(String className, List<Field> fields) {
		MethodBuilder builder = new MethodBuilder()
			.setSignature(sb -> sb
				.addJavadoc("Builds an instance of " + className + " based on the attributes stored by the builder")
				.addJavadoc("@return instance of " + className)
				.setReach(Reach.PUBLIC)
				.setType(className)
				.setName("build"));
		
		builder.addInstruction("return new " + className + "(");
		
		var it = fields.iterator();
		
		while (it.hasNext()) {
			builder.addInstruction(1, it.next().getJavaName() + (it.hasNext() ? "," : ");"));
		}
		
		return builder;
	}
	
	/// Creates the `fromJson(JsonValue)` member method, that takes a JSON value as
	/// input and populates the builder's field with its content.
	/// 
	/// The way values are obtained depends on the field configuration.
	/// 
	/// @param className the builder's class name
	/// @param fields the list of annotated fields
	/// @return the `fromJson` method, as a [MethodBuilder]
	private MethodBuilder fromJsonMethod(String className, List<Field> fields) {
		MethodBuilder builder = new MethodBuilder()
			.setSignature(sb -> sb
				.addJavadoc("Populate the builder's attribute from a JSON value")
				.addJavadoc("@param value the JSON value to be used")
				.setReach(Reach.PUBLIC)
				.setType(className)
				.setName("fromJson")
				.addParameter("JsonValue", "value")
				.addException("JsonSchemaException"))
			.addInstruction("JsonObject object = Json.valueAs(value, JsonObject.class);")
			.addNewline();
		
		for (Field field : fields) {
			field.addFromJsonInstructions(builder, "object");
		}
		
		builder.addNewline().addInstruction("return this;");
		
		return builder;
	}
	
	/// Create the public, static `toJson` method, that takes a record instance as
	/// input and outputs a [JsonValue] corresponding to its fields.
	/// 
	/// @param className class name of the record type
	/// @param fields list of the annotated fields of the record
	/// @return the `toJson` method, under the form of a [MethodBuilder]
	private MethodBuilder toJsonMethod(String className, List<Field> fields) {
		MethodBuilder builder = new MethodBuilder()
			.setSignature(sb -> sb
				.addJavadoc("Create a JSON value based on the fields of the parameter")
				.addJavadoc("@param object to transform into JSON")
				.addJavadoc("@return resulting JSON value")
				.setReach(Reach.PUBLIC)
				.setStatic()
				.setType("JsonValue")
				.setName("toJson")
				.addParameter(className, "object"))
			.addInstruction("JsonFactory factory = JsonFactory.DEFAULT_FACTORY;")
			.addInstruction("JsonObject output = factory.createObject();");
		
		for (Field field : fields) {
			field.addToJsonInstructions(builder, "factory", "object", "output");
		}
		
		builder.addNewline().addInstruction("return output;");
		
		return builder;
	}
	
}
