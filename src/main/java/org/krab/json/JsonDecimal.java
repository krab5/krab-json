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

/// Interface specifying a JSON decimal value, i.e. any number literal that
/// contains a period (`.`) separating a whole part and a fractional part,
/// plus some eventual exponent part (e.g., `1.23`, `.456`, `1.0e+7`, etc.).
/// 
/// The interface requires decimals to be convertible to `double` values, although
/// the implementation may handle other types for various precisions and so on.
/// 
/// @author krab5
/// @version 1.0
public interface JsonDecimal extends JsonValue {
	/// Returns the value contained in that object, as an unboxed double.
	/// 
	/// @return that object's value
	double getValue();
	
	/// Sets the value of the decimal contained in that object, as a double value.
	/// 
	/// @param value the boolean to set this object with
	void setValue(double value);
}
