package com.tinook.common.database.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.BeanMap;

public class BeanIntrospectingRowMapper<T> implements RowMapper<T>
{
	private final Class<T> beanClazz;
	
	public BeanIntrospectingRowMapper(final Class<T> beanClazz) {
		this.beanClazz = beanClazz;
	}

	@Override
	public T extractValues(final Cursor cursor)
	{
		final BeanMap objectMapper = new BeanMap(newBean());
		for (final String beanProp: objectMapper.keySet()) putProperty(objectMapper, cursor, beanProp);
		return (T) objectMapper.getBean();
	}
	
	protected T newBean() {
		try { return beanClazz.newInstance(); }
		catch (IllegalAccessException ex) { throw new RuntimeException(ex); }
		catch (InstantiationException ex) { throw new RuntimeException(ex); }
	}

	protected void putProperty(final BeanMap objectMapper, final Cursor dbRow, final String beanProp)
	{
		final int columnIndex = dbRow.getColumnIndex(beanProp);
		final int columnType = dbRow.getType();

		if (columnType == Cursor.FIELD_TYPE_STRING) objectMapper.put(beanProp, dbRow.getString(columnIndex));
		else if (columnType == Cursor.FIELD_TYPE_INTEGER) objectMapper.put(beanProp, (Long) dbRow.getLong(columnIndex));
		else if (columnType == Cursor.FIELD_TYPE_FLOAT) objectMapper.put(beanProp, (Double) dbRow.getDouble(columnIndex));
		else if (columnType == Cursor.FIELD_TYPE_BLOB) objectMapper.put(beanProp, (Byte[]) dbRow.getBlob(columnIndex));
		else if (columnType == Cursor.FIELD_TYPE_NULL) { /* noop */ }
		else objectMapper.put(beanProp, dbRow.getString(columnIndex));
	}
}