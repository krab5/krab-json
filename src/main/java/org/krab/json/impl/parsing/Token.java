package org.krab.json.impl.parsing;

public class Token {
	public static enum Type {
		OPEN_CURLY, CLOSE_CURLY,
		OPEN_BRACKET, CLOSE_BRACKET,
		COLON, COMMA,
		STRING, BOOLEAN, INTEGER, DECIMAL,
		END
	}
	
	private final Type tokenType;
	private final int tokenLine;
	private final int tokenColumn;
	
	// Variant part
	private final String strContent;
	private final Boolean boolContent;
	private final Integer intContent;
	private final Double decContent;
	
	public Token(Type tt, int l, int c,
			String strct, Boolean boolct, Integer intct, Double decct) {
		this.tokenType = tt;
		this.tokenLine = l;
		this.tokenColumn = c;
		this.strContent = strct;
		this.boolContent = boolct;
		this.intContent = intct;
		this.decContent = decct;
	}
	
	public Token(Type tt, int l, int c) {
		this(tt, l, c, null, null, null, null);
	}
	
	public Token(int l, int c, String value) {
		this(Type.STRING, l, c, value, null, null, null);
	}
	
	public Token(int l, int c, boolean value) {
		this(Type.BOOLEAN, l, c, null, value, null, null);
	}
	
	public Token(int l, int c, int value) {
		this(Type.INTEGER, l, c, null, null, value, null);
	}
	
	public Token(int l, int c, double value) {
		this(Type.DECIMAL, l, c, null, null, null, value);
	}
	
	public Type getTokenType() {
		return this.tokenType;
	}
	
	public int getTokenLine() {
		return this.tokenLine;
	}
	
	public int getTokenColumn() {
		return this.tokenColumn;
	}
	
	public String asString() {
		if (this.strContent == null)
			throw new TokenConversionException(this, Type.STRING);
		return this.strContent;
	}
	
	public boolean asBoolean() {
		if (this.boolContent == null)
			throw new TokenConversionException(this, Type.BOOLEAN);
		return this.boolContent;
	}
	
	public int asInteger() {
		if (this.intContent == null)
			throw new TokenConversionException(this, Type.INTEGER);
		return this.intContent;
	}
	
	public double asDecimal() {
		if (this.decContent == null)
			throw new TokenConversionException(this, Type.DECIMAL);
		return this.decContent;
	}
	
	public boolean isEnd() {
		return this.tokenType == Type.END;
	}
	
	@Override
	public String toString() {
		String result = "<<" + this.tokenType.name()
			+ ",(" + this.tokenLine + "," + this.tokenColumn + ")";
		
		if (this.strContent != null)
			result += "[\"" + this.strContent + "\"]";
		if (this.boolContent != null)
			result += "[" + this.boolContent + "]";
		if (this.intContent != null)
			result += "[" + this.intContent + "]";
		if (this.decContent != null)
			result += "[" + this.decContent + "]";
		
		result += ">>";
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this == obj || (obj != null && (obj instanceof Token tkn) &&
			this.equals(tkn));
	}
	
	public boolean equals(Token tkn) {
		boolean result = (this.tokenType == tkn.tokenType);
		
		if (this.strContent != null)
			result = result && this.strContent.equals(tkn.strContent);
		if (this.boolContent != null)
			result = result && this.boolContent.equals(tkn.boolContent);
		if (this.intContent != null)
			result = result && this.intContent.equals(tkn.intContent);
		if (this.decContent != null)
			result = result && this.decContent.equals(tkn.decContent);
		
		return result;
	}
}
