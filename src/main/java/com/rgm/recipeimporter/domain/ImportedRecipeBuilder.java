package com.rgm.recipeimporter.domain;

import java.util.List;

import lombok.NoArgsConstructor;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor
public class ImportedRecipeBuilder
{
	private String name;
	private List<String> attributes;
	private List<String> ingredients;
	private List<String> directions;
	private String url;
	private List<String> notes;
	
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
		return new ImportedRecipeBean(name, attributes, ingredients, directions, url, notes);
	}
}
