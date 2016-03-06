package com.rgm.recipeimporter;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;

import com.rgm.recipeimporter.ImportedRecipe.ImportedRecipeBuilder;

public class RecipeImporter
{

	public static void main(String[] args)
	{
		final String fileName = "/Users/julie/Downloads/recipes.txt"; //args[0];
		final List<ImportedRecipe> recipes = new RecipeImporter().loadRecipesFromFile(fileName);
		System.out.println("Imported " + recipes.size() + " recipes.");
	}
	
	private List<ImportedRecipe> loadRecipesFromFile(String fileName)
	{
		final List<ImportedRecipe> recipes = new ArrayList<>();
		// this is very, very ugly
		try (FileInputStream inputStream = new FileInputStream(fileName))
		{
			final List<String> buffer = new ArrayList<>();
			boolean hasNotes = false;
			ImportedRecipeBuilder recipeBuilder = new ImportedRecipeBuilder();
			
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
							recipeBuilder.withAttributes(buffer);
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
									final ImportedRecipe recipe = recipeBuilder.build();
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
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return recipes;
	}
}
