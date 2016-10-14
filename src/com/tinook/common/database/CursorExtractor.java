package com.tinook.common.database;

import java.util.HashMap;
import java.util.Map;

import android.database.Cursor;

public class CursorExtractor
{
	/**
	 * View the current row as a map of column names to column values.
	 * 
	 * @return a map by column name to column values where all values are the String
	 * representation as decided by {@link Cursor#getString}.
	 */
    public Map<String,String> toRowMap(final Cursor row)
    {
    	final Map<String,String> columnValues = new HashMap<String,String>();
    	for (final String column: row.getColumnNames()) columnValues.put(column, row.getString(row.getColumnIndex(column)));
    	return columnValues;
    }
}