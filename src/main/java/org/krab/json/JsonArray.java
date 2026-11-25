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

/// Interface specifying a JSON array value. JSON arrays contain a sequence
/// of JSON values (they are heterogeneous, in the sense that they may contain
/// any type of value). Arrays are indeed ordered, making them akin to 
/// [java.util.List]-type collections.
/// 
/// In term of syntax, JSON arrays are delimited with brackets, and values are
/// separated with commas: `["this", "is", "an", "array"]`.
/// 
/// For convenience, the interface extends [java.lang.Iterable], so we can
/// iterate/use for-each on arrays.
/// 
/// Implementations of this interface are assumed to abide by the following rules
/// of consistency: 
///  - faithfulness: _when I add an element `x` to an array `a` of size `n`, then
///    `a.get(n).equals(x)`_
///  - presence: _for array `a`, if `i` is between `0` and `a.getSize() - 1` included,
///    then necessarily `a.get(i)` is not [JsonNull]_
///  - no junk: _for array `a`, if `i` is out of bound, then `a.get(i)` is [JsonNull]_
/// 
/// Size is also required to be positive or 0.
/// 
/// @author krab5
/// @version 1.0
public interface JsonArray extends JsonValue, Iterable<JsonValue> {
	/// Returns the size of the array.
	/// 
	/// @return size of the array, >= 0
	int getSize();
	
	/// Returns the *i*-th element of the array, or [JsonNull] if the index
	/// is out of bound (i.e., `i < 0` or `i >= this.getSize()`).
	/// 
	/// The method **cannot** return `null`.
	/// 
	/// @param i index of the element to be retrieved
	/// @return the value at the *i*-th position in the array, or [JsonNull] if
	///         `i` is out of bound; this return value cannot be `null`
	JsonValue get(int i);

	
	/// Adds a value to the array. The parameter must not be `null`, lest the
	/// behavior of the library is unspecified.
	/// 
	/// As a post condition, the method increments the array's size: `this.getSize()
	///  == \old(this).getSize() + 1`, and the new element is positioned at the previous
	/// size's value: `this.get(\old(this).getSize()) == value`
	/// 
	/// @param value value to be added
	void add(JsonValue value);
}
