package org.krab.json.path;

import static org.junit.Assert.*;

import org.junit.Test;

public class GlobTest {
	
	@Test
	public void testJokerMatchesAll() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(true, true);
		assertTrue(matcher.matches("abcdef"));
	}

	@Test
	public void testJokerMatchesEmpty() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(true, true);
		assertTrue(matcher.matches(""));
	}
	
	@Test
	public void testPrefix1() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(false, true, "abc");
		assertTrue(matcher.matches("abcdef"));
	}
	
	@Test
	public void testPrefix2() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(false, true, "abc");
		assertTrue(matcher.matches("abc"));
	}
	
	@Test
	public void testPrefix3() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(false, true, "abc");
		assertFalse(matcher.matches("ab"));
	}
	
	@Test
	public void testPrefix4() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(false, true, "abc");
		assertFalse(matcher.matches("bc"));
	}
	
	@Test
	public void testPrefix5() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(false, true, "abc");
		assertFalse(matcher.matches("ababc"));
	}
	
	
	@Test
	public void testSuffix1() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(true, false, "def");
		assertTrue(matcher.matches("abcdef"));
	}
	
	@Test
	public void testSuffix2() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(true, false, "def");
		assertTrue(matcher.matches("def"));
	}
	
	@Test
	public void testSuffix3() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(true, false, "def");
		assertFalse(matcher.matches("ef"));
	}
	
	@Test
	public void testSuffix4() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(true, false, "def");
		assertFalse(matcher.matches("de"));
	}
	
	
	@Test
	public void testInfix1() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(true, true, "cd");
		assertTrue(matcher.matches("abcdef"));
	}
	
	@Test
	public void testInfix2() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(true, true, "cd");
		assertTrue(matcher.matches("cdef"));
	}
	
	@Test
	public void testInfix3() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(true, true, "cd");
		assertTrue(matcher.matches("abcd"));
	}
	
	@Test
	public void testInfix4() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(true, true, "cd");
		assertFalse(matcher.matches("abdef"));
	}
	
	@Test
	public void testInfix5() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(true, true, "cd");
		assertFalse(matcher.matches("abc"));
	}
	
	
	@Test
	public void testSalad1() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(false, false, "ab", "cd", "ef");
		assertTrue(matcher.matches("abcdef"));
	}
	
	@Test
	public void testSalad2() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(false, false, "ab", "cd", "ef");
		assertTrue(matcher.matches("abxxxxxcdef"));
	}
	
	@Test
	public void testSalad3() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(false, false, "ab", "cd", "ef");
		assertTrue(matcher.matches("abxxxxxcdyyyyyyyyyyyyyef"));
	}
	
	@Test
	public void testSalad4() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(false, false, "ab", "cd", "ef");
		assertFalse(matcher.matches("bcdef"));
	}
	
	@Test
	public void testSalad5() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(false, false, "ab", "cd", "ef");
		assertFalse(matcher.matches("abcef"));
	}
	
	@Test
	public void testSalad6() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(false, false, "ab", "cd", "ef");
		assertFalse(matcher.matches("abcdf"));
	}
	
	
	@Test
	public void testTricky() {
		GlobStringValueMatcher matcher = new GlobStringValueMatcher(true, false, "ab", "abc");
		assertTrue(matcher.matches("xxxxxxababc"));
	}
	
}
