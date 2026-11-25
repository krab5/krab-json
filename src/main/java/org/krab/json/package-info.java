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

/// This is the main entry point to the handling of JSON constructs in the library.
/// 
/// Save for the `Json` class, elements of this package are _interfaces_. The only
/// way to obtain concrete instances is through _factories_ ([JsonFactory]), which
/// essentially allows one to provide their own implementation, under the condition
/// that such implementation abides by the documented requirements. A reasonable 
/// default implementation is provided with this library, accessible through the 
/// default factory ([JsonFactory#DEFAULT_FACTORY]).
/// 
/// The only way to obtain
/// 
/// `Json` is an utility class, not aimed at being instantiated.
/// 
/// @author krab5
/// @version 1.0
/// @see org.krab.json.JsonFactory
/// @see org.krab.json.Json
package org.krab.json;

