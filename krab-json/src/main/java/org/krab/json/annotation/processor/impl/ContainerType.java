package org.krab.json.annotation.processor.impl;

public sealed abstract class ContainerType implements FieldType permits ArrayType, CollectionType, OptionalType {
	private final FieldType innerType;
	
	public ContainerType(FieldType innerType) {
		this.innerType = innerType;
	}
	
	public FieldType getInnerType() {
		return this.innerType;
	}
}
