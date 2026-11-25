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

/// This annotation is attached to a record to enable its automatic
/// serialization to and deserialization from JSON. The components of the
/// record must be annotated using the [JsonField] annotation, which associates
/// a _key_ to each component (which will be the key in the resulting JSON
/// object). The type of the field determines how it is handled, see [JsonField]
/// for more info.
/// 
/// A class annotated with `JsonRecord` must satisfy the following criteria:
///  - it must be a _record_
///  - it must implement [Jsonifiable], and subsequently implement the [Jsonifiable#toJson]
///    method
///  - it must provide a public, static `fromJson` method that takes a [JsonValue]
///    as input and creates an instance of the record as output
/// 
/// Under the hood, a record annotated with `JsonRecord` will be associated with
/// a _builder_ class that builds instances of that record (if the record is called
/// `XXX` then the builder is called `XXXBuilder` and is located in the same package).
/// The `build()` method of the builder is the one that instantiate the record based on
/// the annotated components. You may annotate only _some_ of the record's components,
/// **in which case a constructor for the selected fields must be present**, or else
/// the builder will not compile.
/// 
/// The builder is perfectly usable; in addition, it defines a static `toJson(XXX)` that
/// transforms an instance of the record to a JSON value, and a `fromJson(JsonValue)` member
/// method, that takes as input a [JsonValue] and populate the builder's attributes
/// accordingly. You can (and _should_) use these to write the `toJson/fromJson` methods
/// of the annotated record.
/// 
/// @author krab5
/// @version 1.0
@Documented
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface JsonRecord {

}
