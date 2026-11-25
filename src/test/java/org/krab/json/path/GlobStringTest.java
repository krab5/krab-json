package org.krab.json.path;

import static org.junit.Assert.*;

import org.junit.Test;

public class GlobStringTest {
	
	@Test
	public void testNoGlob1() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("abcd");
		assertTrue(matcher.matches("abcd"));
	}
	
	@Test
	public void testNoGlob2() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("abcd");
		assertFalse(matcher.matches("abc"));
	}
	
	@Test
	public void testPrefix1() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("abc*");
		assertTrue(matcher.matches("abcdefg"));
	}
	
	@Test
	public void testPrefix2() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("abc*");
		assertFalse(matcher.matches("abdefg"));
	}
	
	@Test
	public void testPrefix3() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("abc*");
		assertTrue(matcher.matches("abc"));
	}
	
	@Test
	public void testSuffix1() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("*def");
		assertTrue(matcher.matches("abcdef"));
	}
	
	@Test
	public void testSuffix2() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("*def");
		assertFalse(matcher.matches("abcef"));
	}
	
	@Test
	public void testSuffix3() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("*def");
		assertTrue(matcher.matches("def"));
	}
	
	@Test
	public void testInfix1() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("abc*def");
		assertTrue(matcher.matches("abcdef"));
	}
	
	@Test
	public void testInfix2() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("abc*def");
		assertTrue(matcher.matches("abcxxxxxxxxdef"));
	}
	
	@Test
	public void testInfix3() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("abc*def");
		assertFalse(matcher.matches("abcef"));
	}
	
	@Test
	public void testNominal1() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("*abc*def*");
		assertTrue(matcher.matches("abcdef"));
	}
	
	@Test
	public void testNominal2() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("*abc*def*");
		assertTrue(matcher.matches("xxxxabcdef"));
	}
	
	@Test
	public void testNominal3() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("*abc*def*");
		assertTrue(matcher.matches("abcxxxxxdef"));
	}
	
	@Test
	public void testNominal4() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("*abc*def*");
		assertTrue(matcher.matches("abcdefxxxxxxxxx"));
	}
	
	@Test
	public void testNominal5() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("*abc*def*");
		assertTrue(matcher.matches("xxabcxxxdefxxx"));
	}

	@Test
	public void testEscaped1() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("ab\\**cd\\*");
		assertTrue(matcher.matches("ab*xxxxxcd*"));
	}
	
	@Test
	public void testEscaped2() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("ab\\**cd\\*");
		assertFalse(matcher.matches("abxxxxxcd*"));
	}
	
	@Test
	public void testEdge1() {
		GlobStringValueMatcher matcher = GlobStringValueMatcher.fromString("a*c");
		assertTrue(matcher.matches("abxxxxxc"));
	}

}
