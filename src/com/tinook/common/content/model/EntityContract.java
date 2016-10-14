package com.tinook.common.content.model;

import java.util.List;

import android.net.Uri;

public interface EntityContract<T>
{
	Class<T> getEntityClass();
	Uri getUriFor();
	Uri getUriFor(Integer entityId);
	
	/**
	 * These are the properties as projection values.
	 */
	List<String> getProperties();

	/**
	 * These are the properties as projection values.
	 */
	List<String> getProjection();
	
	/**
	 * It has the same purpose as the Spring JDBC object.
	 */
	RowMapper<T> getCursorMapper();
	
	/**
	 * Superset of properties that can be inserted.
	 */
	List<String> getInsertableProperties();

	/**
	 * Superset of properties that can be updated.
	 */
	List<String> getUpdatableProperties();

	/**
	 * When inserting or updating the entity properties
	 */
	ContentValuesSetter<T> getPropertySetter();
}