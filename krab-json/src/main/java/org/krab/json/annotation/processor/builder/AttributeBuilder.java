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
package org.krab.json.annotation.processor.builder;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/// Represents an attribute (or field) in a class.
/// 
/// @author krab5
/// @version 1.0
public class AttributeBuilder {
	/// The JavaDoc of the attribute, as a list of lines to be output.
	private List<String> javadoc = new ArrayList<>();
	
	/// Annotations associated with the attribute
	private List<String> annotations = new ArrayList<>();
	
	/// Attribute's reach (or visibility)
	private Reach reach = Reach.PACKAGE;
	
	/// If the attribute is static
	private boolean isStatic;
	
	/// If the attribute is final (read-only)
	private boolean isFinal;
	
	/// Attribute's type
	private String type;
	
	/// Attribute's name
	private String name;
	
	/// Possible initialization of the attribute as a list of 
	/// (value) instructions
	private List<String> value = new ArrayList<>();
	
	/// Creates a default attribute builder, with no JavaDoc, package reach,
	/// non static, non final, no type and no name.
	public AttributeBuilder() {}
	
	/// Add a bit of JavaDoc; it is added to the internal representation
	/// by using a split-wrap algorithm that makes it so that the lines
	/// are cut in bits of a maximum size (wrapped).
	/// 
	/// @param comment bit of JavaDoc to be added
	/// @return this builder
	public AttributeBuilder addJavadoc(String comment) {
		this.javadoc.addAll(Commons.wrappify(comment));
		return this;
	}
	
	/// Add an annotation to the list of annotations of this attribute.
	/// Annotations are represented as simple strings (without the @) to
	/// allow "complex" annotations patterns (i.e.: `@Annot(param="value")`)
	/// 
	/// @param annotation annotation to add
	/// @return this builder
	public AttributeBuilder addAnnotation(String annotation) {
		this.annotations.add(annotation);
		return this;
	}
	
	/// Set the attribute's reach. Reach is _package_ by default.
	/// 
	/// @param reach reach of the attribute
	/// @return this builder
	public AttributeBuilder setReach(Reach reach) {
		this.reach = reach;
		return this;
	}
	
	/// Set the attribute as being static. Attributes are non-static by default.
	/// 
	/// @return this builder
	public AttributeBuilder setStatic() {
		this.isStatic = true;
		return this;
	}
	
	/// Set the attribute as being final. Attributes are non-final by default.
	/// 
	/// @return this builder
	public AttributeBuilder setFinal() {
		this.isFinal = true;
		return this;
	}
	
	/// Set the attribute's type. The type is provided as a string to be as
	/// general as possible while not being too inconvenient. There are no
	/// restrictions on the type, other than the generated file must be
	/// syntactically correct.
	/// 
	/// @param type the type, as a string
	/// @return this builder
	public AttributeBuilder setType(String type) {
		this.type = type;
		return this;
	}
	
	/// Set the name of the attribute. No check is performed on the input
	/// name; it is assumed that it is syntactically correct.
	/// 
	/// @param name the name of the attribute
	/// @return this builder
	public AttributeBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	/// Set the value of the attribute (it's initial assignment in addition to
	/// its declaration), as a valued instruction provided as
	/// a string. Multi-line string are cut so as to be correctly indented.
	/// By default, the attribute has no value (it is simply being declared
	/// and not assigned).
	/// 
	/// @param value instruction(s) corresponding to the attribute's value
	/// @return this builder
	public AttributeBuilder setValue(String value) {
		this.value.addAll(value.lines().collect(Collectors.toList()));
		return this;
	}
	
	/// Output the attribute represented by this builder to the given writer,
	/// with the specified indent.
	/// 
	/// @param indent indent level to be applied to each line
	/// @param writer the writer to output the attribute to
	/// @throws IOException potentially thrown by [Writer#append()]
	public void output(int indent, Writer writer) throws IOException {
		int localindent = indent;
		
		Commons.outputJavadoc(localindent, writer, this.javadoc);
		
		for (String annot : this.annotations) {
			writer.append(Commons.indent(localindent))
			      .append('@')
			      .append(annot)
			      .append('\n');
		}
		
		writer.append(Commons.indent(localindent));
		
		if (this.reach != Reach.PACKAGE)
			writer.append(this.reach.getWord()).append(' ');
		
		if (this.isStatic)
			writer.append("static ");
		
		if (this.isFinal)
			writer.append("final ");
		
		writer.append(this.type)
		      .append(' ')
		      .append(this.name);
		
		if (!this.value.isEmpty()) {
			localindent += 4;
			writer.append(" = ");
			for (int i = 0; i < this.value.size(); i++) {
				if (i > 0)
					writer.append(Commons.indent(localindent));
				writer.append(this.value.get(i));
				if (i < this.value.size() - 1)
					writer.append('\n');
			}
		}
		
		writer.append(';').append('\n');
	}
}
