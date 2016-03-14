package com.rgm.recipeimporter.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
public class ImportedRecipeBuilder
{
	@Getter private String name;
	private List<String> attributes = new ArrayList<>();
	private List<String> ingredients = new ArrayList<>();
	private Set<String> ingredientsTags = new HashSet<>();
	private List<String> directions = new ArrayList<>();
	private String url;
	private List<String> notes = new ArrayList<>();
	
	public ImportedRecipeBuilder withName(String name)
	{
		this.name = name;
		return this;		
	}
	
	public ImportedRecipeBuilder withAttributes(List<String> attributes)
	{
		this.attributes = attributes;
		return this;
	}
	
	public ImportedRecipeBuilder withIngredients(List<String> ingredients)
	{
		this.ingredients = ingredients;
		return this;
	}
	
	public ImportedRecipeBuilder withIngredientsTags(Set<String> ingredientsTags)
	{
		this.ingredientsTags = ingredientsTags;
		return this;
	}
	
	public ImportedRecipeBuilder withDirections(List<String> directions)
	{
		this.directions = directions;
		return this;
	}
	
	public ImportedRecipeBuilder withUrl(String url)
	{
		this.url = url;
		return this;
	}
	
	public ImportedRecipeBuilder withNotes(List<String> notes)
	{
		this.notes = notes;
		return this;
	}
	
	public ImportedRecipeBean build() throws IllegalArgumentException
	{
		if (StringUtils.isBlank(this.name))
		{
			throw new IllegalArgumentException("Invalid recipe, missing name");
		}
		else if (CollectionUtils.isEmpty(this.ingredients))
		{
			throw new IllegalArgumentException("Invalid recipe, missing ingredients: " + this.name);
		}
		else if (CollectionUtils.isEmpty(this.directions))
		{
			throw new IllegalArgumentException("Invalid recipe, missing directions: " + this.name);
		}
		else if (StringUtils.isBlank(url))
		{
			throw new IllegalArgumentException("Invalid recipe, missing url: " + this.name);
		}
		
		// purposely put bean into its own class since it needs the entity annotations
		return new ImportedRecipeBean(name, attributes, ingredients, ingredientsTags, directions, url, notes);
	}
}
