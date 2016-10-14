package com.tinook.common.database.model;

import android.database.Cursor;

public interface RowMapper<T>
{
	T extractValues(Cursor cursor);
}