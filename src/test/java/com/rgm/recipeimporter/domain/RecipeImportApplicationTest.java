package com.rgm.recipeimporter.domain;

import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import com.rgm.recipeimporter.RecipeImportApplication;
import com.rgm.recipeimporter.dao.RecipeImportRepository;

@RunWith(MockitoJUnitRunner.class)
public class RecipeImportApplicationTest
{
	@Mock private RecipeImportRepository recipeImportRepository;
	@Captor private ArgumentCaptor<List<ImportedRecipeBean>> captor;
	
	@Before
	public void setup()
	{
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void verifyRecipesLoaded()
	{
		try
		{
			final RecipeImportApplication app = new RecipeImportApplication(recipeImportRepository);
			app.loadRecipes(getClass().getResource("/recipes.txt").getPath());
			
			verify(recipeImportRepository).saveRecipes(captor.capture());
			
			final List<ImportedRecipeBean> beans = captor.getValue();
			assertEquals(3, beans.size());
			
			final ImportedRecipeBean last = beans.get(2);
			final String notes = last.getNotes();
			assertTrue(!StringUtils.endsWith(notes, last.getUrl()));
		}
		catch (IOException i)
		{
			System.out.println("Failed to import recipes:");
			i.printStackTrace();
		}
	}
}
