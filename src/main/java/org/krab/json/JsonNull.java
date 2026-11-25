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

/// Interface specifying the null JSON value. Null objects appear when
/// accessing a key of an object or index of an array is unsuccessful, and
/// allows the library to have a finer control over the instances of its
/// interfaces (and avoid Java `null` values and subsequent NPEs).
/// 
/// @author krab5
/// @version 1.0
public interface JsonNull extends JsonValue {}
