package com.tinook.cocktailpond.app;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;

import android.database.sqlite.SQLiteDatabase;

import android.util.Log;

import com.tinook.common.database.CursorExtractor;
import com.tinook.common.database.DbMigrator;
import com.tinook.common.database.DbPreparer;
import com.tinook.common.database.MigratingDbOpenHelper;

import com.tinook.cocktailpond.dao.RecipeRepository;

import com.tinook.cocktailpond.model.ContentBridge;
import com.tinook.cocktailpond.model.Recipe;

public class StartupUtils
{
	/**
	 *
	 */
	public static void validateRecipeData(final Context context)
	{
		final SQLiteDatabase db = SQLiteDatabase.openDatabase(context.getDatabasePath("cocktailpond").getAbsolutePath(), null, SQLiteDatabase.OPEN_READWRITE);
		final CursorExtractor rowMapper = new CursorExtractor();

		final Cursor ftsRows = db.rawQuery("SELECT * FROM recipe_fts", null);

		while (ftsRows.moveToNext()) {
			Log.d(StartupUtils.class.getName(), String.format("Recipe FTS row: %s", rowMapper.toRowMap(ftsRows)));
		}

		final Cursor rows = db.rawQuery("SELECT * FROM recipe", null);

		while (rows.moveToNext()) {
			Log.d(StartupUtils.class.getName(), String.format("Recipe row: %s", rowMapper.toRowMap(rows)));
		}
	}

	/**
	 *
	 */
	public static void loadTestRecipesIfEmpty(final Context context, final int arrayResourceId)
	{
		final Cursor existingRecipes =
								context.getContentResolver()
									.query(
										ContentBridge.uriFor(Recipe.class),
										new ContentBridge.ProjectionResolver(Recipe.class).toProjectionArray(),
										null, null,
										null);
        
        if (existingRecipes.isAfterLast())
        {
            for (final String testRecipe: context.getResources().getStringArray(arrayResourceId)) {
	            loadTestRecipe(context, testRecipe);
	        }
        }
	}

	/**
	 *
	 */
    public static void loadTestRecipe(final Context context, final String recipeResString)
    {
    	final Recipe recipe = new Recipe();
		final Map<String,String> properties = readPropertyAssignments(recipeResString);
		recipe.setName(properties.get("name"));
		recipe.setSummary(properties.get("summary"));
		recipe.setAuthor(properties.get("author"));

        new RecipeRepository(context).addRecipe(recipe);
    }

	/**
	 *
	 */
    protected static Map<String,String> readPropertyAssignments(final String assignmentsString)
    {
		final Map<String,String> properties = new HashMap<String,String>();

        for (final String columnAssignment: assignmentsString.split("; ")) {
            final String[] assignmentTokens = columnAssignment.split("=");
            properties.put(assignmentTokens[0].trim(), assignmentTokens[1].trim());
        }

        return properties;
    }
}