package org.krab.json.util;

public interface JsonVisitor<P> {
	void visitValue(P param, String value) throws Throwable;
	void visitValue(P param, int value) throws Throwable;
	void visitValue(P param, double value) throws Throwable;
	void visitValue(P param, boolean value) throws Throwable;
	
	void visitKey(P param, String key) throws Throwable;
	
	void visitObject(P param) throws Throwable;
	void visitEndObject(P param) throws Throwable;
	
	void visitArray(P param) throws Throwable;
	void visitEndArray(P param) throws Throwable;
	
	void visitNull(P param) throws Throwable;
}
