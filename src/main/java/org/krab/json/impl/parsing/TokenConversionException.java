package org.krab.json.impl.parsing;

public class TokenConversionException extends RuntimeException {

	private static final long serialVersionUID = 6809167288199227552L;
	
	private final Token token;
	private final Token.Type type;
	
	public TokenConversionException(Token token, Token.Type type) {
		super(String.format("cannot convert token %s to type %s"
			, token.toString(), type.name()));
		this.token = token;
		this.type = type;
	}
	
	public Token getToken() {
		return this.token;
	}
	
	public Token.Type getType() {
		return this.type;
	}
}
