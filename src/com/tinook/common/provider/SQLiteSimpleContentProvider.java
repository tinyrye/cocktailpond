package com.tinook.common.provider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.tinook.cocktailpond.database.CocktailpondDbPreparer;

public class SQLiteSimpleContentProvider extends ContentProvider
{
	protected SQLiteDatabase db;

	@Override
	public boolean onCreate()
	{
		db = SQLiteDatabase.openDatabase(getContext().getDatabasePath("cocktailpond").getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
		return true;
	}

	@Override
	public String getType(final Uri target) {
		return "text/plain";
	}

	@Override
	public Cursor query(final Uri target, final String[] columns, String selection,
						final String[] selectionArgs, final String sortOrder)
	{
		Log.d(getClass().getName(), "URI of objecting querying " + target.toString());
		return db.query(
					getTableName(target), columns, ensureIdInSelectionIfTargeted(target, selection), selectionArgs,
					/* groupBy */ null, /* having */ null,
					sortOrder);
	}

	@Override
	public Uri insert(final Uri target, final ContentValues insertValues) {
		return target.buildUpon().appendPath(Long.valueOf(db.insert(getTableName(target), null, insertValues)).toString()).build();
	}

	@Override
	public int update(final Uri target, final ContentValues updateValues, final String selection, final String[] selectionArgs) {
		return db.update(getTableName(target), updateValues, ensureIdInSelectionIfTargeted(target, selection), selectionArgs);
	}

	@Override
	public int delete(final Uri target, final String selection, final String[] selectionArgs) {
		return db.delete(getTableName(target), ensureIdInSelectionIfTargeted(target, selection), selectionArgs);
	}

	/**
	 * If the URI/target specifies a specific row by id, then make sure the where clause
	 * has an id-by-number equals condition.
	 * 
	 * @return a where clause where the id clause ensured
	 */
	protected String ensureIdInSelectionIfTargeted(final Uri target, String selection) {
		if (hasId(target)) return appendToSelection(selection, getIdClause(target));
		else return selection;
	}

	protected String appendToSelection(final String selection, final String additionalCriteria) {
		if ((selection != null) && ! selection.trim().isEmpty()) return String.format("%s AND %s", selection, additionalCriteria);
		else return additionalCriteria;
	}

	protected String[] appendToSelectionArgs(final String[] selectionArgs, final Object ... additionalArgs)
	{
		final List<String> combined = new ArrayList<String>();
		if ((selectionArgs != null) && (selectionArgs.length > 0)) combined.addAll(Arrays.asList(selectionArgs));
		for (final Object additionalArg: additionalArgs) combined.add(additionalArg.toString());
		return combined.toArray(new String[0]);
	}
	
	protected List<String> getPathSegmentsBefore(final Uri target, final String terminatingSegment)
	{
		final List<String> pathSegments = target.getPathSegments();
		if (pathSegments.size() > 1)
		{
			for (int i = pathSegments.size() - 1; i >= 1; i--) {
				if (pathSegments.get(i).equals(terminatingSegment)) {
					return pathSegments.subList(0, i);
				}
			}
		}
		return new ArrayList<String>();
    }

	protected String getTableName(final Uri target) {
		final List<String> pathSegments = target.getPathSegments();
		if (pathSegments.size() > 1) {
			if (isNumber(pathSegments.get(pathSegments.size() - 1))) return pathSegments.get(pathSegments.size() - 2);
			else return pathSegments.get(pathSegments.size() - 1);
		}
		else if (pathSegments.size() == 1) return pathSegments.get(0);
		else return null;
	}

	protected boolean hasId(final Uri target) {
		final List<String> pathSegments = target.getPathSegments();
		return ((pathSegments.size() > 1) && isNumber(pathSegments.get(pathSegments.size() - 1)));
	}

	protected String getIdClause(final Uri target) {
		final Integer id = Integer.valueOf(target.getLastPathSegment());
		return String.format("%s = %d", getIdColumnName(target), id);
	}

	protected String getIdColumnName(final Uri target) {
		return BaseColumns._ID;
	}

	/**
	 * Confirms that the URI path goes from parent table name, parent record id, to child table
	 * name.
	 * @return the parent record id from the URI path
	 */
	protected Integer getByParentId(final Uri target, final String parentTableName, final String childTableName)
	{
		final List<String> pathSegmentsBefore = getPathSegmentsBefore(target, childTableName);

		if ((pathSegmentsBefore.size() >= 2)
			 && parentTableName.equals(pathSegmentsBefore.get(pathSegmentsBefore.size() - 2)))
	    {
	    	try { return Integer.valueOf(pathSegmentsBefore.get(pathSegmentsBefore.size() - 1)); }
	    	catch (NumberFormatException ex) { return null; }
	    }
	    else {
	    	return null;
		}
	}

	protected boolean isNumber(final String number) {
		try { Integer.valueOf(number); return true; }
		catch (NumberFormatException ex) { return false; }
	}

	protected List<String> prefaceColumnNames(final String tablePrefix, final List<String> columns)
	{
		for (int i = 0; i < columns.size(); i++) {
			columns.set(i, String.format("%s.%s AS %s", tablePrefix, columns.get(i), columns.get(i)));
		}
		
		return columns;
	}
}