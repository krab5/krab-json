/* krab-json - <description>
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

/// Exception (checked) representing a problem during the annotation
/// processing. This exception should not escape the scope of the 
/// annotation processor, which should report it using the environment's
/// messager.
/// 
/// @author krab5
/// @version 1.0
public class JsonAnnotationException extends Exception {

	/// Exception serial information (generated)
	private static final long serialVersionUID = -384921022237713609L;
	
	/// Creates an exception from its message and its cause.
	/// 
	/// @param message exception's message, used for error reporting
	/// @param cause an other throwable object that was the cause of this
	///              "wrapping" exception
	public JsonAnnotationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	/// Creates an exception from its message.
	/// 
	/// @param message exception's message, used for error reporting
	public JsonAnnotationException(String message) {
		super(message);
	}
}
