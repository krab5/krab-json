package org.krab.json.impl.parsing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;
import org.krab.json.*;
import org.krab.json.exception.JsonParseException;
import org.krab.json.impl.JsonFactoryImpl;
import org.krab.json.util.JsonArrayBuilder;
import org.krab.json.util.JsonObjectBuilder;

public class ParserTest {
	private static JsonFactory factory = new JsonFactoryImpl();
	
	static Parser makeParser(String input) {
		return new Parser(factory, new StringReader(input));
	}

	@Test
	public void testSingleValue1() throws IOException, JsonParseException {
		JsonValue value = makeParser("\"abc def \\u31cc \"").parse();
		assertEquals(factory.makeValue("abc def \u31cc "), value);
	}
	
	@Test
	public void testSingleValue2() throws IOException, JsonParseException {
		JsonValue value = makeParser("true").parse();
		assertEquals(factory.makeValue(true), value);
	}
	
	@Test
	public void testSingleValue3() throws IOException, JsonParseException {
		JsonValue value = makeParser("123").parse();
		assertEquals(factory.makeValue(123), value);
	}
	
	@Test
	public void testSingleValue4() throws IOException, JsonParseException {
		JsonValue value = makeParser("12.3").parse();
		assertEquals(factory.makeValue(12.3), value);
	}
	
	@Test
	public void testArray1() throws IOException, JsonParseException {
		JsonValue expected = factory.createArray();
		JsonValue value = makeParser("[]").parse();
		assertEquals(expected, value);
	}
	
	@Test
	public void testArray2() throws IOException, JsonParseException {
		JsonValue expected = new JsonArrayBuilder(factory).add(51).add(true).add("cde").build();
		JsonValue value = makeParser("[51, true  ,   \"cde\" ]").parse();
		assertEquals(expected, value);
	}
	
	@Test
	public void testObject1() throws IOException, JsonParseException {
		JsonValue expected = factory.createObject();
		JsonValue value = makeParser("{}").parse();
		assertEquals(expected, value);
	}
	
	@Test
	public void testObject2() throws IOException, JsonParseException {
		JsonValue expected = new JsonObjectBuilder(factory)
			.add("abc", 123)
			.add("def", false)
			.add("ghi", "test")
			.build();
		JsonValue value = makeParser("""
				{ "abc" : 123, "def" : false, "ghi" : "test" }
				""").parse();
		assertEquals(expected, value);
	}
	
	@Test
	public void testNested1() throws IOException, JsonParseException {
		JsonValue expected = new JsonArrayBuilder(factory)
			.addArray(ab2 -> ab2
				.add(42)
				.add("test"))
			.add(false)
			.addArray(ab2 -> ab2
				.add(true))
			.build();
		JsonValue value = makeParser("""
				[[42,"test"],false,[true]]
				""").parse();
		assertEquals(expected, value);
	}
	
	@Test
	public void testComplex1() throws IOException, JsonParseException {
		JsonValue expected = new JsonObjectBuilder(factory)
				.add("abc", 123)
				.addArray("def", ab -> ab.add(12).add(14.2).add("test").add(true))
				.addObject("ghi", ob -> ob
					.add("123", false)
					.add("456", "test2"))
				.addArray("jkl", ab -> ab
					.addObject(ob -> ob.add("1", 1).add("2", 2))
					.addObject(ob -> ob.add("3", 3).add("4", 4)))
				.build();
			JsonValue value = makeParser("""
					{ "abc" : 123,
					  "def" : [12, 14.2, "test", true],
					  "ghi" : {
					  	"123" : false,
					  	"456" : "test2"
					  },
					  "jkl" : [
					  	{ "1" : 1, "2" : 2 },
					  	{ "3" : 3, "4" : 4 }
					  ]
					}
					""").parse();
			assertEquals(expected, value);
	}

}
