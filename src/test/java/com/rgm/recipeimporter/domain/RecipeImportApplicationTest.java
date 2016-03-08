package com.rgm.recipeimporter.domain;

import static org.mockito.Mockito.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
			
			final ImportedRecipeBean first = beans.get(0);
			assertTrue(first.getAttributes().startsWith("PREP TIME 5 mins"));
			assertTrue(first.getIngredients().startsWith("8 oz. udon noodles"));
			
			final ImportedRecipeBean second = beans.get(1);
			assertTrue(second.getAttributes().startsWith("COOK TIME 15 mins"));
			assertTrue(second.getIngredients().startsWith("* 4 cups cooked rice"));
			
			final ImportedRecipeBean last = beans.get(2);
			final String notes = last.getNotes();
			assertTrue(!StringUtils.endsWith(notes, last.getUrl()));
			assertTrue(last.getAttributes().startsWith("YIELD:makes 4 servings"));
			assertTrue(last.getIngredients().startsWith("1/4 cup cocoa powder"));
		}
		catch (IOException i)
		{
			fail("Failed to import recipes: " + i.getMessage());
		}
	}
}
