package com.tinook.cocktailpond.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.tinook.common.content.model.ContentContract;
import com.tinook.common.content.model.ChildContentContract;
import com.tinook.common.content.model.EntityRelationCondition;
import com.tinook.common.content.model.RelatedContentContract;
import com.tinook.common.content.model.UriAlteration;

/**
 * A standard DAO should expect the URI for this entity to be
 * content://com.tinook.cocktailpond.provider/recipe/${id} where
 * id will be the id field of Recipe.
 */
@ContentContract(
	/* path = "recipe" defaults to this because the class name will convert to camel case. */
	/* idField ="id", defaults to id by convention. */
	children = {
		@ChildContentContract(
			value="ingredients",
			relationScheme=EntityRelationCondition.BY_PARENT_ID
		)
	},
	relations = {
		@RelatedContentContract("search")
	}
)
public class Recipe implements Serializable
{
	private static final long serialVersionUID = 1L;

	private Integer id;
	private String name;
	private String author;
	private Long createdAt;
	private String summary;
	private List<RecipeIngredient> ingredients = new ArrayList<RecipeIngredient>();
	
	public Integer getId() { return id; }
	public void setId(final Integer id) { this.id = id; }

	public String getName() { return name; }
	public void setName(final String name) { this.name = name; }

	public String getAuthor() { return author; }
	public void setAuthor(final String author) { this.author = author; }

	public Long getCreatedAt() { return createdAt; }
	public void setCreatedAt(final Long createdAt) { this.createdAt = createdAt; }

	public String getSummary() { return summary = summary; }
	public void setSummary(final String summary) { this.summary = summary; }
	
	public List<RecipeIngredient> getIngredients() { return ingredients; }
	public void setIngredients(final List<RecipeIngredient> ingredients) { this.ingredients = ingredients; }
}