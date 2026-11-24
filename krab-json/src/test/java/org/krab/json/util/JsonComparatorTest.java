package org.krab.json.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.krab.json.*;
import org.krab.json.impl.JsonFactoryImpl;

public class JsonComparatorTest {
	private static JsonFactory factory = new JsonFactoryImpl();
	
	private JsonObject obj1, obj2;
	
	@Before
	public void setup() {
		JsonObjectBuilder job = new JsonObjectBuilder(factory);
		job.addObject("abc", builder -> {
			builder
				.add("123", "xxx")
				.add("456", 3.2)
				.addArray("789", arrayBuilder -> {
					arrayBuilder
						.add(1)
						.add(2)
						.add(3);
					return arrayBuilder;
				});
			return builder;
		 }).add("def", 247)
		   .add("jkl", "test")
		   .add("ghi", false);
		this.obj1 = job.build();
		
		JsonObjectBuilder job2 = new JsonObjectBuilder(factory);
		job2.add("abc", 123)
		    .add("def", 456)
		    .addArray("ghi", builder -> builder.add(false).add(true).add(147));
		this.obj2 = job2.build();
	}
	
	@Test
	public void testEqualityRefl1() {
		assertTrue(JsonComparator.jsonEquals(obj1, obj1));
	}
	
	@Test
	public void testEqualityRefl2() {
		JsonObjectBuilder job = new JsonObjectBuilder(factory);
		job.add("abc", 123)
		   .add("def", 456)
		   .addArray("ghi", builder -> builder.add(false).add(true).add(147));
		JsonObject obj = job.build();
		
		assertTrue(JsonComparator.jsonEquals(obj2, obj));
	}
	
	@Test
	public void testEqualityRefl3() {
		JsonObjectBuilder job = new JsonObjectBuilder(factory);
		job.add("abc", 123)
		   .add("def", 456)
		   .addArray("ghi", builder -> builder.add(false).add(true).add(147));
		JsonValue obj = job.build();
		
		assertTrue(JsonComparator.jsonEquals(obj2, obj));
	}
}
