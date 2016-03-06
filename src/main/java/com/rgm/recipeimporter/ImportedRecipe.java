package com.rgm.recipeimporter;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

public class ImportedRecipe
{
	final String name;
	final List<String> attributes;
	final List<String> ingredients;
	final List<String> directions;
	final String url;
	final List<String> notes;
	
	private ImportedRecipe(String name, List<String> attributes, List<String> ingredients, List<String> directions, String url, List<String> notes)
	{
		this.name = name;
		this.attributes = attributes;
		this.ingredients = ingredients;
		this.directions = directions;
		this.url = url;
		this.notes = notes;
	}
	
	public static class ImportedRecipeBuilder
	{
		private String name;
		private List<String> attributes;
		private List<String> ingredients;
		private List<String> directions;
		private String url;
		private List<String> notes;
		
		public ImportedRecipeBuilder() {}
		
		public ImportedRecipeBuilder withAttributes(List<String> attributes)
		{
			this.name = attributes.get(0);
			this.attributes = attributes.subList(1, attributes.size());
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
		
		public ImportedRecipe build() throws IllegalArgumentException
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
			return new ImportedRecipe(name, attributes, ingredients, directions, url, notes);
		}
	}

	public String getName()
	{
		return name;
	}

	public List<String> getAttributes()
	{
		return attributes;
	}

	public List<String> getIngredients()
	{
		return ingredients;
	}

	public List<String> getDirections()
	{
		return directions;
	}

	public String getUrl()
	{
		return url;
	}

	public List<String> getNotes()
	{
		return notes;
	}
}
