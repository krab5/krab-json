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

/// Represents a file containing a type.
/// 
/// @author krab5
/// @version 1.0
public class FileBuilder {
	/// The name of the file (and of the enclosed type, as per Java's constraints).
	private String name;
	
	/// Preamble of the file. The preamble is a long-form comment that does not appear
	/// in documentation, usually for licensing or explanation purposes. This attribute
	/// holds the preamble as a list of lines that will be output at the very beginning 
	/// of the file.
	private List<String> preamble = new ArrayList<>();
	
	/// Package name for the file (defaults to an empty string, i.e. the default package).
	private String pkg = "";
	
	/// List of imports. This list is sorted upon file writing.
	private List<String> imports = new ArrayList<>();
	
	/// The main type in the file.
	private TypeBuilder mainType;
	
	
	/// Constructor.
	public FileBuilder() {}
	
	/// Set the file's name. Note that it is the responsibility of the user
	/// to make sure that the _actual_ file name (typically associated with 
	/// the writer given to [#output]) is consistent with the provided name,
	/// which is only used in the documentation and when creating the main
	/// type.
	/// 
	/// @param name the name of the file/unit
	/// @return the builder
	public FileBuilder setName(String name) {
		this.name = name;
		return this;
	}
	
	/// Add a piece of comment into the file's [preamble][#preamble]. The
	/// input string is [wrappified][Commons#wrappify(String)] before being
	/// added to the preamble.
	/// 
	/// @param comment comment to add to the preamble (possibly multi-line)
	/// @return this builder
	public FileBuilder addPreamble(String comment) {
		this.preamble.addAll(Commons.wrappify(comment));
		return this;
	}
	
	/// Set the package name for the unit, that will appear in a `package ...;`
	/// Java directive. No check is made on this, so the user must ensure the
	/// package is correct as per the Java standard. Do note that null or blank
	/// or empty package names will be ignored.
	/// 
	/// @param pkg the package name
	/// @return this builder
	public FileBuilder setPackage(String pkg) {
		this.pkg = pkg;
		return this;
	}
	
	/// Add an import to the file. 
	/// 
	/// @param imprt the import to be added
	/// @return this builder
	public FileBuilder addImport(String imprt) {
		if (!this.imports.contains(imprt))
			this.imports.add(imprt);
		return this;
	}
	
	/// Add several imports at once.
	/// 
	/// @param imprts the imports to be added
	/// @return this builder
	public FileBuilder addImports(String... imprts) {
		for (String imprt : imprts)
			this.addImport(imprt);
		return this;
	}
	
	/// Set the file's main type using a continuation. The continuation is preset
	/// with the registered name (as in theory the file and the type's name must
	/// be identical).
	/// 
	/// @param next the continuation for building upon a new type
	/// @return this builder
	public FileBuilder setMainType(ContinueBuilding<TypeBuilder> next) {
		this.mainType = next.andThen(new TypeBuilder().setName(this.name));
		return this;
	}
	
	/// Set the file's main type using a [TypeBuilder] object.
	/// 
	/// @param type the main type as a builder
	/// @return this builder
	public FileBuilder setMainType(TypeBuilder type) {
		this.mainType = type;
		return this;
	}
	
	/// Output the file to the given writer.
	/// 
	/// @param writer the writer to output the file to
	/// @throws IOException possibly thrown by [Writer#append()]
	public void output(Writer writer) throws IOException {
		for (int i = 0; i < this.preamble.size(); i++) {
			if (i == 0)
				writer.append("/* ");
			else
				writer.append(" * ");
			writer.append(this.preamble.get(i))
			      .append('\n');
			
			if (i == this.preamble.size() - 1) {
				writer.append(" */\n");
			}
		}
		
		if (this.pkg != null && !this.pkg.isBlank()) {
			writer.append("package ")
			      .append(this.pkg)
			      .append(';')
			      .append('\n').append('\n');
		}
		
		this.imports.sort(String::compareTo);
		for (String imprt : this.imports) {
			writer.append("import ")
			      .append(imprt)
			      .append(';')
			      .append('\n');
		}
		
		writer.append('\n');
		
		this.mainType.output(0, writer);
		
		writer.append('\n').append('\n');
	}
}
