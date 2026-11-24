package org.krab.json.impl.parsing;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.krab.json.exception.JsonParseException;

public class BulkLexer implements Lexer {
	private List<Token> tokens = new ArrayList<>();
	private Iterator<Token> iterator;
	
	private boolean done = false;
	
	private final String source;
	private Reader reader;
	
	public BulkLexer(String source, Reader reader) {
		this.source = source;
		this.reader = reader;
	}
	
	public BulkLexer(Reader reader) {
		this("<<??>>", reader);
	}
	
	@Override
	public String getSource() {
		return this.source;
	}
	
	@Override
	public Token nextToken() throws IOException, JsonParseException {
		if (!this.done)
			this.doLexing();
		return iterator.next();
	}
	
	public List<Token> getResult() {
		return List.copyOf(this.tokens);
	}
	
	public boolean isDone() {
		return this.done;
	}
	
	public void doLexing() throws IOException, JsonParseException {
		StreamingLexer lex = new StreamingLexer(this.reader);
		
		Token tkn;
		do {
			tkn = lex.nextToken();
			this.tokens.add(tkn);
		} while (!tkn.isEnd());
		
		this.done = true;
		this.iterator = this.tokens.iterator();
	}
}
