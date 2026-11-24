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
package org.krab.json;

/// Interface representing a JSON string value, which is any sequence of
/// symbols delimited by double-quotes (`"`), that may contain escaped characters
/// (`\\uxxxx`, `\n`, `\\`, `\"`, etc.).
/// 
/// @author krab5
/// @version 1.0
public interface JsonString extends JsonValue {
	/// Returns the value contained in that object, as a String.
	/// 
	/// @return that object's value
	String getValue();
	
	/// Sets the value of the string contained in that object.
	/// 
	/// @param value the string to set this object with
	void setValue(String value);
}
