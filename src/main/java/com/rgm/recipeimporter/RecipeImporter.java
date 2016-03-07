package com.rgm.recipeimporter;

import java.io.IOException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import com.google.inject.persist.jpa.JpaPersistModule;

// TODO add logger
public class RecipeImporter
{
	public static void main(String[] args)
	{
		final Injector injector = Guice.createInjector(new JpaPersistModule("imported-recipes"));
		injector.getInstance(PersistService.class).start();;
		
		final String fileName = "/Users/julie/Downloads/recipes.txt"; //args[0];
		
		try
		{
			final RecipeImportApplication app = injector.getInstance(RecipeImportApplication.class);
			app.loadRecipes(fileName);
			System.out.println("Imported recipes successfully.");
		}
		catch (IOException i)
		{
			System.out.println("Failed to import recipes:");
			i.printStackTrace();
		}
	}	
}
