package com.tinook.cocktailpond.model;

import java.io.Serializable;

import com.tinook.common.text.format.ParameterizedText;

/*
@Entity(
	name = "ingredient", parent = Recipe.class,
	columns = @Columns([name, amount, amountUnit])
)
From this the supposed DAO/content requesters and providers should infer a URI of the form,
    content://..../recipe/${recipeId}/ingredient
this is where ContentBridge should be going.
*/
public class RecipeIngredient implements Serializable
{
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private Volume amount;
	
	public Integer getId() { return id; }
	public void setId(final Integer id) { this.id = id; }

	public String getName() { return name; }
	public void setName(final String name) { this.name = name; }

	public Volume getAmount() { return amount; }
	public void setAmount(final Volume amount) { this.amount = amount; }

	@Override
	public String toString() {
		return new ParameterizedText("${name} - ${amount}")
					.param("name", name).param("amount", amount)
					.toString();
	}
}