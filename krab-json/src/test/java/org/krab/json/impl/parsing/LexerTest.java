package org.krab.json.impl.parsing;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.junit.Test;
import org.krab.json.exception.JsonParseException;

public class LexerTest {
	
	static Reader stringToReader(String input) {
		return new StringReader(input);
	}
	
	static Token tkn(Token.Type type) {
		return new Token(type, 0, 0);
	}
	
	static Token tkn(String str) {
		return new Token(0, 0, str);
	}
	
	static Token tkn(int val) {
		return new Token(0, 0, val);
	}
	
	static Token tkn(boolean val) {
		return new Token(0, 0, val);
	}
	
	static Token tkn(double val) {
		return new Token(0, 0, val);
	}
	
	static void assertSameTokens(TokenList expected, TokenList actual) {
		boolean sameSize = expected.getSize() == actual.getSize();
		boolean matching = true;
		int i;
		
		for (i = 0; i < expected.getSize() && i < actual.getSize() && matching; i++) {
			matching = expected.get(i).equals(actual.get(i));
		}
		
		i--;
		
		if (!sameSize && !matching)
			fail(String.format(
				"size of token lists mismatch (expected: %d, actual: %d) + "
			  + "token lists disagree at index %d (expected: %s, actual: %s)\n"
			  + "expected list: %s\n"
			  + "actual list  : %s"
				, expected.getSize(), actual.getSize(), i, expected.get(i), actual.get(i),
				  expected.toString(), actual.toString()));
		
		if (!matching)
			fail(String.format(
				"token lists disagree at index %d (expected: %s, actual: %s)\n"
			  + "expected list: %s\n"
			  + "actual list  : %s"
				, i, expected.get(i), actual.get(i), expected.toString(), actual.toString()));
		
		if (!sameSize)
			fail(String.format(
				"size of token lists mismatch (expected: %d, actual: %d)"
			  + "expected list: %s\n"
			  + "actual list  : %s"
				, expected.getSize(), actual.getSize(), expected.toString(), actual.toString()));
	}
	
	@Test
	public void sanityCheck() throws IOException {
		Reader rdr = stringToReader("abcdefghi");
		@SuppressWarnings("unused")
		BulkLexer lx = new BulkLexer(rdr);
		rdr.close();
	}
	
	@Test
	public void testLexingString() throws IOException, JsonParseException {
		Reader rdr = stringToReader("\"abc\"");
		BulkLexer lx = new BulkLexer(rdr);
		lx.doLexing();
		assertTrue(lx.isDone());
		assertSameTokens(
				new TokenList(tkn("abc"), tkn(Token.Type.END)),
				new TokenList(lx.getResult())
			);
	}
	
	@Test
	public void testLexingStringSpaces() throws IOException, JsonParseException {
		Reader rdr = stringToReader("\"  a  bc  \"");
		BulkLexer lx = new BulkLexer(rdr);
		lx.doLexing();
		assertTrue(lx.isDone());
		assertSameTokens(
				new TokenList(tkn("  a  bc  "), tkn(Token.Type.END)),
				new TokenList(lx.getResult())
			);
	}
	
	@Test
	public void testLexingStringEscape1() throws IOException, JsonParseException {
		Reader rdr = stringToReader("\"abc\\\"def\"");
		BulkLexer lx = new BulkLexer(rdr);
		lx.doLexing();
		assertTrue(lx.isDone());
		assertSameTokens(
				new TokenList(tkn("abc\"def"), tkn(Token.Type.END)),
				new TokenList(lx.getResult())
			);
	}
	
	@Test
	public void testLexingStringEscape2() throws IOException, JsonParseException {
		Reader rdr = stringToReader("\"abc\\n\\t\\\\\"");
		BulkLexer lx = new BulkLexer(rdr);
		lx.doLexing();
		assertTrue(lx.isDone());
		assertSameTokens(
				new TokenList(tkn("abc\n\t\\"), tkn(Token.Type.END)),
				new TokenList(lx.getResult())
			);
	}
	
	@Test
	public void testLexingStringUnicode() throws IOException, JsonParseException {
		Reader rdr = stringToReader("\"abc\\u0245\"");
		BulkLexer lx = new BulkLexer(rdr);
		lx.doLexing();
		assertTrue(lx.isDone());
		assertSameTokens(
				new TokenList(tkn("abc\u0245"), tkn(Token.Type.END)),
				new TokenList(lx.getResult())
			);
	}
	
	@Test
	public void testLexingInteger() throws IOException, JsonParseException {
		Reader rdr = stringToReader("123");
		BulkLexer lx = new BulkLexer(rdr);
		lx.doLexing();
		assertTrue(lx.isDone());
		assertSameTokens(
				new TokenList(tkn(123), tkn(Token.Type.END)),
				new TokenList(lx.getResult())
			);
	}
	
	@Test
	public void testLexingDecimal() throws IOException, JsonParseException {
		Reader rdr = stringToReader("5.647");
		BulkLexer lx = new BulkLexer(rdr);
		lx.doLexing();
		assertTrue(lx.isDone());
		assertSameTokens(
				new TokenList(tkn(5.647), tkn(Token.Type.END)),
				new TokenList(lx.getResult())
			);
	}
	
	@Test
	public void testLexingBoolean1() throws IOException, JsonParseException {
		Reader rdr = stringToReader("true");
		BulkLexer lx = new BulkLexer(rdr);
		lx.doLexing();
		assertTrue(lx.isDone());
		assertSameTokens(
				new TokenList(tkn(true), tkn(Token.Type.END)),
				new TokenList(lx.getResult())
			);
	}
	
	@Test
	public void testLexingBoolean2() throws IOException, JsonParseException {
		Reader rdr = stringToReader("false");
		BulkLexer lx = new BulkLexer(rdr);
		lx.doLexing();
		assertTrue(lx.isDone());
		assertSameTokens(
				new TokenList(tkn(false), tkn(Token.Type.END)),
				new TokenList(lx.getResult())
			);
	}

	@Test
	public void testLexingBase1() throws IOException, JsonParseException {
		Reader rdr = stringToReader("{\"abc\":\"def\"}");
		BulkLexer lx = new BulkLexer(rdr);
		lx.doLexing();
		assertTrue(lx.isDone());
		assertSameTokens(
				new TokenList(tkn(Token.Type.OPEN_CURLY), tkn("abc"), tkn(Token.Type.COLON),
						tkn("def"), tkn(Token.Type.CLOSE_CURLY), tkn(Token.Type.END)),
				new TokenList(lx.getResult())
			);
	}
	
	@Test
	public void testLexingBase1ButWithSpaces() throws IOException, JsonParseException {
		Reader rdr = stringToReader("  {    \"abc\"   :     \"def    \"     }    ");
		BulkLexer lx = new BulkLexer(rdr);
		lx.doLexing();
		assertTrue(lx.isDone());
		assertSameTokens(
				new TokenList(tkn(Token.Type.OPEN_CURLY), tkn("abc"), tkn(Token.Type.COLON),
						tkn("def    "), tkn(Token.Type.CLOSE_CURLY), tkn(Token.Type.END)),
				new TokenList(lx.getResult())
			);
	}
	
	@Test
	public void testLexingBase2() throws IOException, JsonParseException {
		Reader rdr = stringToReader("""
				{ "abc" : [ 54, true, 6.7 ],
				  "def" : {
				    "ghi" : true,
				    "jkl" : 12 },
				  "mno" : false }
				""");
		BulkLexer lx = new BulkLexer(rdr);
		lx.doLexing();
		assertTrue(lx.isDone());
		assertSameTokens(
				new TokenList(
						tkn(Token.Type.OPEN_CURLY), tkn("abc"), tkn(Token.Type.COLON),
						tkn(Token.Type.OPEN_BRACKET), tkn(54), tkn(Token.Type.COMMA),
						tkn(true), tkn(Token.Type.COMMA), tkn(6.7), tkn(Token.Type.CLOSE_BRACKET),
						tkn(Token.Type.COMMA), tkn("def"), tkn(Token.Type.COLON), tkn(Token.Type.OPEN_CURLY),
						tkn("ghi"), tkn(Token.Type.COLON), tkn(true), tkn(Token.Type.COMMA),
						tkn("jkl"), tkn(Token.Type.COLON), tkn(12), tkn(Token.Type.CLOSE_CURLY),
						tkn(Token.Type.COMMA), tkn("mno"), tkn(Token.Type.COLON), tkn(false),
						tkn(Token.Type.CLOSE_CURLY), tkn(Token.Type.END)
					),
				new TokenList(lx.getResult())
			);
	}
	
	@Test
	public void testLexingBase3() throws IOException, JsonParseException {
		// /!\ no white space!!
		System.out.println("testLexingBase3");
		Reader rdr = stringToReader("""
				[{"abc":"def","ghi":4},16,{"1":1}]
				""");
		BulkLexer lx = new BulkLexer(rdr);
		lx.doLexing();
		assertTrue(lx.isDone());
		assertSameTokens(
				new TokenList(
						tkn(Token.Type.OPEN_BRACKET), tkn(Token.Type.OPEN_CURLY), tkn("abc"),
						tkn(Token.Type.COLON), tkn("def"), tkn(Token.Type.COMMA), tkn("ghi"),
						tkn(Token.Type.COLON), tkn(4), tkn(Token.Type.CLOSE_CURLY), tkn(Token.Type.COMMA),
						tkn(16), tkn(Token.Type.COMMA), tkn(Token.Type.OPEN_CURLY), tkn("1"),
						tkn(Token.Type.COLON), tkn(1), tkn(Token.Type.CLOSE_CURLY), tkn(Token.Type.CLOSE_BRACKET),
						tkn(Token.Type.END)
					),
				new TokenList(lx.getResult())
			);
	}
	
	// TODO: test exceptions

}
