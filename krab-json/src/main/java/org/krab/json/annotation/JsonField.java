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
package org.krab.json.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/// This annotation is tied to a _record component_ (or _field_) and "registers"
/// it to be taken into account by the annotation processor. The field is identified
/// by a key that will be used during JSON (de)serialization. The type of the field is
/// used to determine how to handle the input/output [JsonValue] associated with said
/// key:
///  - `int`, `double`, `boolean` and their boxed versions are turned into the corresponding
///    `JsonXXX` type and are (de)serialized accordingly (by `XXX.parseXXX`, probably)
///  - `String` is turned into [JsonString] and is input and output as is
///  - a list, set or array of optional, list/set, arrays or jsonifiable type is turned into a
///    [JsonArray], where values are (de)serialized using the method associated with the
///    inner type
///  - an optional of optional, list/set, arrays or jsonifiable type is turned into the
///    [JsonValue] associated with the inner type; the optional aspect allows a key to
///    not be present upon transformation from JSON (by default a declared field must be
///    provided, for obvious reasons)
///  - a [Jsonifiable] object is turned into a [JsonValue], which is the one obtained by
///    [Jsonifiable#toJson] or input in `fromJson` to obtain an instance of the field
/// 
/// For enumerations, the annotation processor supports parsing/output of literals either
/// by value (ordinal, an integer) or by name (a string). The output is by value by default,
/// and is meaningless (and unused) for non-enum fields.
/// 
/// Example:
/// ```java
/// @JsonRecord
/// public class MyRecord (@JsonField("my_field") int field) ...
/// ...
/// new MyRecord(12) -> { "my_field" : 12 }
/// ```
/// 
/// 
/// 
/// @author krab5
/// @version 1.0
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.RECORD_COMPONENT)
public @interface JsonField {
	/// Mode for handling enumeration fields.
	public static enum EnumMode {
		/// Handle enumeration literals using their _name_ (string)
		BY_NAME,
		/// Handle enumeration literals using their _value_ (integer)
		BY_VALUE;
	}
	
	/// The value of the annotation is its "JSON name", i.e. the key that will be
	/// used during (de)serialization of the enclosing record. There are no
	/// real rule in term of what string can be used; traditionally, JSON keys are
	/// written in snake case, without spaces and starting with an alphanumeric 
	/// character.
	/// 
	/// @return JSON field key associated with the field
	String value();
	
	/// For fields which type is an enumeration (or a composite of an enumeration such
	/// as a collection or optional), determines how enum literals are being (de)serialized.
	/// 
	/// By default, the mode is _by value_ (i.e. as integers).
	/// 
	/// @return the mode for enumeration (de)serialization
	EnumMode mode() default EnumMode.BY_VALUE;
}
