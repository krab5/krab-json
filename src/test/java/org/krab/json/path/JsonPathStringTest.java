package org.krab.json.path;

import static org.junit.Assert.*;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.krab.json.JsonFactory;
import org.krab.json.JsonObject;
import org.krab.json.JsonValue;
import org.krab.json.util.JsonObjectBuilder;

public class JsonPathStringTest {

	private static void containsAll(Collection<? extends JsonValue> result, JsonValue... content) {
		assertEquals("number of results is not right", content.length, result.size());
		for (JsonValue ct : content) {
			assertTrue(String.format(
					"%s (%s) is not contained in the result", ct.toString(), ct.getClass().getSimpleName()),
					result.stream().anyMatch(x -> x.equals(ct))
				);
		}
	}
	
	JsonObject object;
	JsonFactory factory = JsonFactory.DEFAULT_FACTORY;

	@Before
	public void setUp() {
		// { "abc" : {
		//		"def" : 123,
		// 		"abc" : false,
		//      "test" : [28, 29, 30],
		//      "abcde" : {
		//			"abc" : "def",
		//			"def" : "abc" },
		//      "abbbbc" : 27.0 },
		//	 "yes" : 0,
		//   "no" : true,
		//   "values" : ["a", "b", "c"],
		//   "objects" : [
		//   	{ "abc" : 1, "def" : 2 },
		//		{ "abc" : 195, "def" : 196 }]}
		this.object = new JsonObjectBuilder(this.factory)
			.addObject("abc", ob -> ob
				.add("def", 123)
				.add("abc", false)
				.addArray("test", ab -> ab
					.add(28)
					.add(29)
					.add(30))
				.addObject("abcde", ob2 -> ob2
					.add("abc", "def")
					.add("def", "abc"))
				.add("abbbbc", 27.0))
			.add("yes", 0)
			.add("no", true)
			.addArray("values", ab -> ab
				.add("a")
				.add("b")
				.add("c"))
			.addArray("objects", ab -> ab
				.addObject(ob -> ob
					.add("abc", 1)
					.add("def", 2))
				.addObject(ob -> ob
					.add("abc", 195)
					.add("def", 196))
				.addObject(ob -> ob
					.add("abc", 206)
					.add("def", 207)))
			.build();
	}
	
	@Test
	public void testSimple1() {
		JsonPath jsonPath = JsonPath.makePath("/yes");
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(0));
	}
	
	@Test
	public void testSimple2() {
		JsonPath jsonPath = JsonPath.makePath("/no/::boolean");
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(true));
	}
	
	@Test
	public void testSimplePath1() {
		JsonPath jsonPath = JsonPath.makePath("/abc/abcde/abc");
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue("def"));
	}
	
	@Test
	public void testSimplePathArray() {
		JsonPath jsonPath = JsonPath.makePath("/objects/[1]/abc");
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(195));
	}
	
	@Test
	public void testIntegerRange() {
		JsonPath jsonPath = JsonPath.makePath("/objects/[1]/abc/=[191-200]");
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(195));
	}
	
	@Test
	public void testArrayRange1() {
		JsonPath jsonPath = JsonPath.makePath("/objects/[*]/abc");
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(1),
				this.factory.makeValue(195),
				this.factory.makeValue(206));
	}
	
	@Test
	public void testArrayRange2() {
		JsonPath jsonPath = JsonPath.makePath("/objects/[0,2]/abc");
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(1),
				this.factory.makeValue(206));
	}
	
	@Test
	public void testArrayRange3() {
		JsonPath jsonPath = JsonPath.makePath("/objects/[1-2]/abc");
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(195),
				this.factory.makeValue(206));
	}
	
	
	@Test
	public void testGlob1() {
		JsonPath jsonPath = JsonPath.makePath("/abc/abc*/=false");
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(false));
	}
	
	@Test
	public void testGlob2() {
		JsonPath jsonPath = JsonPath.makePath("/abc/abc*/=false::boolean");
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(false));
	}
	
	@Test
	public void testGlob3() {
		JsonPath jsonPath = JsonPath.makePath("/abc/a*c");
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(false),
				this.factory.makeValue(27.0));
	}
	
	@Test
	public void testGlob4() {
		JsonPath jsonPath = JsonPath.makePath("/abc/a*c/::decimal");
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(27.0));
	}
	
	@Test
	public void testGlob5() {
		JsonPath jsonPath = JsonPath.makePath("/abc/a*c/=*::decimal");
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(27.0));
	}
}
