/* <module> - <description>
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

import java.util.List;

/// Interface specifying a JSON object. JSON objects are basically lists of
/// key-value pairs, where `key` is a string and `value` is any JSON value.
/// 
/// In term of syntax, key-value pairs are given as `key : value`, are delimited 
/// by curly brackets `{}` and separated by commas.
/// 
/// In theory, keys are not stored in any particular order; in practice, an
/// implementation might want to keep track of the order of keys to have a
/// consistent and deterministic serialization output. This specificity is
/// enforced by the default implementation, but is not leveraged in any way
/// by the lib, is thus optional.
/// 
/// Implementations of this interface are assumed to abide by the following rules
/// of consistency:
///  - key uniqueness: _for object `o`, `o.keyList()` does not contain duplicate
///    elements_
///  - presence: _for object `o`, after performing `o.add(k, v)`, we have necessarily
///    `o.keyList().contains(k)`, `o.has(k)` and `o.get(k)` is not [JsonNull]_
///  - no junk: _for object `o`, if `!o.has(k)` then `o.get(k)` is [JsonNull]_
///  - **canonicity**: _for an object `o` and a sequence of **distinct** keys (`kn`), 
///    the sequence of additions `o.add(k1, v1); 
///    o.add(k2, v2); ...; o.add(kn, vn);` leads to a `keyList()` that is exactly
///    `[k1, k2, ..., kn]`_
/// 
/// @author krab5
/// @version 1.0
public interface JsonObject extends JsonValue {
	/// Determines if the given key is present in the object.
	/// 
	/// This method and the [JsonObject#keyList()] method must be consistent with
	/// each other, in the sense that if `has(key)` returns true, then `keyList()`
	/// must contain `key`.
	/// 
	/// @param key key to check; we require this param to be not null, although an
	///        implementation may very well handle such case gracefully
	/// @return true if and only if the key is inside the object
	boolean has(String key);
	
	/// Gets the value mapped to the given key in this object, or [JsonNull] if (and
	/// only if) no such key exists.
	/// 
	/// By design, this method **cannot return `null`**. This _will_ be assumed by 
	/// the library, so not abiding by that requirement is potentially dangerous.
	/// 
	/// @param key the key to lookup in the object (should not be `null`)
	/// @return the object mapped to the key or a [JsonNull] is the key is not mapped
	///         (cannot be `null`)
	JsonValue get(String key);
	
	/// Returns the list of keys mapped in this object, **in the order they were
	/// added**. This specificity assures a form of deterministic/canonical
	/// representation of the object.
	/// 
	/// @return list of keys, in order of addition
	List<String> keyList();
	
	
	/// Add a key-value pair into the object. If the key already exists, overwrites
	/// the associated value (in the way [java.util.Map] does). The object must
	/// keep track of the order the key have been added, to ensure the canonicity
	/// condition; obviously this excludes `add`-ing a key that is already there.
	/// 
	/// Clients of this methods should not assume that the value added to the
	/// object will always be the one obtained with [JsonObject#get(String)].
	/// 
	/// @param key key to be added (should not be `null`)
	/// @param value value mapped to the key (should not be `null`)
	void add(String key, JsonValue value);
}
