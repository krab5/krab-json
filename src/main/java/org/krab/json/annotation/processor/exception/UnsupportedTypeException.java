/* krab-json - Krab's lightweight JSON library
 * Copyright (C) 2026 krab5
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
package org.krab.json.annotation.processor.exception;

/// Exception representing a type that is not supported by the annotation
/// processor (e.g., non-jsonifiable user classes).
/// 
/// @author krab5
/// @version 1.0
public class UnsupportedTypeException extends JsonAnnotationException {
	
	private static final long serialVersionUID = -5096192024258865935L;

	public UnsupportedTypeException(String message) {
		super(message);
	}
}
