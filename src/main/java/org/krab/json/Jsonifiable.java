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

/// Interface specifying a type that is _jsonifiable_, i.e., that can be turned
/// into a JSON value.
/// 
/// Usually, implementing classes also define a `fromJson(JsonValue)` static method,
/// that allows creating an instance based on a JSON value.
/// 
/// It is a good practice to have `Jsonifiable` instances be annotated with the 
/// [JsonRecord] or [JsonClass] annotations.
/// 
/// @author krab5
/// @version 1.0
public interface Jsonifiable {
	/// Transforms this object into a JSON representation. The result of the transformation
	/// is most likely to be a [JsonObject], but to cover all grounds, a more generic
	/// [JsonValue] is required to be returned.
	/// 
	/// There is no assumption on how the value may be used, so it is recommended the
	/// result be calculated when the method is called, and a different object be
	/// returned for each call (no memoization).
	/// 
	/// @return JSON representation of this object, as a [JsonValue]
	JsonValue toJson();
}
