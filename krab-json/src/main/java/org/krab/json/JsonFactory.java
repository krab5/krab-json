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

import java.io.Reader;
import java.util.Collection;
import java.util.Map;

import org.krab.json.impl.JsonFactoryImpl;
import org.krab.json.io.JsonParser;

/// `JsonFactory` is the entry point for obtaining `JsonXXX` concrete instances
/// since they are interfaces. The factory itself is an interface, so that one
/// can provide their own implementations for the interface. A default
/// default implementation of the factory is provided (as well as default
/// implementations for each `JsonXXX` interface, consequently), given in the
/// static attribute [JsonFactory#DEFAULT_FACTORY].
/// 
/// The factory also provides a method to create a [JsonParser]. The parser
/// should guarantee that any JSON value constructed is done using this
/// factory.
/// 
/// @author krab5
/// @version 1.0
public interface JsonFactory {
	/// Create a JSON value that is an integer from the given value.
	/// 
	/// @param value contained in the resulting [JsonInteger] object
	/// @return the newly created JSON integer
	JsonInteger makeValue(int value);
	
	/// Create a JSON value that is a decimal from the given double float value.
	/// 
    /// @param value contained in the resulting [JsonDecimal] object
	/// @return the newly created JSON decimal
	JsonDecimal makeValue(double value);
	
	/// Create a JSON value that is a boolean from the given value.
	/// 
	/// @param value contained in the resulting [JsonBoolean] object
	/// @return the newly created JSON boolean
	JsonBoolean makeValue(boolean value);
	
	/// Create a JSON value that is a String from the given value.
	/// 
	/// @param value contained in the resulting [JsonString] object
	/// @return the newly created JSON string
	JsonString makeValue(String value);
	
	
	/// Create the `NULL` value. This special type is mostly used when handling
	/// [JsonObject]s, to encode that a key is not associated with any value, and
	/// [JsonArray]s, to encode that a requested index if out of bound.
	/// This essentially allows the implementation to be `null`-free.
	/// 
	/// @return the JSON null value
	JsonValue makeNull();
	
	
	/// Create an empty JSON object (`{}` in JSON). The resulting [JsonObject] instance
	/// can then be populated using [JsonObject#add(String, JsonValue)].
	/// 
	/// @return new empty JSON object
	JsonObject createObject();
	
	/// Create a JSON object based on a map containing key-JSON value associations.
	/// The resulting [JsonObject] is such that:
	///  - for all key `k` in `input`, `createObject(input).has(k)` is true
	///  - for all key `k` in `input`, `createObject(input).get(k).equals(input.get(k))`
	/// 
	/// The object returned may be edited further using [JsonObject#add(String, JsonValue)].
	/// 
	/// If the input is empty, the call to the method is equivalent to that of
	/// [JsonFactory#createObject()] (without argument).
	/// 
	/// @param input the key-value set to initialize the object with
	/// @return new JSON object encoding the input
	JsonObject createObject(Map<String, ? extends JsonValue> input);
	
	
	/// Create an empty JSON array (`[]` in JSON). The resulting [JsonArray] instance
	/// can then be populated using [JsonArray#add(JsonValue)].
	/// 
	/// @return new empty JSON array
	JsonArray createArray();
	
	/// Create a JSON array based on the content of a collection. The resulting
	/// [JsonArray] has the same size as `input.size()` and contains the same elements,
	/// in no particular order (it may be in the iterator order though).
	/// 
	/// The array returned may be edited further using [JsonArray#add(JsonValue)].
	/// 
	/// If the collection is empty, the call to the method is equivalent to that of
	/// [JsonFactory#createArray()] (without argument).
	/// 
	/// @param input collection to populate the array with
	/// @return new JSON array containing the input collection
	JsonArray createArray(Collection<? extends JsonValue> input);
	
	
	/// Create a JSON parser for the given reader and with the given source name.
	/// Parsers are created for a specific input (provided in the form of a 
	/// [java.io.Reader]), and are associated with a source name, for error 
	/// reporting purposes. Parser creation is safe in general; the parsing
	/// operation (performed through the [JsonParser#parse()] method) is the
	/// one who raises exceptions when the parsing is failing.
	/// 
	/// **Note:** the default parser implementation requires the reader be capable
	/// of performing [java.io.Reader#mark(int)] and [java.io.Reader#reset()]. Failure to
	/// provide this operation will result in an unchecked exception being thrown.
	/// 
	/// @param source name of the source for the parser (file name, `stdin`, etc.)
	///               for error reporting purposes
	/// @param reader input to the parser in the form of a reader
	/// @return the newly created parser
	JsonParser createParser(String source, Reader reader);
	
	
	/// A default_implementation for a factory, which handles the creation of
	/// JSON values with the default implementation bundled with the library.
	public static JsonFactory DEFAULT_FACTORY = new JsonFactoryImpl();
}
