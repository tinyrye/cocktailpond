package com.tinook.cocktailpond.dao;

import android.database.Cursor;

import android.content.AsyncTaskLoader;
import android.content.Context;

import android.net.Uri;

public class RecipeIngredientsLoader extends AsyncTaskLoader<Cursor>
{
	private final RecipeRepository repository;
	private final Uri recipeUri;

	public RecipeIngredientsLoader(final Context context, final Uri recipeUri) {
		super(context);
		repository = new RecipeRepository(context);
		this.recipeUri = recipeUri;
	}

	@Override
	public Cursor loadInBackground() {
		return repository.getIngredients(recipeUri);
	}

	@Override
	protected void onStartLoading() {
		forceLoad();
	}
}