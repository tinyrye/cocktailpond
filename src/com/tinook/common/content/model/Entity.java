package com.tinook.common.content.model;

public @interface Entity
{
	/* String value(); is the URI path */
	String idField(default = "id");
	Class<?> parent();
	Class<?> rowMapper();
	String[] insertableProperties();
	String[] updatableProperties();
	Class<?> propertySetter();
}