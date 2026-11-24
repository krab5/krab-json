package org.krab.json.exception;

public class JsonParseException extends Exception {

	private static final long serialVersionUID = 2010588891786471020L;
	private String file;
	private final int line, column;
	
	public JsonParseException(String file, int line, int column, String message, Throwable cause) {
		super(message, cause);
		this.file = file;
		this.line = line;
		this.column = column;
	}
	
	public JsonParseException(String file, int line, int column, String message) {
		this(file, line, column, message, null);
	}
	
	public JsonParseException(int line, int column, String message, Throwable cause) {
		this(null, line, column, message, cause);
	}
	
	public JsonParseException(int line, int column, String message) {
		this(line, column, message, null);
	}
	
	public String getFile() {
		return this.file;
	}
	
	public int getLine() {
		return this.line;
	}
	
	public int getColumn() {
		return this.column;
	}
	
	@Override
	public String getMessage() {
		StringBuilder builder = new StringBuilder();
		builder.append("at ");
		builder.append(this.file == null ? "<<??>>" : this.file);
		builder.append(":");
		builder.append(this.getLine());
		builder.append(":");
		builder.append(this.getColumn());
		builder.append(": ");
		builder.append(super.getMessage());
		return builder.toString();
	}
}
