package org.krab.json.impl.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TokenList {
	private List<Token> content = new ArrayList<>();
	
	public TokenList(List<Token> content) {
		this.content = content;
	}
	
	public TokenList(Token... tokens) {
		this(List.of(tokens));
	}
	
	public int getSize() {
		return this.content.size();
	}
	
	public Token get(int i) {
		return this.content.get(i);
	}
	
	@Override
	public boolean equals(Object other) {
		return this == other || (other != null && (other instanceof TokenList)
			&& this.equals((TokenList) other));
	}
	
	public boolean equals(TokenList other) {
		if (this.content.size() == other.content.size()) {
			boolean result = true;
			for (int i = 0; i < this.content.size(); i++)
				result = result && this.content.get(i).equals(other.content.get(i));
			return result;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[" +
			this.content.stream().map(Token::toString).collect(Collectors.joining(","))
			+ "]";
	}
}
