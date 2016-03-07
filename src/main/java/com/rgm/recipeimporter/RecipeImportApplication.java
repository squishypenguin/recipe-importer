package com.rgm.recipeimporter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Cleanup;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

import com.google.inject.Inject;
import com.rgm.recipeimporter.dao.RecipeImportRepository;
import com.rgm.recipeimporter.domain.ImportedRecipeBean;
import com.rgm.recipeimporter.domain.ImportedRecipeBuilder;

// TODO add logger
public class RecipeImportApplication
{
	private final RecipeImportRepository repository;

	@Inject
	public RecipeImportApplication(RecipeImportRepository repository)
	{
		this.repository = repository;
	}
	
	public void loadRecipes(String fileName) throws IOException
	{
		List<ImportedRecipeBean> recipes = loadRecipesFromFile(fileName);
		System.out.println("Passing " + recipes.size() + " down to be saved.");
		repository.saveRecipes(recipes);
	}
	
	// this is very, very ugly
	private List<ImportedRecipeBean> loadRecipesFromFile(String fileName) throws IOException
	{
		final List<ImportedRecipeBean> recipes = new ArrayList<>();
		final List<String> buffer = new ArrayList<>(); 
		boolean hasNotes = false;
		ImportedRecipeBuilder recipeBuilder = new ImportedRecipeBuilder();
		
		@Cleanup final FileInputStream inputStream = new FileInputStream(fileName);
		final LineIterator lineIterator = IOUtils.lineIterator(inputStream, "UTF-8");
		while (lineIterator.hasNext())
		{
			final String currentLine = lineIterator.next().trim();
			if (StringUtils.isNotBlank(currentLine))
			{
				buffer.add(currentLine);
				
				// what type of line are we?
				switch (currentLine.toLowerCase())
				{
					case "ingredients":
					case "ingredients:":
						// all lines prior to this are the recipe name and its attributes
						recipeBuilder.withAttributes(buffer.subList(1, buffer.size()));
						recipeBuilder.withName(buffer.get(0));
						buffer.clear();
						break;
					case "instructions":
					case "instructions:":
					case "directions":
					case "directions:":
						// all lines prior to this are the ingredients
						recipeBuilder.withIngredients(buffer);
						buffer.clear();
						break;
					case "notes":
					case "notes:":
					case "tips":
					case "tips:":
						// tips are last so all lines before this are the directions
						recipeBuilder.withDirections(buffer);
						hasNotes = true;
						buffer.clear();
						break;
					default:
						// see if it's a url, if so, we're at the end of the recipe
						final boolean isUrl = UrlValidator.getInstance().isValid(currentLine);
						if (isUrl)
						{
							recipeBuilder.withUrl(currentLine);
							
							// all lines prior to this are the directions
							if (hasNotes)
							{
								recipeBuilder.withNotes(buffer);									
							}
							else
							{
								recipeBuilder.withDirections(buffer);
							}
							
							// recipe is complete, build it
							try
							{
								final ImportedRecipeBean recipe = recipeBuilder.build();
								recipes.add(recipe);
							}
							catch (IllegalArgumentException i)
							{
								System.out.println(i.getMessage());
							}
							
							recipeBuilder = new ImportedRecipeBuilder();
							hasNotes = false;
							buffer.clear();
							break;
						}
				}
			}
		}
		return recipes;
	}
}
