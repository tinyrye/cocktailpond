package com.tinook.cocktailpond.dao;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.database.Cursor;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.tinook.cocktailpond.model.ContentBridge;
import com.tinook.cocktailpond.model.Recipe;
import com.tinook.cocktailpond.model.RecipeIngredient;
import com.tinook.cocktailpond.model.Volume;

import com.tinook.common.text.InlineReplace;
import com.tinook.common.text.FrequencyMeasure;
import com.tinook.common.util.Function;
import com.tinook.common.util.LogContext;
import com.tinook.common.util.LogEvent;
import com.tinook.common.util.Predicate;
import com.tinook.common.util.OrPredicate;

public class RecipeRepository
{
    private static final LogContext LOG = new LogContext(RecipeRepository.class);

    private Context context;
    private EntityBridge<Recipe> recipeContentIdentifier;
    private EntityBridge<Recipe> recipeSearchContentIdentifier;

    public RecipeRepository(final Context context) {
        this.context = context;
        /* recipeContentIdentifier = ...; */
        /* recipeSearchContentIdentifier = ...; */
    }

    protected ContentResolver getContentResolver() {
        return context.getContentResolver();
    }

    protected Cursor queryEntityById(final EntityBridge<?> contentIdentifier, final Integer entityId)
    {
        return getContentResolver().query(
                    contentIdentifier.uriFor(recipeId),
                    contentIdentifier.projectionFor(),
                    null, null, null);
    }

    protected Uri getRecipeUri(final Integer recipeId) {
        return recipeContentIdentifier.uriFor(recipeId);
    }

    public Cursor searchByKeywords(final String queryString)
    {
        return getContentResolver().query(
                    recipeSearchContentIdentifier.uriFor(),
                    recipeSearchContentIdentifier.projectionFor(),
                    queryString, null, null);
    }

    public Cursor getRecipe(final Integer recipeId) {
        return queryEntityById(recipeContentIdentifier, recipeId);
    }

    public Cursor getRecipe(final Uri recipeUri) {
        return getContentResolver().query(recipeUri, recipeContentIdentifier.projectionFor(), null, null, null);
    }

    public Recipe getRecipeObject(final Integer recipeId) {
        return getRecipeObject(getRecipeUri(recipeId));
    }

    public Recipe getRecipeObject(final Uri recipeUri) {
        return toRecipe(getRecipe(recipeUri));
    }

    public Recipe toRecipe(final Cursor cursor)
    {
        Recipe recipe = null;

        if (cursor.moveToNext())
        {
            recipe = new Recipe();
            recipe.setName(cursor.getString(cursor.getColumnIndex("name")));
            recipe.setSummary(cursor.getString(cursor.getColumnIndex("summary")));
            recipe.setAuthor(cursor.getString(cursor.getColumnIndex("author")));
            recipe.setCreatedAt(cursor.getLong(cursor.getColumnIndex("createdAt")));
        }

        return recipe;
    }

    public Cursor getIngredients(final Integer recipeId) {
        return getIngredients(getRecipeUri(recipeId));
    }

    public Cursor getIngredients(final Uri recipeUri)
    {
        return context.getContentResolver().query(
                    recipeUri.buildUpon().appendPath("ingredient").build(),
                    new ContentBridge.ProjectionResolver(RecipeIngredient.class).toProjectionArray(),
                    null, null, null);
    }

    public List<RecipeIngredient> getIngredientObjects(final Uri recipeUri)
    {
        final List<RecipeIngredient> ingredients = new ArrayList<RecipeIngredient>();
        final Cursor ingredientsCursor = getIngredients(recipeUri);
        while (ingredientsCursor.moveToNext()) ingredients.add(toIngredient(ingredientsCursor));
        return ingredients;
    }

    public RecipeIngredient toIngredient(final Cursor cursor)
    {
        final RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.setName(cursor.getString(cursor.getColumnIndex("name")));
        ingredient.setAmount(new Volume());
        ingredient.getAmount().setNumber(cursor.getFloat(cursor.getColumnIndex("amount")));
        ingredient.getAmount().setUnit(cursor.getString(cursor.getColumnIndex("amountUnit")));
        return ingredient;
    }

    public void deleteRecipe(final Integer recipeId)
    {
        final Uri recipeUri = getRecipeUri(recipeId);
        final Cursor ftsIdCursor = context.getContentResolver().query(recipeUri, new String[] { "recipe_fts_id" }, null, null, null);
        String recipeFtsId = null;
        if (ftsIdCursor.moveToNext()) recipeFtsId = ftsIdCursor.getString(0);

        getContentResolver().delete(
            getRecipeUri(recipeId), null, null);

        if (recipeFtsId != null) {
            context.getContentResolver().delete(getRecipeSearchUri(recipeFtsId), null, null);
        }

        context.getContentResolver().delete(getRecipeIngredientsUri(recipeId), null, null);
    }

    /**
     * add recipe
     *    insert into recipe, ingredient, and recipe_fts
     */ 
    public void addRecipe(final Recipe recipe)
    {
        if (recipe.getName() == null || recipe.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe requires a name.");
        }
        if ((recipe.getSummary() == null || recipe.getSummary().trim().isEmpty())
            && recipe.getIngredients().isEmpty())
        {
            throw new IllegalArgumentException("Recipe requires either a summary or at least one ingredient.");
        }
        
        final ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", recipe.getName());
        recipeValues.put("summary", recipe.getSummary());

        final ContentValues recipeFtsValues = new ContentValues(recipeValues);
        recipeFtsValues.put("ingredients", new IngredientByNameFunction().joinedString(recipe.getIngredients(), "|"));

        final Uri recipeFtsUri = context.getContentResolver().insert(ContentBridge.Search.uriFor(Recipe.class), recipeFtsValues);

        Log.d(getClass().getName(), String.format("Inserted Recipe into FTS: resultingUri=%s", recipeFtsUri.toString()));

        recipeValues.put("recipe_fts_id", Integer.valueOf(recipeFtsUri.getLastPathSegment()));
        recipeValues.put("createdAt", System.currentTimeMillis());
        recipeValues.put("author", recipe.getAuthor());
        
        final Uri recipeUri = context.getContentResolver().insert(ContentBridge.uriFor(Recipe.class), recipeValues);

        Log.d(getClass().getName(), String.format("Inserted Recipe: resultingUri=%s", recipeUri.toString()));

        recipe.setId(getIdFromUri(recipeUri));

        for (final RecipeIngredient ingredient: recipe.getIngredients())
        {
            final ContentValues ingredientValues = new ContentValues();
            ingredientValues.put("name", ingredient.getName());
            
            if (ingredient.getAmount() != null) {
                ingredientValues.put("amount", ingredient.getAmount().getNumber());
                ingredientValues.put("amountUnit", ingredient.getAmount().getUnit());
            }

            final Uri ingredientUri =
                    context.getContentResolver().insert(
                        ingredientContract.uriUnderneath(Recipe.class, recipe.getId()),
                        ingredientValues);
            
            ingredient.setId(Integer.valueOf(ingredientUri.getLastPathSegment()));

            Log.d(getClass().getName(), String.format("Inserted Recipe ingredient: resultingUri=%s", ingredientUri.toString()));
        }
    }

    private static class IngredientByNameFunction extends Function<RecipeIngredient,String>
    {
        @Override
        public String get(final RecipeIngredient in) { return in.getName(); }
    }
}