package org.krab.json.io;

import java.io.IOException;

import org.krab.json.JsonValue;
import org.krab.json.exception.JsonParseException;

public interface JsonParser {
	JsonValue parse() throws IOException, JsonParseException;
}
