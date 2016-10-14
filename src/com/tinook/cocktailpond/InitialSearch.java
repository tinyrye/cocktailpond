package com.tinook.cocktailpond;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.Editable;
import android.util.Log;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;

import com.tinook.cocktailpond.database.CocktailpondDbPreparer;
import com.tinook.cocktailpond.dao.SearchByKeywordsLoader;
import com.tinook.cocktailpond.model.ContentBridge;
import com.tinook.cocktailpond.model.Recipe;

import com.tinook.common.text.AbstractTextWatcher;
import com.tinook.common.util.LogContext;

public class InitialSearch extends Activity
{
    private static final LogContext LOG = new LogContext(InitialSearch.class);

    private CursorAdapter searchResultsAdapter;
    private SearchResultsLoaderCallback searchResultsCallback;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        new CocktailpondDbPreparer(this).makeReady();

        setContentView(R.layout.main);
        getQueryStringEditView().addTextChangedListener(new RecipeQueryTextChangeHandler());
        getAddRecipeButton().setOnClickListener(new AddRecipeButtonHandler());
        initializeSearchResults();
    }

    protected EditText getQueryStringEditView() {
        return ((EditText) findViewById(R.id.search_recipes_text_view));
    }

    protected Button getAddRecipeButton() {
        return ((Button) findViewById(R.id.search_results_add_recipe));
    }

    protected ListView getSearchResultsListView() {
        return (ListView) findViewById(R.id.search_results);
    }

    protected void initializeSearchResults()
    {
        searchResultsAdapter = new SearchResultsCursorAdapter(this);
        searchResultsCallback = new SearchResultsLoaderCallback(this, searchResultsAdapter);
        getSearchResultsListView().setAdapter(searchResultsAdapter);
        getLoaderManager().initLoader(0, null, searchResultsCallback);
    }

    private static class SearchResultsCursorAdapter extends SimpleCursorAdapter
    {
        private final Map<View,Integer> recipeByView = new HashMap<View,Integer>();

        public SearchResultsCursorAdapter(final Context context)
        {
            super(
                context, R.layout.search_result_item, null, 
                new String[] { "name", "author", "summary", "ingredients" },
                new int[] { R.id.name, R.id.author, R.id.summary, R.id.ingredients },
                0);
        }

        @Override
        public View getView(final int position, View listItemView, final ViewGroup parent)
        {
            listItemView = super.getView(position, listItemView, parent);
            listItemView.setOnClickListener(new SearchResultRowClickHandler(recipeByView));
            return listItemView;
        }

        @Override
        public void bindView(final View view, final Context context, final Cursor cursor)
        {
            super.bindView(view, context, cursor);
            recipeByView.put(view, Integer.valueOf(cursor.getString(cursor.getColumnIndex(BaseColumns._ID))));
        }
    }

    private static class SearchResultsLoaderCallback implements LoaderManager.LoaderCallbacks<Cursor>
    {
        private final LogContext logContext = new LogContext(SearchResultsLoaderCallback.class);
        private final Context context;
        private final CursorAdapter resultsAdapter;
        private String queryString;

        public SearchResultsLoaderCallback(final Context context, final CursorAdapter resultsAdapter) {
            this.context = context;
            this.resultsAdapter = resultsAdapter;
            logContext.putAttribute("queryString", "N/A");
        }

        public String getQueryString() {
            return queryString;
        }

        public SearchResultsLoaderCallback setQueryString(final String queryString) {
            this.queryString = queryString;
            logContext.putAttribute("queryString", queryString);
            return this;
        }

        @Override
        public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle args) {
            logContext.newEvent(Log.DEBUG, "Creating search loader on [${queryString}]").dispatch();
            return new SearchByKeywordsLoader(context, queryString);
        }

        @Override
        public void onLoadFinished(final Loader<Cursor> loader, final Cursor data) {
            logContext.newEvent(Log.DEBUG, "Search loader has completed data load on [${queryString}]").dispatch();
            resultsAdapter.changeCursor(data);
        }

        @Override
        public void onLoaderReset(final Loader<Cursor> loader) {
            logContext.newEvent(Log.DEBUG, "Search loader was reset on [${queryString}]").dispatch();
            resultsAdapter.changeCursor(null);
        }
    }

    private static class SearchResultRowClickHandler implements View.OnClickListener
    {
        private final Map<View,Integer> recipeByView;

        public SearchResultRowClickHandler(final Map<View,Integer> recipeByView) {
            this.recipeByView = recipeByView;
        }

        @Override
        public void onClick(final View view)
        {
            final Integer recipeId = recipeByView.get(view);

            new LogContext(SearchResultRowClickHandler.class)
                .newEvent(Log.DEBUG, "User clicked on search results row: recipeId=${recipeId}")
                .param("recipeId", recipeId)
                .dispatch();
            
            view.getContext().startActivity(
                new Intent(
                    Intent.ACTION_VIEW,
                    ContentBridge.uriFor(Recipe.class).buildUpon().appendPath(recipeId.toString()).build(),
                    view.getContext(), RecipeDetailsViewing.class
                ));
        }
    }
    
    private class AddRecipeButtonHandler implements View.OnClickListener
    {
        @Override
        public void onClick(final View v) {
            InitialSearch.this.startActivity(new Intent(InitialSearch.this, RecipeEdit.class));
        }
    }

    private class RecipeQueryTextChangeHandler extends AbstractTextWatcher
    {
        @Override
        public void afterTextChanged(final Editable s)
        {
            new LogContext(RecipeQueryTextChangeHandler.class)
                .newEvent(Log.DEBUG, "Query String changed [${queryString}]")
                .param("queryString", s.toString())
                .dispatch();

            searchResultsCallback.setQueryString(s.toString());
            getLoaderManager().restartLoader(0, null, searchResultsCallback);
        }
    }
}