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

/// Builder to make up a signature. A signature is what essentially _declares_
/// a method; adding to that instructions makes it an implemented method. A
/// method is mainly identified by its name, return value and parameters, plus
/// a few other things like reach and type parameters.
/// 
/// Note that constructors are methods, identified by their lack of return type.
/// 
/// @author krab5
/// @version 1.0
public class SignatureBuilder {
	/// Return type of the method. If the type is null or blank, the method is
	/// assumed to be a constructor.
	private String type;
	
	/// Name of the method.
	private String name;
	
	/// Reach of the method (public, private, protected or package (default))
	private Reach reach = Reach.PACKAGE;
	
	/// Is the method static
	private boolean isStatic = false,
	/// Is the method a default method
					isDefault = false,
	/// Is the method abstract
					isAbstract = false;
	
	/// List (possibly empty) of the method's local type parameters
	private List<String> typeParams = new ArrayList<>();
	
	/// List of the types of the parameters, in order of appearance
	private List<String> paramType = new ArrayList<>();
	
	/// List of the names of the parameters, in order of appearance
	private List<String> paramName = new ArrayList<>();
	
	/// JavaDoc for the method
	private List<String> javadoc = new ArrayList<>();
	
	/// List of annotations for the signature
	private List<String> annotations = new ArrayList<>();
	
	/// List of declared exception (through the `throws` keyword)
	private List<String> exceptions = new ArrayList<>();
	
	
	/// Constructor.
	public SignatureBuilder() {}
	
	
	/// Set the method's return type. A method with null or blank type
	/// is considered a constructor.
	/// 
	/// @param type return type
	/// @return this builder
	public SignatureBuilder setType(String type) {
		this.type = type;
		return this;
	}
	
	/// Set the method's name.
	/// 
	/// @param name method's name
	/// @return this builder
	public SignatureBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	/// Set the method's reach (`package` by default).
	/// 
	/// @param r method reach
	public SignatureBuilder setReach(Reach r) {
		this.reach = r;
		return this;
	}
	
	/// Set the method to be static (non-static by default).
	/// 
	/// @return this builder
	public SignatureBuilder setStatic() {
		this.isStatic = true;
		return this;
	}
	
	/// Set the method to be a default method (non-default by default ha ha).
	/// 
	/// @return this builder
	public SignatureBuilder setDefault() {
		this.isDefault = true;
		return this;
	}
	
	/// Set the method to be (explicitly) abstract (non-abstract by default).
	/// 
	/// @return this builder
	public SignatureBuilder setAbstract() {
		this.isAbstract = true;
		return this;
	}
	
	/// Add JavaDoc to the method. The input is [wrappified][Commons#wrappify(String)]
	/// before being integrated, to format it on < 80 characters.
	/// 
	/// @param comment the comment to add
	/// @return this builder
	public SignatureBuilder addJavadoc(String comment) {
		this.javadoc.addAll(Commons.wrappify(comment));
		return this;
	}
	
	/// Add an annotation to the signature. The annotation is a free-form
	/// string (without the `@`) to allow complex annotation patterns.
	/// 
	/// @param annotation annotation to be added
	/// @return this builder
	public SignatureBuilder addAnnotation(String annotation) {
		this.annotations.add(annotation);
		return this;
	}
	
	/// Add a type parameter to the method (for generic methods).
	/// 
	/// @param param type parameter name
	/// @return this builder
	public SignatureBuilder addTypeParam(String param) {
		this.typeParams.add(param);
		return this;
	}
	
	/// Add a parameter, identified by its type and name.
	/// 
	/// @param type parameter's type
	/// @param name parameter's name
	/// @return this builder
	public SignatureBuilder addParameter(String type, String name) {
		this.paramType.add(type);
		this.paramName.add(name);
		return this;
	}
	
	/// Add an exception to the `throws` declaration.
	/// 
	/// @param except exception to be added
	/// @return this builder
	public SignatureBuilder addException(String except) {
		this.exceptions.add(except);
		return this;
	}
	
	/// Output this method to the given writer, with the given indent level.
	/// Because signatures are used both for method definitions and declarations,
	/// no semi-colon (`;`) is added at the end of the output. The user must make
	/// sure that the proper line termination is appended to the writer.
	/// 
	/// @param indent indent level added to each line
	/// @param writer the writer to output the method to
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
		
		// Signature
		writer.append(Commons.indent(localindent));
		
		if (this.isDefault)
			writer.append("default ");
		
		if (this.reach != Reach.PACKAGE)
			writer.append(this.reach.getWord()).append(' ');
		
		if (this.isAbstract)
			writer.append("abstract ");
		
		if (this.isStatic)
			writer.append("static ");
		
		if (!this.typeParams.isEmpty()) {
			writer.append('<')
			      .append(String.join(", ", this.typeParams))
			      .append('>')
			      .append(' ');
		}
		
		if (type != null && !type.isBlank())
			writer.append(this.type).append(' ');
		
		writer.append(this.name)
		      .append('(');
		
		for (int i = 0; i < this.paramName.size(); i++) {
			writer.append(this.paramType.get(i))
			      .append(' ')
			      .append(this.paramName.get(i));
			if (i < this.paramName.size() - 1)
				writer.append(", ");
		}
		
		writer.append(')');
		
		if (!this.exceptions.isEmpty()) {
			writer.append(" throws ");
			writer.append(String.join(", ", this.exceptions));
		}
	}
}
