package org.krab.json.impl.parsing;

import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

import org.krab.json.exception.JsonParseException;

public class LexerTestManual {
	private static TokenList lex(String input) throws IOException, JsonParseException {
		StringReader reader = new StringReader(input);
		BulkLexer lex = new BulkLexer(reader);
		lex.doLexing();
		
		reader.close();
		
		if (!lex.isDone())
			throw new RuntimeException("something went terribly wrong");
		
		return new TokenList(lex.getResult());
	}
	
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String line = null;
		
		while (scanner.hasNext()) {
			System.out.print(">>> ");
			System.out.flush();
			line = scanner.nextLine();
			try {
				System.out.println(lex(line));
			} catch (IOException e) {
				System.err.println("IOException caught: " + e.getMessage());
			} catch (JsonParseException e) {
				System.err.println("parse error: " + e.getMessage());
			}
		}
		
		System.out.println("bye.");
		scanner.close();
	}
}
