package org.krab.json.path;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.krab.json.*;
import org.krab.json.util.JsonObjectBuilder;

public class JsonPathTest {
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
					.add("def", 196)))
			.build();
	}
	
	@Test
	public void testSimple1() {
		JsonPath jsonPath = new JsonPath(List.of( // /yes -> 0
				new ObjectPathFragment(new GlobStringValueMatcher(false, false, "yes")),
				new ValuePathFragment(new IntegerValueMatcher(Optional.empty(), Optional.empty()), FragmentType.ANY_TYPE)
			));
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(0));
	}
	
	@Test
	public void testSimple2() {
		JsonPath jsonPath = new JsonPath(List.of( // /no -> true
				new ObjectPathFragment(new GlobStringValueMatcher(false, false, "no")),
				new ValuePathFragment(new BooleanValueMatcher(true), FragmentType.BOOLEAN)
			));
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(true));
	}
	
	@Test
	public void testSimplePath1() {
		JsonPath jsonPath = new JsonPath(List.of( // /abc/abcde/abc -> "def"
				new ObjectPathFragment(new GlobStringValueMatcher(false, false, "abc")),
				new ObjectPathFragment(new GlobStringValueMatcher(false, false, "abcde")),
				new ObjectPathFragment(new GlobStringValueMatcher(false, false, "abc"))
			));
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue("def"));
	}
	
	@Test
	public void testSimplePathArray() {
		JsonPath jsonPath = new JsonPath(List.of( // /objects/[1]/abc -> 195
				new ObjectPathFragment(new GlobStringValueMatcher(false, false, "objects")),
				new ArrayPathFragment(Optional.of(new ArrayIndices(new ArrayIndices.Range(1, 1)))),
				new ObjectPathFragment(new GlobStringValueMatcher(false, false, "abc"))
			));
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(195));
	}
	
	@Test
	public void testArrayRange() {
		JsonPath jsonPath = new JsonPath(List.of( // /objects/[*]/abc -> 2, 196
				new ObjectPathFragment(new GlobStringValueMatcher(false, false, "objects")),
				new ArrayPathFragment(Optional.empty()),
				new ObjectPathFragment(new GlobStringValueMatcher(false, false, "def"))
			));
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(2),
				this.factory.makeValue(196));
	}
	
	@Test
	public void testGlob1() {
		JsonPath jsonPath = new JsonPath(List.of( // /abc/abc*/=false
				new ObjectPathFragment(new GlobStringValueMatcher(false, false, "abc")),
				new ObjectPathFragment(new GlobStringValueMatcher(false, true, "abc")),
				new ValuePathFragment(new BooleanValueMatcher(false), FragmentType.ANY_TYPE)
			));
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(false));
	}
	
	@Test
	public void testGlob2() {
		JsonPath jsonPath = new JsonPath(List.of( // /abc/abc*/=false
				new ObjectPathFragment(new GlobStringValueMatcher(false, false, "abc")),
				new ObjectPathFragment(new GlobStringValueMatcher(false, true, "abc")),
				new ValuePathFragment(new BooleanValueMatcher(false), FragmentType.ANY_TYPE)
			));
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(false));
	}
	
	@Test
	public void testGlob3() {
		JsonPath jsonPath = new JsonPath(List.of( // /abc/a*c
				new ObjectPathFragment(new GlobStringValueMatcher(false, false, "abc")),
				new ObjectPathFragment(new GlobStringValueMatcher(false, false, "a", "c"))
			));
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(false),
				this.factory.makeValue(27.0));
	}
	
	@Test
	public void testGlob4() {
		JsonPath jsonPath = new JsonPath(List.of( // /abc/a*c/=*::decimal
				new ObjectPathFragment(new GlobStringValueMatcher(false, false, "abc")),
				new ObjectPathFragment(new GlobStringValueMatcher(false, true, "a", "c")),
				new ValuePathFragment(new AnyValueMatcher(FragmentType.ANY_TYPE), FragmentType.DECIMAL)
			));
		Collection<? extends JsonValue> result = jsonPath.filter(this.object);
		containsAll(result,
				this.factory.makeValue(27.0));
	}

}
