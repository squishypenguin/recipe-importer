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

	@Inject public RecipeImportApplication(RecipeImportRepository repository)
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
		List<String> buffer = new ArrayList<>(); 
		ImportedRecipeBuilder recipeBuilder = new ImportedRecipeBuilder();
		boolean hasNotes = false;
		boolean nextIsName = true;
		
		// do scan from name to url and then pass that blob to recipe parser?
		@Cleanup final FileInputStream inputStream = new FileInputStream(fileName);
		final LineIterator lineIterator = IOUtils.lineIterator(inputStream, "UTF-8");
		while (lineIterator.hasNext())
		{
			final String currentLine = lineIterator.next().trim();
			if (StringUtils.isNotBlank(currentLine))
			{
				if (nextIsName)
				{
					recipeBuilder.withName(currentLine);
					nextIsName = false;
					continue;
				}
				else if (UrlValidator.getInstance().isValid(currentLine))
				{
					recipes.add(completeRecipe(recipeBuilder, buffer, hasNotes, currentLine));
					recipeBuilder = new ImportedRecipeBuilder();
					hasNotes = false;
					nextIsName = true;
					buffer = new ArrayList<>(); 
					continue;
				}
				
				// what type of line are we?
				switch (currentLine.toLowerCase())
				{
					case "ingredients":
					case "ingredients:":
						// all lines prior to this are the recipe attributes
						recipeBuilder.withAttributes(buffer); 
						buffer = new ArrayList<>(); 
						break;
					case "instructions":
					case "instructions:":
					case "directions":
					case "directions:":
						// all lines prior to this are the ingredients
						recipeBuilder.withIngredients(buffer); 
						buffer = new ArrayList<>(); 
						break;
					case "notes":
					case "notes:":
					case "tips":
					case "tips:":
						// tips are last so all lines before this are the directions
						recipeBuilder.withDirections(buffer);
						hasNotes = true;
						buffer = new ArrayList<>(); 
						break;
					default:
						buffer.add(currentLine);
				}
			}
		}
		return recipes;
	}
	
	private ImportedRecipeBean completeRecipe(ImportedRecipeBuilder recipeBuilder, List<String> buffer, boolean hasNotes, String currentLine)
	{
		recipeBuilder.withUrl(currentLine);

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
			return recipeBuilder.build();
		}
		catch (IllegalArgumentException i)
		{
			System.out.println(i.getMessage());
		}
		return null;
	}
}
