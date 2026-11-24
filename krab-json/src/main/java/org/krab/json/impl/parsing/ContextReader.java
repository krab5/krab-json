package org.krab.json.impl.parsing;

import java.io.IOException;
import java.io.Reader;

public class ContextReader {
	private Reader reader;
	private final String source;
	private int line = 1, column = 1;
	
	public ContextReader(String source, Reader reader) {
		this.source = source;
		this.reader = reader;
	}
	
	public ContextReader(Reader reader) {
		this("<<??>>", reader);
	}
	
	public int read() throws IOException {
		int c =  this.reader.read();
		
		if (c == '\n') {
			this.line++;
			this.column = 1;
		} else {
			this.column++;
		}
		
		return c;
	}
	
	public int read(boolean ignoringSpaces) throws IOException {
		int c = this.read();
		if (ignoringSpaces) {
			while (c > 0 && Character.isWhitespace(c)) {
				c = this.read();
			}
		}
		return c;
	}
	
	public String read(int number) throws IOException {
		String result = "";
		for (int i = 0; i < number; i++)
			result += (char) this.read();
		return result;
	}
	
	public void skip(int number) throws IOException {
		for (int i = 0; i < number; i++)
			this.read();
	}
	
	public void mark(int maxread) throws IOException {
		this.reader.mark(maxread);
	}
	
	public void reset() throws IOException {
		this.reader.reset();
	}
	
	public String getSource() {
		return this.source;
	}
	
	public int getLine() {
		return this.line;
	}
	
	public int getColumn() {
		return this.column;
	}
}
