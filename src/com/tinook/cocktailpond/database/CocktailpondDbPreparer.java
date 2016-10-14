package com.tinook.cocktailpond.database;

import java.io.IOException;

import android.content.Context;

import com.tinook.common.database.DbMigrator;
import com.tinook.common.database.DbPreparer;

import com.tinook.cocktailpond.model.Recipe;

public class CocktailpondDbPreparer extends DbPreparer
{
	public CocktailpondDbPreparer(final Context context) {
		super(context, "cocktailpond");
	}

	@Override
	protected DbMigrator createDbMigrator() throws IOException
	{
		final DbMigrator dbMigrator = new CocktailpondDbMigrator();
		dbMigrator.scanMigrations(getContext().getAssets(), dbMigrator.getMigrationsPath(Recipe.class));
		return dbMigrator;
	}
}