package org.krab.json.impl.parsing;

import java.io.IOException;
import java.io.Reader;

import org.krab.json.*;
import org.krab.json.exception.JsonParseException;
import org.krab.json.io.JsonParser;

public class Parser implements JsonParser {
	private JsonFactory factory;
	private Lexer lexer;
	
	public Parser(JsonFactory factory, String source, Reader reader) {
		this.factory = factory;
		this.lexer = new StreamingLexer(source, reader);
	}
	
	public Parser(JsonFactory factory, Reader reader) {
		this.factory = factory;
		this.lexer = new StreamingLexer(reader);
	}
	
	@Override
	public JsonValue parse() throws IOException, JsonParseException {
		JsonValue result = this.parseValue(false);
		this.checkTokenIs(Token.Type.END);
		
		return result;
	}
	
	private JsonValue parseValue(boolean inArray) throws IOException, JsonParseException {
		Token tkn = this.lexer.nextToken();
		
		return switch (tkn.getTokenType()) {
			case STRING -> this.factory.makeValue(tkn.asString());
			case INTEGER -> this.factory.makeValue(tkn.asInteger());
			case DECIMAL -> this.factory.makeValue(tkn.asDecimal());
			case BOOLEAN -> this.factory.makeValue(tkn.asBoolean());
			case OPEN_CURLY -> this.parseObject();
			case OPEN_BRACKET -> this.parseArray();
			case CLOSE_BRACKET -> {
				if (inArray)
					yield this.factory.makeNull();
				else
					throw new JsonParseException(tkn.getTokenLine(), tkn.getTokenColumn(),
						"unexpected token " + tkn.getTokenType().name() + "; was expecting a value at this point"
					);
			}
			default -> throw new JsonParseException(tkn.getTokenLine(), tkn.getTokenColumn(),
					"unexpected token " + tkn.getTokenType().name() + "; was expecting a value at this point"
				);
		};
	}
	
	private JsonValue parseObject() throws IOException, JsonParseException {
		JsonObject obj = this.factory.createObject();
		
		boolean cont = true;
		Token tkn;
		
		do {
			tkn = this.lexer.nextToken();
			
			if (tkn.getTokenType() == Token.Type.CLOSE_CURLY) {
				cont = false;
			} else {
				if (tkn.getTokenType() != Token.Type.STRING)
					this.throwUnexpected(tkn, Token.Type.STRING);
				
				String key = tkn.asString();
				this.checkTokenIs(Token.Type.COLON);
				JsonValue value = this.parseValue(false);
				obj.add(key, value);
				
				tkn = this.lexer.nextToken();
				
				if (tkn.getTokenType() == Token.Type.CLOSE_CURLY) {
					cont = false;
				} else {
					if (tkn.getTokenType() != Token.Type.COMMA)
						this.throwUnexpected(tkn, Token.Type.COMMA);
				}
			}
		} while (cont);
		
		return obj;
	}
	
	private JsonArray parseArray() throws IOException, JsonParseException {
		JsonArray array = this.factory.createArray();
		
		boolean cont = true;
		Token tkn;
		
		do {
			JsonValue value = this.parseValue(true);

			if (value instanceof JsonNull) { // has reached end of array
				cont = false;
			} else {
				array.add(value);
				tkn = this.lexer.nextToken();
				
				if (tkn.getTokenType() == Token.Type.CLOSE_BRACKET) {
					cont = false;
				} else {
					if (tkn.getTokenType() != Token.Type.COMMA)
						this.throwUnexpected(tkn, Token.Type.COMMA);
				}
			}
		} while (cont);
		
		return array;
	}
	
	private Token checkTokenIs(Token.Type tokenType) throws IOException, JsonParseException {
		Token result = this.lexer.nextToken();
		if (result.getTokenType() != tokenType)
			this.throwUnexpected(result, tokenType);
		return result;
	}
	
	private void throwUnexpected(Token tkn, Token.Type expected) throws JsonParseException {
		throw new JsonParseException(tkn.getTokenLine(), tkn.getTokenColumn(), String.format(
				"token of type %s while token of type %s was expected"
				, tkn.getTokenType().name(), expected.name()));
	}
}
