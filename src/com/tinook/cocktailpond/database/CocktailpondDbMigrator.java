package com.tinook.cocktailpond.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tinook.common.database.DbMigrator;

public class CocktailpondDbMigrator extends DbMigrator
{
	@Override
	protected void runCustomMigration(final SQLiteDatabase db, final Migration m)
	{
		if (m.script.trim().equals("addAllRecipesToFullTextSearch")) {
			addAllRecipesToFullTextSearch(db);
		}
		else {
			onUnknownCustomMigration(m);
		}
	}

	protected void addAllRecipesToFullTextSearch(final SQLiteDatabase db)
	{
		final Cursor recipe =
			db.rawQuery("SELECT recipe._id, recipe.name, recipe.summary, group_concat(ingredient.name, ' | ') as ingredients " +
			            "  FROM recipe LEFT OUTER JOIN ingredient ON recipe._id = ingredient.recipe_id " +
				        " WHERE recipe_fts_id IS NULL GROUP BY recipe.name, recipe.summary", null);

		while (recipe.moveToNext())
		{
			final ContentValues ftsInsertValues = new ContentValues();
			ftsInsertValues.put("name", recipe.getString(recipe.getColumnIndex("name")));
			ftsInsertValues.put("summary", recipe.getString(recipe.getColumnIndex("summary")));
			ftsInsertValues.put("ingredients", recipe.getString(recipe.getColumnIndex("ingredients")));

			final Integer ftsId = (int) db.insert("recipe_fts", null, ftsInsertValues);

			final ContentValues recipeUpdateValues = new ContentValues();
			recipeUpdateValues.put("recipe_fts_id", ftsId);

			db.update("recipe", recipeUpdateValues, "_id = ?", new String[] { recipe.getString(recipe.getColumnIndex("_id")) });
		}
	}
}