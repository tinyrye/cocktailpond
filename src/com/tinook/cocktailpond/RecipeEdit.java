package com.tinook.cocktailpond;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tinook.cocktailpond.dao.RecipeRepository;

import com.tinook.cocktailpond.model.ContentBridge;
import com.tinook.cocktailpond.model.Recipe;
import com.tinook.cocktailpond.model.RecipeIngredient;
import com.tinook.cocktailpond.model.Volume;

import com.tinook.common.util.LogContext;

public class RecipeEdit extends Activity
{
    private final LogContext logContext = new LogContext(RecipeEdit.class);
    
    private final IngredientAdditionHandler ingredientAdditionHandler = new IngredientAdditionHandler();
	
    private Recipe recipe;
    
    public void determineRecipe()
    {
        final RecipeRepository repo = new RecipeRepository(this);
        Integer recipeId = null;

        if ((getIntent().getData() != null)
            && (recipeId = repo.getIdFromUri(getIntent().getData())) != null)
        {
            recipe = repo.getRecipeObject(getIntent().getData());
            recipe.setIngredients(repo.getIngredientObjects(getIntent().getData()));
        }
        else {
            recipe = new Recipe();
        }

        logContext.putAttribute("recipeUri", getIntent().getData());
        logContext.putAttribute("recipeId", recipeId);
    }

    @Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        determineRecipe();
		setContentView(R.layout.edit_recipe);

        ((EditText) findViewById(R.id.name)).setText(recipe.getName());
        ((EditText) findViewById(R.id.summary)).setText(recipe.getSummary());
        ((EditText) findViewById(R.id.author)).setText(recipe.getAuthor());

		((Button) findViewById(R.id.submit_recipe_button)).setOnClickListener(new SubmitRecipeChangesHandler());
        ((Button) findViewById(R.id.add_ingredient_button)).setOnClickListener(ingredientAdditionHandler);
        ((Button) findViewById(R.id.cancel_ingredient_button_add)).setOnClickListener(ingredientAdditionHandler);
        ((Button) findViewById(R.id.submit_ingredient_button_add)).setOnClickListener(ingredientAdditionHandler);

        final ListView existingIngredients = ((ListView) findViewById(R.id.existing_ingredients));

        existingIngredients.setAdapter(new ArrayAdapter<RecipeIngredient>(this, R.layout.existing_ingredient, recipe.getIngredients())
        {
            @Override
            public View getView(final int position, View viewAsIs, final ViewGroup parent)
            {
                final View listItem = getLayoutInflater().inflate(R.layout.existing_ingredient, null);
                final RecipeIngredient ingredient = getItem(position);
                ((TextView) listItem.findViewById(R.id.name)).setText(ingredient.getName());
                ((TextView) listItem.findViewById(R.id.amount)).setText(ingredient.getAmount().getNumber().toString());
                ((TextView) listItem.findViewById(R.id.amountUnit)).setText(ingredient.getAmount().getUnit());
                return listItem;
            }
        });
        
        existingIngredients.setEmptyView(findViewById(R.id.add_recipe_no_ingredients));
	}

    protected void submitIngredient()
    {
        final RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.setName(((EditText) findViewById(R.id.ingredient_name_edit)).getText().toString());
        ingredient.setAmount(Volume.parse(((EditText) findViewById(R.id.ingredient_amount_edit)).getText().toString()));
        recipe.getIngredients().add(ingredient);
    }

    protected class SubmitRecipeChangesHandler implements View.OnClickListener
    {
        @Override
        public void onClick(final View v)
        {
            recipe.setName(((EditText) findViewById(R.id.name)).getText().toString());
            recipe.setSummary(((EditText) findViewById(R.id.summary)).getText().toString());
            recipe.setAuthor(((EditText) findViewById(R.id.author)).getText().toString());

            try
            {
                new RecipeRepository(RecipeEdit.this).addRecipe(recipe);
                finish();
            }
            catch (IllegalArgumentException ex) {
                showInputError(ex.getMessage());
                return;
            }
        }
    }

    private static final Map<Integer,Integer> NEW_INGREDIENT_VIEW_VISIBILITY = new HashMap<Integer,Integer>();
    private static final Map<Integer,Integer> EXISTING_INGREDIENT_VIEW_VISIBILITY = new HashMap<Integer,Integer>();

    static
    {
        NEW_INGREDIENT_VIEW_VISIBILITY.put(R.id.add_ingredient_panel, View.VISIBLE);
        NEW_INGREDIENT_VIEW_VISIBILITY.put(R.id.add_ingredient_button, View.GONE);
        NEW_INGREDIENT_VIEW_VISIBILITY.put(R.id.cancel_ingredient_button_add, View.VISIBLE);
        NEW_INGREDIENT_VIEW_VISIBILITY.put(R.id.submit_ingredient_button_add, View.VISIBLE);
        NEW_INGREDIENT_VIEW_VISIBILITY.put(R.id.existing_ingredients, View.GONE);
        NEW_INGREDIENT_VIEW_VISIBILITY.put(R.id.add_recipe_no_ingredients, View.GONE);
        NEW_INGREDIENT_VIEW_VISIBILITY.put(R.id.add_ingredient_panel_error_feedback, View.GONE);

        EXISTING_INGREDIENT_VIEW_VISIBILITY.put(R.id.add_ingredient_panel, View.GONE);
        EXISTING_INGREDIENT_VIEW_VISIBILITY.put(R.id.add_ingredient_button, View.VISIBLE);
        EXISTING_INGREDIENT_VIEW_VISIBILITY.put(R.id.cancel_ingredient_button_add, View.GONE);
        EXISTING_INGREDIENT_VIEW_VISIBILITY.put(R.id.submit_ingredient_button_add, View.GONE);
        EXISTING_INGREDIENT_VIEW_VISIBILITY.put(R.id.existing_ingredients, View.VISIBLE);
        EXISTING_INGREDIENT_VIEW_VISIBILITY.put(R.id.add_recipe_no_ingredients, View.VISIBLE);
        EXISTING_INGREDIENT_VIEW_VISIBILITY.put(R.id.add_ingredient_panel_error_feedback, View.GONE);
    }

    protected class IngredientAdditionHandler implements View.OnClickListener
    {
        @Override
        public void onClick(final View v)
        {
            if (v.getId() == R.id.add_ingredient_button) {
                showNewIngredientPanel();
            }
            else if (v.getId() == R.id.cancel_ingredient_button_add) {
                clearIngredients();
                showExistingIngredientsPanel();
            }
            else if (v.getId() == R.id.submit_ingredient_button_add)
            {
                // add to recipes
                try
                {
                    submitIngredient();
                    clearIngredients();
                }
                catch (IllegalArgumentException ex) {
                    showInputError(ex.getMessage());
                    return;
                }

                showExistingIngredientsPanel();
            }
        }

        protected void showNewIngredientPanel()
        {
            ((EditText) getIngredientPanel().findViewById(R.id.ingredient_name_edit)).requestFocus();
            for (final Integer viewId: NEW_INGREDIENT_VIEW_VISIBILITY.keySet()) {
                findViewById(viewId).setVisibility(NEW_INGREDIENT_VIEW_VISIBILITY.get(viewId));
            }
        }

        protected void showExistingIngredientsPanel()
        {
            for (final Integer viewId: EXISTING_INGREDIENT_VIEW_VISIBILITY.keySet()) {
                findViewById(viewId).setVisibility(EXISTING_INGREDIENT_VIEW_VISIBILITY.get(viewId));
            }
        }

        protected View getIngredientPanel() {
            return findViewById(R.id.add_ingredient_panel);
        }

        protected void clearIngredients() {
            ((EditText) getIngredientPanel().findViewById(R.id.ingredient_name_edit)).setText("");
            ((EditText) getIngredientPanel().findViewById(R.id.ingredient_amount_edit)).setText("");
        }

        protected void hideInputError() {
            findViewById(R.id.add_ingredient_panel_error_feedback).setVisibility(View.GONE);
        }

        protected void showInputError(final String errorMessage) {
            final TextView text = ((TextView) findViewById(R.id.add_ingredient_panel_error_feedback));
            text.setText(errorMessage);
            text.setVisibility(View.VISIBLE);
        }
    }

    protected void hideInputError() {
        findViewById(R.id.add_recipe_error_feedback).setVisibility(View.GONE);
    }
    
    protected void showInputError(final String errorMessage) {
        final TextView text = ((TextView) findViewById(R.id.add_recipe_error_feedback));
        text.setText(errorMessage);
        text.setVisibility(View.VISIBLE);
    }
}