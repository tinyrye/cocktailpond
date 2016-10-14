package com.tinook.cocktailpond.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import com.tinook.cocktailpond.model.ContentBridge;
import com.tinook.cocktailpond.model.Recipe;
import com.tinook.common.provider.SQLiteSimpleContentProvider;
import com.tinook.common.text.format.ParameterFormat;
import com.tinook.common.text.format.ParameterizedText;
import com.tinook.common.text.format.SimpleIterableJoin;

public class RecipeSearchContentProvider extends SQLiteSimpleContentProvider
{
	protected static final ParameterizedText RECIPE_QUERY_SQL_FORMAT =
		new ParameterizedText(
			"SELECT ${columns} " +
			"  FROM (SELECT docid, name, summary, ingredients FROM recipe_fts " +
			"         WHERE recipe_fts MATCH '${queryString}') recipe_match " +
		    "  JOIN recipe ON recipe_match.docid = recipe.recipe_fts_id"
		);
    
	static
	{
		RECIPE_QUERY_SQL_FORMAT.getFormat().setValueSerializer(new ParameterFormat.BasicParameterSetter()
		{
			@Override
			public String serializeParam(final String paramName, final Object paramValue) {
				if (paramName.equals("columns") && (paramValue instanceof Iterable)) {
					return new SimpleIterableJoin(", ").toString((Iterable) paramValue);
				}
				else {
					return super.serializeParam(paramName, paramValue);
				}
			}
		});
	}

	/**
	 * Perform Full Text Search on target table.
	 * 
	 * @param selection ASSUMED: full text search string
	 * @param selectionArgs IGNORED
	 * @param sortOrder When relevancy vs random etc is tackled sort order will affect results but not now
	 */
	@Override
	public Cursor query(final Uri target, final String[] columns, final String selection,
						final String[] selectionArgs, final String sortOrder)
	{
		Log.d(getClass().getName(), "Query on URI: " + target.toString());

		if (! super.getTableName(target).equals("recipe")) {
			throw new UnsupportedOperationException("Only recipes can be searched for now.");
		}
		else if (selection == null || selection.trim().isEmpty()) {
			throw new UnsupportedOperationException("Cannot run wildcard queries.");
		}

		final Map<String,Object> queryParams = new HashMap<String,Object>();
		queryParams.put("columns", prefaceSearchColumns(Arrays.asList(columns)));
		queryParams.put("queryString", selection);

		final String actualSql = RECIPE_QUERY_SQL_FORMAT.withParams(queryParams).toString();
		Log.d(getClass().getName(), "Actual recipe search SQL: " + actualSql);
		final Cursor results = db.rawQuery(actualSql, null);

		Log.d(
			getClass().getName(),
			String.format("Found %d results: queryString=%s", results.getCount(), selection));
	    
	    return results;
	}

	@Override
	protected String getIdColumnName(final Uri target) { return "docid"; }

	@Override
	protected String getTableName(final Uri target) {
		String tableName = super.getTableName(target);
		if (! tableName.endsWith("_fts")) tableName = tableName + "_fts";
		return tableName;
	}
	
	protected List<String> prefaceSearchColumns(final List<String> columns)
	{
		Log.d(getClass().getName(), "Search columns requested: " + columns);

		for (int i = 0; i < columns.size(); i++)
		{
			if (columns.get(i).equals("ingredients")) {
				columns.set(i, "replace(recipe_match." + columns.get(i) + ", '|', ', ') AS " + columns.get(i));
			}
			else {
				columns.set(i, "recipe." + columns.get(i) + " AS " + columns.get(i));
			}
		}
		
		return columns;
	}
}