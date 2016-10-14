package com.tinook.cocktailpond.dao;

import android.database.Cursor;

import android.content.AsyncTaskLoader;
import android.content.Context;

import android.util.Log;

import com.tinook.common.util.LogContext;

public class SearchByKeywordsLoader extends AsyncTaskLoader<Cursor>
{
    private String queryString;
    private final RecipeRepository repository;
    private final LogContext logContext = new LogContext(SearchByKeywordsLoader.class);

    public SearchByKeywordsLoader(final Context context, final String queryString) {
        super(context);
		repository = new RecipeRepository(context);
        this.queryString = queryString;
        logContext.putAttribute("queryString", queryString);
    }

    @Override
    public Cursor loadInBackground() {
        logContext.newEvent(Log.DEBUG, "loadInBackground: queryString=[${queryString}]").dispatch();
        return new RecipeRepository(getContext()).searchByKeywords(queryString);
    }

    @Override
    protected void onStartLoading()
    {
        logContext.newEvent(Log.DEBUG, "startLoading: queryString=[${queryString}]").dispatch();
		if (repository.isValidAndCompleteSearch(queryString)) {
			queryString = repository.correctModifiers(queryString);
            logContext.newEvent(Log.DEBUG, "Query string is valid; continue loading").dispatch();
    		forceLoad();
    	}
    	else {
    		cancelLoad();
    	}
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }
}