/* krab-json - Krab's lightweight JSON library
 * Copyright (C) 2026 Krab
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

/// This library constitutes both a lightweight JSON set of features as well
/// as an annotation-based system to automatically serialize and deserialize
/// any compatible object to and from JSON values.
/// 
/// The main feature of the library are as such:
///  - basic JSON features :
///    + standard-based JSON value model (objects, arrays, integers, decimals,
///      strings and booleans)
///    + fully compliant streaming-based JSON parser
///    + fully compliant, configurable JSON writer
///    + simple and reasonably efficient JSON path query system
///  - annotation system
///    + annotate records and record field to register an associated JSON
///      representation
///    + auto-detect field-to-JSON exporting and importing schemes, featuring
///      a number of supported types: `int`, `boolean`, `double` (decimal),
///      strings, lists and sets and array (JSON array), any compatible
///      types
///    + generates builders from annotations + to and from JSON methods
/// 
/// ## Basics of JSON and the interfaces
/// 
/// A JSON value is given by the following EBNF (spaces that are not found
/// as part of the `string` non-terminal are ignored):
/// 
/// ```
/// digit = 0 | ... | 9 ;
/// integer = digit, { digit } ;
/// decimal = integer, ".", integer ;
/// 
/// boolean = "true" | "false" ;
/// 
/// character  = <any unicode character put " and \> ;
/// characteresc = character | '\"' | '\\' ;
/// string = '"', { characteresc }, '"'
/// 
/// value = integer
///       | decimal
///       | boolean
///       | string
///       | array
///       | object
///       ;
/// 
/// array = "[", { value }, "]" ;
/// 
/// keyval = string, ":", value ;
/// object = "{", { keyval }, "}" ;
/// ```
/// 
/// This EBNF is mirrored by the architecture of interfaces found in
/// [org.krab.json], with the added [org.krab.json.JsonNull] value, which
/// is associated with missing keys and invalid indices, typically.
/// 
/// JSON values are not to be handled directly, but only through the interfaces
/// of the library. `JsonValue`s are created through [org.krab.json.JsonFactory],
/// which is a factory and exposes a default implementation.
/// 
/// Note that `JsonArray`s and `JsonObject`s may be non-homogenous (as per JSON's
/// specification).
/// 
/// As an example, take the following JSON object:
/// 
/// ```
/// { "person" : { "first_name" : "Abc", "last_name" : "Def" },
///   "salary" : 12000,
///   "vacations" : [ "Jan", "Feb", "Aug" ]
/// }
/// ```
/// 
/// This would correspond to the following code:
/// 
/// ```java
/// JsonFactory factory = JsonFactory.DEFAULT_FACTORY;
/// 
/// JsonObject person = factory.createObject();
/// person.put("first_name", factory.makeValue("Abc"));
/// person.put("last_name", factory.makeValue("Def"));
/// 
/// JsonArray vacations = factory.createArray();
/// vacations.add(factory.makeValue("Jan"));
/// vacations.add(factory.makeValue("Feb"));
/// vacations.add(factory.makeValue("Aug"));
/// 
/// JsonObject employee = factory.createObject();
/// employee.put("person", person);
/// employee.put("salary", factory.makeValue(12000));
/// employee.put("vacations", vacations);
/// ```
/// 
/// 
/// Of course, this looks tedious, so you might want to use the convenient builders,
/// [org.krab.json.util.JsonArrayBuilder] and [org.krab.json.util.JsonObjectBuilder]:
/// 
/// ```java
/// JsonObject employee = new JsonObjectBuilder()
///     .addObject("person", ob -> ob
///         .add("first_name", "Abc")
///         .add("last_name", "Def"))
///     .add("salary", 12000)
///     .addArray("vacations", ab -> ab
///         .add("Jan")
///         .add("Feb")
///         .add("Aug"))
///     .build();
/// ```
/// 
/// ## Parsing and exporting
/// 
/// The factory can create a [org.krab.json.io.JsonParser] instance from a
/// [java.io.Reader] object representing a streamable JSON source (which can be a file but
/// also a `String` using [java.io.StringReader]). Here is the general use of it:
/// 
/// ```java
/// Reader reader = ...; // a file or a string or whatever
/// JsonParser parser = JsonFactory.DEFAULT_FACTORY.createParser("myfile.json", reader);
/// JsonValue value = parser.parse();
/// ```
/// 
/// The first argument of `createParser` is the name of the source for the JSON to be parsed.
/// This is mainly used to improve error reporting.
/// 
/// The default implementation of the `JsonParser` returned by the default factory is a
/// _streaming_ parser, in the sense that the parser takes tokens as needed, and taking
/// a token is what causes the input to be read.
/// 
/// Of course, the parser is called with [JsonParser#parse()] which reads the entire
/// JSON source.
/// 
/// Parsing error are represented by [JsonParseException]. [IOException] is raised upon
/// failing to use the reader.
/// 
/// **TODO**: event-based JSON parsing
/// 
/// <br>
/// 
/// Dually, we can export JSON objects into strings using a JSON "exporter"
/// ([org.krab.json.io.JsonExporter]). The exporter takes a [java.io.Writer] as input, which
/// is the target to which the JSON is to be exported. The `export()` method does the exporting
/// and fails with an [IOException] when failing to use the writer (apart from that the method
/// should not fail).
/// 
/// The exporter is configurable for different newline characters, and more importantly with a
/// style modifier, either `PRETTY` to properly space, newline and indent the output, `SPACE`
/// to write everything on one line but with spaces, and `COMPACT` to remove non-significant
/// spaces.
/// 
/// 
/// ## Paths and querying JSONs
/// 
/// JSON being a hierarchical, it is reasonable to envision a way to query values at a given
/// succession of "steps" from the root, in the fashion of XPath for XML. This is the origin
/// of the path-based querying system of this lib, concentrated in class [org.krab.json.path.JsonPath].
/// 
/// The basics of the path system is as such:
///  - a _path_ is composed of _fragments_, each of which begins with a slash (`/`)
///  - fragments are of 3 natures: value fragments, array fragments, and object fragments
///  - value fragments start with an equal sign (`=`) and match as scalar JSON value (integer,
///    decimal, boolean or string); after the sign, it is possible to provide specific
///    matching:
///    + integer values can be matched with a precise value (given as is) or with a range of
///      the form `[<lower bound>-<upper bound>]` where both bounds are exclusively optional
///      (in which case it can be assumed to mean "as high/low as possible"); intervals must
///      be inhabited (lower &leq; upper)
///    + decimal values can be matched with a precise value (given as is) or with a range of
///      the form `[[<lower bound>-<upper bound>]]` where both bounds are exclusively optional
///      (in which case it can be assumed to mean "as high/low as possible"); intervals must
///      be inhabited (lower &leq; upper)
///    + boolean values can be matched with a precise literal (`true`, `false`), written as is
///    + string values can be matched with either a glob in between single (`'`) or double (`"`) 
///      quotes, or a regular expression in between exclamation points (`!`)
///    + matching any value is done using `*`; in that case the fragment can filter based on 
///      type, using `::<type>`, where `<type>` is any of `integer`, `decimal`, `boolean`,
///      `string` or `*`, the latter matching any type; in practice, type can be specified for
///      any value, but it must be consistent with the type of the "matcher"
///  - array fragments allows "descending" in an array by selecting specific indices, which
///    are expressed as a list of ranges, each range being a lower and upper bound, or just one
///    number; the set of indices must be wrapped in brackets (`[l1-u1,l2-u2,...]`), and the
///    special star (`*`) symbol may be used to match any index
///  - object fragments allows "descending" in an object using a specific set of keys, which is
///    build by using globs or regular expressions (delimited with bands (`!`)); globs may be 
///    wrapped in single or double quotes
/// 
/// An EBNF grammar of this specification is given below:
/// ```
/// path = { "/", fragment } ;
/// 
/// fragment = valuefragment | objectfragment | arrayfragment ;
/// 
/// valuefragment = "::", valuetype
///               | "=", valuematcher, [ "::", valuetype ] ;
/// valuetype = "integer" | "decimal" | "boolean" | "string" | "*" ;
/// valuematcher = '"', glob, '"' | "'", glob, "'" | "!", regex, "!"
///              | integer | "[", [ integer ], "-", [ integer ], "]"
///              | decimal | "[[", [ decimal ], "-", [ decimal ], "]]"
///              | "true" | "false"
///              | "*" ;
/// 
/// objectfragment = glob | "'", glob, "'" | '"', glob, '"' ;
/// 
/// arrayfragment = "[", arrayindices, "]" ;
/// arrayindices = arrayindice, ",", arrayindices | ;
/// arrayindice = integer, [ "-", integer ] | "*" ;
/// ```
/// 
/// Globs are sequences of letter interrupted by (non-escaped) stars (`*`), that represent
/// the set of all strings comprised of the letters in the same order, between which any
/// number (potentially 0) of letters can be interspersed. For instance, `this*is*a*glob`
/// matches `thisisaglob`, `this is a glob`, `thisisxxxxaglob`, etc.
/// 
/// In a glob, the asterisk may be escaped using a backslash (`\`) to match the asterisk
/// as a character. As usual, the backslash can itself be escaped to match the backslash
/// as a character. For instance: `this\*is*a\\glob` matches `this*isxxxxa\glob`, and not
/// `this is a\\glob`.
/// 
/// Regular expressions or regexes are delimited with bangs (`!`). Anything between two
/// bangs can be any Java-compatible regular expression. Inner bangs may be escaped with
/// a backslash, i.e.: `!abc\!def!` to avoid early termination. A glob that is outside 
/// quotations (an object key set typically) may start with a bang by escaping it as
/// well.
/// 
/// Other than that, integers and decimals are parsed as usual (using a `parseXXX` 
/// function native to Java).
/// 
/// <br>
/// 
/// The main method for "applying" a path to a set of values is [org.krab.json.JsonPath#filter(Collection)].
/// What this method essentially does is to filter the values that matches the first
/// fragment of the path, drop that fragment and visit the values, and do it again until
/// no fragment (or no value) remains.
/// 
/// More concretely, the filter works as such:
///  - if the head of the path is a value fragment, filter out the non-value elements of
///    the input and filter out the remaining values that do not match the associated
///    value matcher
///  - if the head of the path is an object fragment, filter out the non-object elements
///    of the input, then gather all the values associated with a key matched by the
///    fragment in each object
///  - if the head of the path is an array fragment, filter out the non-array elements
///    of the input, then gather all the values at the indices provided by the fragment
///    in each array
/// 
/// Here is an example:
/// ```
/// { "abc" : 1,
///   "aec" : {
///     "x" : 15,
///     "y" : -11 },
///   "afc" : {
///     "x" : 1.2,
///     "y" : true,
///     "z" : ["test", 0, false] }}
/// ```
/// 
///  - path `/aec/x` returns only the integer `15`
///  - path `/aec/y/::integer` returns only the integer `-11`
///  - path `/afc/y/::integer` returns nothing
///  - path `/afc/z/[1]` returns the integer `0`
///  - path `/afc/z/[0,2]` returns the string `"test"` and the boolean `false`
///  - path `/a*c/x` returns the integer `15` and the decimal `1.2
///  - path `/!.*c!/*/[0-2]/::boolean` returns the boolean `false`
/// 
/// 
/// ## The annotation system
/// 
/// The annotation system allows automating the creation of translations from an object to
/// a JSON object and back. The system is intended for use with _records_ mostly, although
/// it is possible to annotate a class, with much less automation, unfortunately.
/// 
/// The two mains annotations to use are [org.krab.json.annotation.JsonRecord] which
/// identifies a record as being "jsonifiable", and [org.krab.json.annotation.JsonField]
/// which identifies a field of the record as to be serialized and give it a JSON key.
/// 
/// Note that a `@JsonRecord` record must implement `org.krab.json.Jsonifiable` (and thus
/// implement the `toJson` method), and must define a public, static 
/// `<record> fromJson(JsonValue)` method. Their implementation may (and probably
/// should) be based on the equivalent methods in the generated _builder_ (see below).
/// 
/// A typical use of these annotations looks like this:
/// 
/// ```java
/// @JsonRecord
/// public record Person (
///         @JsonField("first_name") String firstName,
///         @JsonField("last_name") String, lastName,
///         @JsonField("birth_year") int yearOfBirth) 
///         implements Jsonifiable {
/// ...
///     @Override
///     public JsonValue toJson() {
///         return PersonBuilder.toJson(this);
///     }
/// 
///     public static Person fromJson(JsonValue value) throws JsonSchemaException {
///         return new PersonBuilder().fromJson(value).build();
///     }
/// }
/// ```
/// 
/// These annotations are handled by a registered annotation processor that will
/// derive any information relevant to (de)jsonification. A JSON "value type" is
/// associated with the field based on its type, according to the following rules:
///  - primitive types `int`, `double`, `boolean` and `String` are transposed to their
///    JSON counterparts (`JsonInteger`, `JsonDecimal`, `JsonBoolean`, and `JsonString` respectively)
///  - boxed types are associated to their counterparts as well
///  - arrays, list and sets are transposed to `JsonArray`s
///  - enumerations are transposed to either strings or integers, based on the `mode` argument of 
///    `@JsonField`
///  - any jsonifiable object are transposed to their value representation based on `to/fromJson`
/// 
/// Then, the processor creates a _builder_ class for the annotated record, named `<record>Builder`,
/// that in particular defines a static `toJson(<record>)` method and a `fromJson(JsonValue)` member
/// method, that may be used in the record's implementation of the associated methods.
/// 
/// As mentioned, enumeration may be exported/imported based on either their _value_ (or "ordinal"
/// an integer) or by _name_ (as a string). This is selected using the `mode` argument of the
/// annotation, which takes an [EnumMode] value, either `BY_VALUE` or `BY_NAME`. By default, enums
/// are encoded using integers (by value).
/// 
/// ```java
/// public record Observer (
///         @JsonField("coming_from") Direction comingFrom, // by value/ordinal
///         @JsonField(value="looking_towards", mode=EnumMode.BY_NAME) // argument names have to be made explicit here
///     ) implements Jsonifiable {
///     public enum Direction {
///         NORTH, SOUTH, EAST, WEST;
///     }
/// ...
/// }
/// 
/// new Observer(Direction.NORTH, Direction.SOUTH).toJson();
/// // -> { "coming_from" : 0, "looking_towards" : "SOUTH" }
/// 
/// ```
///
/// Classes may be annotated using `@JsonClass` but this is not associated to any automation.
/// Instead, the processor only checks the presence of the `Jsonifiable` implementation and of
/// the `fromJson` method. It is good practice to use that annotation on any jsonifiable class
/// nonetheless.
/// 
/// 
/// ### The builder
/// 
/// As mentioned previously, any record `<record>` annotated with the `@JsonRecord` annotation
/// is associated with a `<record>Builder` class generated by the annotation processor. Said
/// builder is perfectly usable even apart from the `to/fromJson` methods!
/// 
/// The builder features:
///  - one `set<Field>(<fieldType>)` method for each `<field>`, that sets that field's value in
///    the builder (the first letter of the field is upper-cased to match Java's usual convention, 
///    e.g. `firstName` becomes `setFirstName`)
///  - for collections of elements of type `<elementType>`, an additional `add<Field>(<elementType>)`
///    that adds an element to the builder's collection
///  - for optionals on an element of type `<elementType>`, an additional `set<Field>(<elementType>)`
///    that sets the value of the optional
///  - a `build()` method that returns an instance of `<record>` with each field set with the
///    corresponding internal builder attribute
///  - a public, static `toJson(<record>)` method that transforms an instance of `<record>` into a
///    JSON object
///  - a `fromJson(JsonValue)` method that integrates uses the input value to set the components
///    of the builder
/// 
/// @author krab5
/// @version 1.0.0
/// @see org.krab.json
/// @see org.krab.json.annotation
/// @see org.krab.json.path
/// @provides javax.annotation.processing.Processor annotation processor associated with
///   the annotations provided in [org.krab.json.annotation].
/// 
module org.krab.json {
	requires transitive static java.compiler;
	
	exports org.krab.json;
	exports org.krab.json.annotation;
	//exports org.krab.json.annotation.processor;
	exports org.krab.json.exception;
	exports org.krab.json.io;
	exports org.krab.json.util;
	
	provides javax.annotation.processing.Processor with org.krab.json.annotation.processor.JsonAnnotationProcessor;
}
