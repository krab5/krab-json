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

import org.krab.json.util.JsonVisitor;

/// The general interface for any JSON value, extended by the various interfaces
/// of this package.
/// 
/// @author krab5
/// @version 1.0
public interface JsonValue {
	/// Accept and handle the given visitor using the given parameters.
	/// 
	/// @param <P> type of the parameter to pass along to the visitor
	/// @param visitor visitor to accept
	/// @param param initial value of the value to be pass along to the visitor
	/// @throws Throwable generic exception to let the visitor fail however it wants
	<P> void accept(JsonVisitor<P> visitor, P param) throws Throwable;
	
	@Override boolean equals(Object object);
}
