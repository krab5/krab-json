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

import org.krab.json.exception.JsonSchemaException;

/// Utility class containing various JSON miscellaneous general-purpose 
/// static methods.
/// 
/// As every utility class, this class should not be sub-classed (it
/// cannot be sub-classed, in fact, since the only constructor available
/// is private).
/// 
/// @author krab5
/// @version 1.0
public abstract class Json {
	private Json() {}
	
	/// Safely casts a [JsonValue] object into a given type, expressed as a
	/// [java.lang.Class] instance, or throw a [JsonSchemaException] if casting
	/// is not possible.
	/// 
	/// This method is typically used when extracting data, and simplifies code
	/// writing by handling errors with a specific checked exception rather than
	/// general error/runtime exceptions (NPE, typically).
	/// 
	/// Type checking is down based on [java.lang.Class#isAssignableFrom(Class)],
	/// and the type parameter of `targetType` sets up the concrete value for 
	/// type parameter `T`.
	/// 
	/// **Note:** the "unchecked" type casting performed in the method is safe-
	/// guarded by the class comparison/assignment check. In theory the method
	/// cannot raise any [java.lang.ClassCastException].
	/// 
	/// @param <T> type to which the value must be cast; it _must_ be a subtype
	///            of [JsonValue]
	/// @param value value to be cast
	/// @param targetType type the value must be cast to, as a Class object
	/// @throws JsonSchemaException if the value cannot be cast to `T`
	@SuppressWarnings("unchecked") // object-level instanceof in if condition
	public static <T extends JsonValue> T valueAs(JsonValue value, Class<? extends T> targetType)
		throws JsonSchemaException {
		
		if (targetType.isAssignableFrom(value.getClass())) {
			return (T) value;
		}
		
		throw new JsonSchemaException(value, targetType);
	}
}
