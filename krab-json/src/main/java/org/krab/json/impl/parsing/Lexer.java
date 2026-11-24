package org.krab.json.impl.parsing;

import java.io.IOException;

import org.krab.json.exception.JsonParseException;

public interface Lexer {
	String getSource();
	Token nextToken() throws IOException, JsonParseException;
}
