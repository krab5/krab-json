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

/// Utility class for common treatments.
/// 
/// @author krab5
/// @version 1.0
public abstract class Commons {
	/// _UNUSED_ private constructor of the class
	private Commons() {}
	
	/// Maximum line width in term of number of characters. Documentation
	/// strings are cut so that they do not exceed this number, and so that
	/// no word is cut (i.e., lines are cut so that the sum of words and 
	/// spaces is below this value).
	public static final int LINE_WIDTH = 80;
	
	/// An indentation block of space (most likely 4 spaces :) ).
	public static final String BASE_INDENT = "    ";
	
	
	/// Create an indent of _n_ times the basic indent space block (by
	/// repeating this block _n_ times).
	/// 
	/// @param n indent level
	/// @return a bunch of space corresponding to a _n_-level indent
	public static String indent(int n) {
		return repeat(BASE_INDENT, n);
	}

	/// Repeat a given string a given amount of time.
	/// 
	/// @param str string to be repeated
	/// @param n number of times the string is to be repeated (positive or null)
	/// @return the resulting repetition
	public static String repeat(String str, int n) {
		String result = "";
		for (int i = 0; i < n; i++)
			result += str;
		return result;
	}
	
	
	/// Take a string comprising multiple lines and format it into a
	/// list of lines such that:
	///   1. each line has a number of character below [#LINE_WIDTH]
	///   2. no word is split nor truncated
	///   3. newlines are added as is (no line merge)
	/// 
	/// @param input the string to format
	/// @return list of lines abiding by the three properties given above
	public static List<String> wrappify(String input) {
		List<String> result = new ArrayList<>();
		String[] lines = input.split("\n");
		
		for (String line : lines) {
			for (String lineline : wrappify1(line))
				result.add(lineline);
		}
		
		return result;
	}
	
	/// Auxiliary function for [#wrappify] that wraps _one_ line of string.
	/// 
	/// @param input a string to wrappify, exempt of any newline character
	/// @return the resulting lines after wrappification
	private static List<String> wrappify1(String input) {
		List<String> result = new ArrayList<>();
		String[] words = input.split(" ");
		
		String current = "";
		int size = 0;
		
		for (String w : words) {
			if (size + 1 + w.length() > LINE_WIDTH) {
				result.add(current);
				current = "";
			}
			if (!current.isEmpty())
				current += " ";
			current += w;
		}
		
		if (!current.isEmpty())
			result.add(current);
		
		return result;
	}
	
	/// Take a list of lines and format it as a JavaDoc comment (delimited with
	/// `/**` and `* /`, and with each line starting with `*`) to output it to
	/// the given writer and at the given indent level. The input lines are
	/// assumed to be already [wrappified][#wrappify()], although this will not
	/// really pose any problem. The lines must be exempt of closing comment
	/// sequence, however.
	/// 
	/// @param indent level of indent to apply to each produced line of JavaDoc
	/// @param writer the writer to output the JavaDoc to
	/// @param javadoc the JavaDoc, as a list of strings
	/// @throws IOException potentially thrown by [Writer#append()]
	public static void outputJavadoc(int indent, Writer writer, List<String> javadoc) throws IOException {
		if (!javadoc.isEmpty()) {
			writer.append(Commons.indent(indent))
			      .append("/**\n");
			for (String line : javadoc) {
			      writer.append(Commons.indent(indent))
			            .append(" * ")
			            .append(line)
			            .append('\n');
			};
			writer.append(Commons.indent(indent))
			      .append(" */\n");
		}
	}
}
