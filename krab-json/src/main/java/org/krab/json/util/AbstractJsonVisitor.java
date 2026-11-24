package org.krab.json.util;

public abstract class AbstractJsonVisitor<P> implements JsonVisitor<P> {

	@Override
	public void visitValue(P param, String value) {}

	@Override
	public void visitValue(P param, int value) {}

	@Override
	public void visitValue(P param, double value) {}

	@Override
	public void visitValue(P param, boolean value) {}

	@Override
	public void visitKey(P param, String key) {}

	@Override
	public void visitObject(P param) {}
	
	@Override
	public void visitEndObject(P param) {}

	@Override
	public void visitArray(P param) {}
	
	@Override
	public void visitEndArray(P param) {}

	@Override
	public void visitNull(P param) {}

}
