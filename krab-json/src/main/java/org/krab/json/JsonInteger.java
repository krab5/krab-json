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

/// Interface specifying a JSON integer (i.e., any number literal consisting
/// of a stream of digits and only digits).
/// 
/// @author krab5
/// @version 1.0
public interface JsonInteger extends JsonValue {
	/// Returns the value contained in that object, as an unboxed integer.
	/// 
	/// @return that object's value
	int getValue();
	
	/// Sets the value of the integer contained in that object.
	/// 
	/// @param value the integer to set this object with
	void setValue(int value);
}
