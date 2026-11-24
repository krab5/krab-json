package org.krab.json.impl.parsing;

import java.io.IOException;
import java.io.Reader;
import java.util.Set;

import org.krab.json.exception.JsonParseException;

public class StreamingLexer implements Lexer {
	static final Set<Character> Boundaries = Set.of(
			',', ':', '[', ']', '{', '}'
		);
	
	private ContextReader reader;
	
	public StreamingLexer(String source, Reader reader) {
		this.reader = new ContextReader(source, reader);
	}
	
	public StreamingLexer(Reader reader) {
		this.reader = new ContextReader(reader);
	}
	
	@Override
	public String getSource() {
		return this.reader.getSource();
	}
	
	@Override
	public Token nextToken() throws IOException, JsonParseException {
		int c = this.reader.read(true);
		
		switch (c) {
			case '{': return new Token(Token.Type.OPEN_CURLY, this.reader.getLine(), this.reader.getColumn());
			case '}': return new Token(Token.Type.CLOSE_CURLY, this.reader.getLine(), this.reader.getColumn());
			case '[': return new Token(Token.Type.OPEN_BRACKET, this.reader.getLine(), this.reader.getColumn());
			case ']': return new Token(Token.Type.CLOSE_BRACKET, this.reader.getLine(), this.reader.getColumn());
			case ',': return new Token(Token.Type.COMMA, this.reader.getLine(), this.reader.getColumn());
			case ':': return new Token(Token.Type.COLON, this.reader.getLine(), this.reader.getColumn());
			case '"': {
				int currLine = this.reader.getLine();
				int currCol = this.reader.getColumn();
				return new Token(currLine, currCol, this.accumulateWholeString());
			}
			default: {
				int currLine = this.reader.getLine();
				int currCol  = this.reader.getColumn();
				if (Character.isDigit(c)) {
					String number = "" + ((char) c) + this.accumulateUntilBoundary();
					
					try {
						if (number.indexOf('.') > 0) { //decimal
							return new Token(currLine, currCol, Double.parseDouble(number));
						} else {
							return new Token(currLine, currCol, Integer.parseInt(number));
						}
					} catch (NumberFormatException e) {
						this.throwParseException(currLine, currCol, "ill-formed number: " + number);
					}
				} else if (c == 't' || c == 'f') {
					String boolValue = "" + ((char) c) + this.reader.read(c == 't' ? 3 : 4);
					if ("true".equals(boolValue))
						return new Token(currLine, currCol, true);
					else if ("false".equals(boolValue))
						return new Token(currLine, currCol, false);
					else
						this.throwParseException(currLine, currCol, "unexpected free literal '" + ((char) c) + "'");
				} else if (c <= 0) {
					return new Token(Token.Type.END, this.reader.getLine(), this.reader.getColumn());
				} else {
					this.throwParseException("unexpected free literal '" + ((char) c) + "'");
				}
			}
		}
		
		return null;
	}
	
	private String accumulateUntilBoundary() throws IOException {
		StringBuilder result = new StringBuilder();
		this.reader.mark(2); // We need to mark once in case the next character is the boundary or EOS
		int c = this.reader.read();
		while (c > 0 && !Boundaries.contains((char) c) && !Character.isWhitespace(c)) {
			this.reader.mark(2);
			result.append((char) c);
			c = this.reader.read();
		}
		
		// this effectively moves the cursor back 1 character (because you have to
		// read the boundary to exit the loop but you want to parse it by its own right)
		if (c > 0)
			this.reader.reset();
		
		return result.toString();
	}
	
	private String accumulateWholeString() throws IOException, JsonParseException {
		int currLine = this.reader.getLine();
		int currCol = this.reader.getColumn();
		String result = "";
		boolean escaping = false;
		int c = this.reader.read();
		while (c > 0 && (escaping || c != '"')) {
			if (!escaping && c == '\\') {
				escaping = true;
			} else {
				if (escaping) {
					if (c == 'u' || c == 'x') { //unicode or ASCII
						int point = 0;
						try {
							point = Integer.parseInt(this.reader.read(c == 'u' ? 4 : 2), 16);
						} catch (NumberFormatException e) {
							this.throwParseException("codepoint is ill-formed");
						}
						result += (char) point;
					} else if (c == 'n') {
						result += '\n';
					} else if (c == 't') {
						result += '\t';
					} else {
						result += (char) c;
					}
					escaping = false;
				} else {
					result += (char) c;
				}
			}
			c = this.reader.read();
		}
		
		if (c != '"') {
			this.throwParseException(currLine, currCol, "unclosed string");
		}
		
		return result;
	}
	
	private void throwParseException(String str) throws JsonParseException {
		throw new JsonParseException(
			this.reader.getSource(), this.reader.getLine(), this.reader.getColumn(), str);
	}
	
	private void throwParseException(int line, int column, String str) throws JsonParseException {
		throw new JsonParseException(
			this.reader.getSource(), line, column, str);
	}
}
