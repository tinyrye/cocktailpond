package com.tinook.cocktailpond.provider;

import android.database.Cursor;
import android.content.ContentValues;
import android.net.Uri;

import com.tinook.common.provider.SQLiteSimpleContentProvider;

public class CocktailpondDaoContentProvider extends SQLiteSimpleContentProvider
{
	@Override
	public Uri insert(final Uri target, final ContentValues values)
	{
		final String tableName = getTableName(target);

		if (tableName.equals("ingredient"))
		{
			// URI is saying to list all ingredients of a recipe by its ids
			final Integer parentRecipeId = getByParentId(target, "recipe", tableName);

			if (parentRecipeId != null) {
				values.put("recipe_id", parentRecipeId);
			}
		}

		return super.insert(target, values);
	}

	@Override
	public int update(final Uri target, final ContentValues values, String selection,
						 String[] selectionArgs)
	{
		final String tableName = getTableName(target);

		if (tableName.equals("ingredient"))
		{
			// URI is saying to list all ingredients of a recipe by its ids
			final Integer parentRecipeId = getByParentId(target, "recipe", tableName);

			if (parentRecipeId != null) {
				selection = appendToSelection(selection, String.format("recipe_id = ?", parentRecipeId));
				selectionArgs = appendToSelectionArgs(selectionArgs, parentRecipeId);
			}
		}

		return super.update(target, values, selection, selectionArgs);
	}

	@Override
	public int delete(final Uri target, String selection, String[] selectionArgs)
	{
		final String tableName = getTableName(target);

		if (tableName.equals("ingredient"))
		{
			// URI is saying to list all ingredients of a recipe by its ids
			final Integer parentRecipeId = getByParentId(target, "recipe", tableName);

			if (parentRecipeId != null) {
				selection = appendToSelection(selection, String.format("recipe_id = ?", parentRecipeId));
				selectionArgs = appendToSelectionArgs(selectionArgs, parentRecipeId);
			}
		}

		return super.delete(target, selection, selectionArgs);
	}

	@Override
	public Cursor query(final Uri target, final String[] projection, String selection,
						String[] selectionArgs, final String sortBy)
	{
		final String tableName = getTableName(target);

		if (tableName.equals("ingredient"))
		{
			// if getting ingredient by id, then no other selection criteria is expected.
			if (! hasId(target))
			{
				// URI is saying to list all ingredients of a recipe by its ids
				final Integer parentRecipeId = getByParentId(target, "recipe", tableName);

				if (parentRecipeId != null) {
					selection = appendToSelection(selection, String.format("recipe_id = ?", parentRecipeId));
					selectionArgs = appendToSelectionArgs(selectionArgs, parentRecipeId);
				}
			}
		}

		return super.query(target, projection, selection, selectionArgs, sortBy);
	}
}