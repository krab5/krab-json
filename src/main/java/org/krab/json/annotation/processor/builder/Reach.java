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

/// An enumeration representing the possible _reaches_ or _visibility_ of the
/// various elements in Java (types, fields, methods). Each reach is associated
/// with a keyword, save for `package`.
public enum Reach {
	/// Public reach
	PUBLIC,
	/// Private reach
	PRIVATE,
	/// Protected reach
	PROTECTED,
	/// Package reach (reach when there are no reach keyword)
	PACKAGE;
	
	/// Retrieve the Java keyword associated with this reach.
	/// 
	/// @return a string containing the Java keyword for this reach
	public String getWord() {
		if (this == PACKAGE)
			return "";
		else
			return this.name().toLowerCase();
	}
}
