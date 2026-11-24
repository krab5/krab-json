package org.krab.json.io;

import java.io.IOException;
import java.io.Writer;

import org.krab.json.*;

public class JsonExporter {
	public static enum Style {
		COMPACT, SPACED, PRETTY
	}
	
	private Writer writer;
	private JsonValue value;
	
	private Style style = Style.COMPACT;
	private CharSequence newline = "\n";
	
	private int indent = 0;
	
	public JsonExporter(Writer writer, JsonValue value) {
		this.writer = writer;
		this.value = value;
	}
	
	public JsonExporter setStyle(Style style) {
		this.style = style;
		return this;
	}
	
	public JsonExporter setNewline(CharSequence nl) {
		this.newline = nl;
		return this;
	}
	
	public void export() throws IOException {
		try {
			this.exportValue(this.value);
			this.newline();
			this.writer.flush();
		} catch (IOException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException("unexpected exception " + e.getClass(), e);
		}
	}
	
	private void exportValue(JsonString str) throws IOException {
		this.outputString(str.getValue());
	}
	
	private void exportValue(JsonInteger value) throws IOException {
		this.writer.append(Integer.toString(value.getValue()));
	}
	
	private void exportValue(JsonDecimal value) throws IOException {
		this.writer.append(Double.toString(value.getValue()));
	}
	
	private void exportValue(JsonBoolean value) throws IOException {
		this.writer.append(Boolean.toString(value.getValue()));
	}
	
	private void exportValue(JsonArray value) throws IOException {
		this.writer.append('[');
		this.indent++;
		this.newline();
		this.indent();
		
		for (int i = 0; i < value.getSize(); i++) {
			this.exportValue(value.get(i));
			
			if (i < value.getSize() - 1) {
				this.writer.append(',');
				this.space();
				this.newline();
				this.indent();
			}
		}
		
		this.indent--;
		this.newline();
		this.indent();
		this.writer.append(']');
	}
	
	private void exportValue(JsonObject value) throws IOException {
		this.writer.append('{');
		this.space();
		this.indent++;
		this.newline();
		this.indent();
		
		var it = value.keyList().iterator();
		
		while (it.hasNext()) {
			String key = it.next();
			this.outputString(key);
			this.space();
			this.writer.append(':');
			this.space();
			this.exportValue(value.get(key));
			
			if (it.hasNext()) {
				this.writer.append(',');
				this.space();
				this.newline();
				this.indent();
			}
		}

		this.indent--;
		this.space();
		this.newline();
		this.indent();
		this.writer.append('}');
	}
	
	private void exportValue(JsonValue value) throws IOException {
		switch (value) {
			case JsonString  str  -> this.exportValue(str);
			case JsonInteger inte -> this.exportValue(inte);
			case JsonBoolean bool -> this.exportValue(bool);
			case JsonDecimal dec  -> this.exportValue(dec);
			case JsonArray   arr  -> this.exportValue(arr);
			case JsonObject  obj  -> this.exportValue(obj);
			default ->
				throw new RuntimeException("unsupported JSON value type " + value.getClass());
		}
	}
	
	private void newline() throws IOException {
		if (this.style == Style.PRETTY)
			this.writer.append(this.newline);
	}
	
	private void space() throws IOException {
		if (this.style != Style.COMPACT)
			this.writer.append(' ');
	}
	
	private void indent() throws IOException {
		if (this.style == Style.PRETTY) {
			for (int i = 0; i < this.indent; i++)
				this.writer.append("  ");
		}
	}

	private void outputString(String value) throws IOException {
		this.writer.append('"');
		for (char c : value.toCharArray()) {
			switch (c) {
				case '\n' -> this.writer.append("\\n");
				case '\r' -> this.writer.append("\\r");
				case '\\' -> this.writer.append("\\\\");
				case '"' -> this.writer.append("\\\"");
				default -> this.writer.append(c);
			}
		}
		this.writer.append('"');
	}
	
	
}
