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

/// A builder for methods. Methods consist of a [signature][SignatureBuilder]
/// together with a list of instructions. Instructions are "free-form strings"
/// corresponding to (hopefully correct) Java code, that will be output _as is_.
/// 
/// @author krab5
/// @version 1.0
public class MethodBuilder {
	/// The method's signature, as a [SignatureBuilder].
	private SignatureBuilder signature;
	
	/// The method's instructions, as "free-form" strings.
	private List<String> instructions = new ArrayList<>();
	
	
	/// Constructor.
	public MethodBuilder() {}
	
	/// Set the method's signature using a continuation.
	/// 
	/// @param next the continuation for getting the signature
	/// @return this builder
	public MethodBuilder setSignature(ContinueBuilding<SignatureBuilder> next) {
		this.signature = next.andThen(new SignatureBuilder());
		return this;
	}
	
	/// Set the method's signature using a builder directly.
	/// 
	/// @param signature the method's signature
	/// @return this builder
	public MethodBuilder setSignature(SignatureBuilder signature) {
		this.signature = signature;
		return this;
	}
	
	/// Add an instruction at the given indent level. The instruction may
	/// be multi-line, in which case it will be broken down into lines and
	/// indented separately. The indent for each instruction is equal to the
	/// base indent of the method + the given indent level.
	/// 
	/// @param indentlevel the extra level at which the lines will be indented
	/// @param instr instruction line(s) to be added
	/// @return this builder
	public MethodBuilder addInstruction(int indentLevel, String instr) {
		instr.lines().forEach(l -> this.instructions.add(Commons.repeat("    ",  indentLevel) + l));
		return this;
	}
	
	/// Add an instruction as for [#addInstruction(int, String)], with a default
	/// extra indent of 0.
	/// 
	/// @param instr instruction line(s) to be added
	/// @return this builder
	public MethodBuilder addInstruction(String instr) {
		instr.lines().forEach(l -> this.instructions.add(l));
		return this;
	}
	
	/// Add multiple instructions (using [#addInstruction(String)]). Instructions
	/// are added in order of appearance.
	/// 
	/// @param instructions a sequence of instructions to be added
	/// @return this builder
	public MethodBuilder addInstructions(String... instructions) {
		for (String instruction : instructions)
			this.addInstruction(instruction);
		return this;
	}
	
	/// Get the current number of instructions. This is useful for generating 
	/// unique local identifiers, typically.
	/// 
	/// @return current number of instructions registered in this builder
	public int getInstructionNumber() {
		return this.instructions.size();
	}
	
	/// Add a comment. A comment is basically like an instruction starting with `//`.
	/// 
	/// @param comment the comment (**without** the leading `//`)
	/// @return this builder
	public MethodBuilder addComment(String comment) {
		comment.lines().map(c -> "// " + c).forEach(l -> this.instructions.add(l));
		return this;
	}
	
	/// Add a newline.
	/// 
	/// @return this builder
	public MethodBuilder addNewline() {
		this.instructions.add("");
		return this;
	}
	
	/// Output the method represented by this builder to the given writer and at
	/// the given base indent level.
	/// 
	/// @param indent the base indent level for each line of code
	/// @param writer the writer to output the method to
	/// @throws IOException possibly thrown by [Writer#append]
	public void output(int indent, Writer writer) throws IOException {
		int localindent = indent;
		
		this.signature.output(localindent, writer);
		
		writer.append(" {");
		
		if (!this.instructions.isEmpty()) {
			localindent++;
			writer.append('\n');
			
			// Instructions
			for (String instr : this.instructions) {
				writer.append(Commons.indent(localindent))
					  .append(instr)
					  .append('\n');
			}
		
			// End
			localindent--;
			writer.append(Commons.indent(localindent));
		}
		writer.append('}')
		      .append('\n');
	}
}
