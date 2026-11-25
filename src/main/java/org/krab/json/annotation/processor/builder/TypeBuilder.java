/* krab-json - Krab's lightweight JSON library
 * Copyright (C) 2026 krab5
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
package org.krab.json.annotation.processor.builder;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/// This builder represents a Java _type_, that is, an interface, a class or
/// an enum. **Records are not currently supported.**
/// 
/// @author krab5
/// @version 1.0
public class TypeBuilder {
	/// Enumeration listing possible type "natures". Note that abstract
	/// classes constitute a separate category from classes, because it was
	/// more convenient (to avoid "abstract interface" or such).
	public static enum Type {
		/// The type is a (non-abstract) class definition
		CLASS,
		/// The type is an abstract class definition
		ABSTRACT_CLASS,
		/// The type is an interface definition
		INTERFACE,
		/// The type is an enum definition
		ENUM;
		
		/// Turns this value into the associated Java keyword(s)
		/// 
		/// @return keyword corresponding to this value
		public String getWord() {
			switch (this) {
				case CLASS: return "class";
				case ABSTRACT_CLASS: return "abstract class";
				case INTERFACE: return "interface";
				case ENUM: return "enum";
				default: return null;
			}
		}
	}
	
	/// Type's JavaDoc
	private List<String> javadoc = new ArrayList<>();
	
	/// Type's list of annotations
	private List<String> annotations = new ArrayList<>();
	
	/// Type's reach (`package` by default)
	private Reach reach = Reach.PACKAGE;
	
	/// Is the type static
	private boolean isStatic = false;
	
	/// Is the type final
	private boolean isFinal = false;
	
	/// Type's nature (class by default)
	private Type type = Type.CLASS;
	
	/// Type's name
	private String name;
	
	/// Type's class extension declaration. If this is null or blank, no
	/// extension is provided to the type; also, it is ignored for interfaces
	private String extending = "";
	
	/// Type's implementations declaration. This is used when defining interfaces
	/// to populate the `extends` declaration.
	private List<String> implementing = new ArrayList<>();
	
	/// Type's attributes
	private List<AttributeBuilder> attributes = new ArrayList<>();
	
	/// Type's declared (not defined) methods
	private List<SignatureBuilder> signatures = new ArrayList<>();
	
	/// Type's defined methods, including constructors
	private List<MethodBuilder> methods = new ArrayList<>(); // and constructors!
	
	/// Type's nested types
	private List<TypeBuilder> nestedTypes = new ArrayList<>();
	
	
	/// Constructor.
	public TypeBuilder() {}
	
	/// Add a bit of comment to this type's JavaDoc. The input is [wrappified][Commons#wrappify]
	/// before being integrated.
	/// 
	/// @param comment the comment to be added
	/// @return this builder
	public TypeBuilder addJavadoc(String comment) {
		this.javadoc.addAll(Commons.wrappify(comment));
		return this;
	}
	
	/// Add an annotation to the list of annotations of the type.
	/// 
	/// @param annotation annotation to be added
	/// @return this builder
	public TypeBuilder addAnnotation(String annotation) {
		this.annotations.add(annotation);
		return this;
	}
	
	
	/// Set the type's reach (`package` by default).
	/// 
	/// @param reach type's reach
	/// @return this builder
	public TypeBuilder setReach(Reach reach) {
		this.reach = reach;
		return this;
	}
	
	/// Set this type to be static (non-static by default).
	/// 
	/// @return this builder
	public TypeBuilder setStatic() {
		this.isStatic = true;
		return this;
	}
	
	/// Set this type to be final (non-final by default).
	/// 
	/// @return this builder
	public TypeBuilder setFinal() {
		this.isFinal = true;
		return this;
	}
	
	/// Set this type's nature (class by default).
	/// 
	/// @return this builder
	public TypeBuilder setType(Type type) {
		this.type = type;
		return this;
	}
	
	/// Set this type's name.
	/// 
	/// @return this builder
	public TypeBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	/// Set this type's extending declaration (none by default). Null or
	/// blank means no extending declaration.
	/// 
	/// @param name extended type name
	/// @return this builder
	public TypeBuilder setExtending(String name) {
		this.extending = name;
		return this;
	}
	
	/// Add a type to the list of implementations for the type, or of
	/// extensions for interfaces specifically.
	/// 
	/// @param name type name to add to the implemented list
	/// @return this builder
	public TypeBuilder addImplementing(String name) {
		this.implementing.add(name);
		return this;
	}
	
	/// Add an attribute as a continuation.
	/// 
	/// @param next the continuation creating an attribute to be added
	/// @return this builder
	public TypeBuilder addAttribute(ContinueBuilding<AttributeBuilder> next) {
		this.attributes.add(next.andThen(new AttributeBuilder()));
		return this;
	}
	
	/// Add an attribute directly.
	/// 
	/// @param attribute field to be added
	/// @return this builder
	public TypeBuilder addAttribute(AttributeBuilder attribute) {
		this.attributes.add(attribute);
		return this;
	}
	
	/// Add a method as a continuation.
	/// 
	/// @param next the continuation creating a method to be added
	/// @return this builder
	public TypeBuilder addMethod(ContinueBuilding<MethodBuilder> next) {
		this.methods.add(next.andThen(new MethodBuilder()));
		return this;
	}
	
	/// Add a method directly.
	/// 
	/// @param method method to be added
	/// @return this builder
	public TypeBuilder addMethod(MethodBuilder method) {
		this.methods.add(method);
		return this;
	}
	
	/// Add a signature as a continuation.
	/// 
	/// @param next the continuation creating a signature to be added
	/// @return this builder
	public TypeBuilder addSignature(ContinueBuilding<SignatureBuilder> next) {
		this.signatures.add(next.andThen(new SignatureBuilder()));
		return this;
	}
	
	/// Add a signature directly.
	/// 
	/// @param signature signature to be added
	/// @return this builder
	public TypeBuilder addSignature(SignatureBuilder signature) {
		this.signatures.add(signature);
		return this;
	}
	
	/// Add a nested type as a continuation.
	/// 
	/// @param next the continuation creating a type to be added
	/// @return this builder
	public TypeBuilder addNestedType(ContinueBuilding<TypeBuilder> next) {
		this.nestedTypes.add(next.andThen(new TypeBuilder()));
		return this;
	}
	
	/// Add a nested type directly.
	/// 
	/// @param type type to be added
	/// @return this builder
	public TypeBuilder addNestedType(TypeBuilder type) {
		this.nestedTypes.add(type);
		return this;
	}
	
	/// Output this type to the given writer, with the given level of
	/// indent.
	/// 
	/// @param indent indent level to use for the output
	/// @param writer the writer to output to
	/// @throws IOException possibly thrown by [Writer#append]
	public void output(int indent, Writer writer) throws IOException {
		int localindent = indent;
		
		// Javadoc
		Commons.outputJavadoc(localindent, writer, this.javadoc);
		
		// Annotations
		for (String annotation : this.annotations) {
			writer.append(Commons.indent(localindent))
				  .append('@').append(annotation)
				  .append('\n');
		}
		
		// Head
		if (this.reach != Reach.PACKAGE)
			writer.append(this.reach.getWord()).append(' ');
		
		if (this.isStatic)
			writer.append("static ");
		
		if (this.isFinal)
			writer.append("final ");
		
		writer.append(this.type.getWord())
		      .append(' ')
		      .append(this.name)
		      .append(' ');
		
		if (this.extending != null && !this.extending.isBlank() && this.type != Type.INTERFACE)
			writer.append("extends ")
			      .append(this.extending)
			      .append(' ');
		
		if (!this.implementing.isEmpty()) {
			if (this.type == Type.INTERFACE)
				writer.append("extends ");
			else
				writer.append("implements ");
			
			writer.append(String.join(", ", this.implementing))
			      .append(' ');
		}
		
		writer.append("{\n");
		localindent++;
		
		// Content: nested types
		for (TypeBuilder tb : this.nestedTypes) {
			tb.output(localindent, writer);
			writer.append('\n');
		}
		
		// Content: attributes
		for (AttributeBuilder ab : this.attributes) {
			ab.output(localindent, writer);
			writer.append('\n');
		}
		
		// Content: signatures
		for (SignatureBuilder sb : this.signatures) {
			sb.output(localindent, writer);
			writer.append('\n');
		}
		
		// Content: methods
		for (MethodBuilder mb : this.methods) {
			mb.output(localindent, writer);
			writer.append('\n');
		}
		
		localindent--;
		writer.append('\n')
		      .append(Commons.indent(localindent))
		      .append('}')
		      .append('\n');
	}
}


