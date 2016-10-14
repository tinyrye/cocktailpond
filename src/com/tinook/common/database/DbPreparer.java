package com.tinook.common.database;

import java.io.File;
import java.io.IOException;

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;

public abstract class DbPreparer
{
	private final Context context;
	private final String dbName;

	public DbPreparer(final Context context, final String dbName) {
		this.context = context;
		this.dbName = dbName;
	}

	protected Context getContext() { return context; }
	protected String getDbName() { return dbName; }

	/**
	 *
	 */
	public DbPreparer reset()
	{
		final File dbFile = context.getDatabasePath(getDbName());
		
		if (dbFile.exists())
		{
			try
			{
				SQLiteDatabase.deleteDatabase(dbFile);
				dbFile.createNewFile();
			}
			catch (IOException ex) { throw new RuntimeException(ex); }
		}

		return this;
	}

	/**
	 *
	 */
	public void makeReady()
	{
		final File dbFile = context.getDatabasePath(getDbName());
		try { if (! dbFile.exists()) { dbFile.mkdirs(); dbFile.createNewFile(); } }
		catch (IOException ex) { throw new RuntimeException(ex); }

		try
		{
			new MigratingDbOpenHelper(context, getDbName(), createDbMigrator()).getReadableDatabase();
		}
		catch (IOException ex) {
			throw new RuntimeException("Failed to migrate database.", ex);
		}
	}

	protected abstract DbMigrator createDbMigrator() throws IOException;
}