package com.tinook.cocktailpond;

import android.app.Activity;
import android.app.LoaderManager;
import android.database.Cursor;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tinook.cocktailpond.dao.RecipeIngredientsLoader;
import com.tinook.cocktailpond.dao.RecipeRepository;

import com.tinook.cocktailpond.model.ContentBridge;
import com.tinook.cocktailpond.model.Recipe;

public class RecipeDetailsViewing extends Activity
{
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_details);

        Log.d(getClass().getName(), "Recipe to query: " + getIntent().getData().toString());

        final Cursor recipeRow = new RecipeRepository(this).getRecipe(getIntent().getData());

        try
        {
            if (recipeRow.moveToNext())
            {
                ((TextView) findViewById(R.id.name)).setText(getString(recipeRow, "name"));
                ((TextView) findViewById(R.id.summary)).setText(getString(recipeRow, "summary"));
                ((TextView) findViewById(R.id.author)).setText(getString(recipeRow, "author"));
                initializeIngredientsListView();
                ((Button) findViewById(R.id.deleteRecipe)).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v) {
                        new RecipeRepository(RecipeDetailsViewing.this)
                            .deleteRecipe(Integer.valueOf(getIntent().getData().getLastPathSegment()));
                        RecipeDetailsViewing.this.finish();
                    }
                });
                ((Button) findViewById(R.id.editRecipe)).setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(final View v)
                    {
                        RecipeDetailsViewing.this.startActivity(
                            new Intent(
                                Intent.ACTION_EDIT,
                                RecipeDetailsViewing.this.getIntent().getData(),
                                RecipeDetailsViewing.this,
                                RecipeEdit.class));
                    }
                });
            }
            else
            {
                Log.e(getClass().getName(), "Recipe not found by id: " + getIntent().getData().toString());
                finish();
            }
        }
        finally {
            recipeRow.close();
        }
	}

    protected void initializeIngredientsListView()
    {
        getIngredientsListView().setAdapter(
            new SimpleCursorAdapter(
                this, R.layout.recipe_details_ingredients, null,
                new String[] { "name", "amount", "amountUnit" },
                new int[] { R.id.name, R.id.amount, R.id.amountUnit },
                0
            ));

        getLoaderManager().initLoader(0, null, new IngredientLoaderCallback());
    }

    protected ListView getIngredientsListView() {
        return (ListView) findViewById(R.id.ingredients);
    }

    protected String getString(final Cursor row, final String columnName) {
        return row.getString(row.getColumnIndex(columnName));
    }

    protected class IngredientLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor>
    {
        @Override
        public Loader<Cursor> onCreateLoader(final int id, final Bundle args) {
            return new RecipeIngredientsLoader(RecipeDetailsViewing.this, getIntent().getData());
        }

        @Override
        public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
            ((CursorAdapter) getIngredientsListView().getAdapter()).changeCursor(data);
        }

        @Override
        public void onLoaderReset(final Loader<Cursor> loader) {
            ((CursorAdapter) getIngredientsListView().getAdapter()).changeCursor(null);
        }
    }
}