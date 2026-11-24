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

/// This package contains the annotation processor ([javax.annotation.processing.Processor]
/// implementation) that handles the `JsonXXX` annotations ([JsonRecord], [JsonClass] and 
/// [JsonField]). See these annotations' documentation for more info on how the processor
/// works in the large.
/// 
/// In term of implementation, the processor only scans for [JsonRecord] and [JsonClass]
/// annotations; [JsonField]s are retrieved per record and used to constitute the builder.
/// [JsonClass]es are not associated with a builder but they are "sanity-checked" to
/// ensures full compatibility with the lib's architecture.
/// 
/// Upon detecting a record annotated with [JsonRecord], the annotation processor checks
/// that the type implements [Jsonifying] and that it contains a static `fromJson` method
/// that creates a record from a [JsonValue]. Then, it gathers the fields and creates a
/// builder class for the record (named `XXXBuilder` for record `XXX`), in the same
/// package as the source record. The builder
/// provides setters and adders for each field, a `build()` function that instantiate the
/// record based on the attribute set by the builder's client. The builder also defines a
/// `toJson` static method for transforming an instance of the record into a `JsonValue`,
/// and a member `fromJson` method that populates the builder's fields using the content
/// of a [JsonValue].
/// 
/// The way the record is exported to and imported from JSON is determined by the [JsonField]
/// annotation's value.
/// 
/// Error reporting for this processor is done through the Java compiler API.
/// 
/// @see JsonRecord
/// @see JsonClass
/// @see JsonField
/// @author krab5
/// @version 1.0
package org.krab.json.annotation.processor;

