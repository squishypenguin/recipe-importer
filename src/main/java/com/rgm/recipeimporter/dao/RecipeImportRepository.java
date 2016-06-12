package com.rgm.recipeimporter.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.rgm.recipeimporter.domain.ImportedRecipeBean;

@Singleton
public class RecipeImportRepository
{
	private final EntityManager entityManager;
	
	@Inject
	public RecipeImportRepository(EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}
	
	public void saveRecipes(List<ImportedRecipeBean> recipes)
	{
		for (ImportedRecipeBean recipe : recipes)
		{
			if (recipe == null)
			{
				System.out.println("Null recipe passed down to save");
				continue;
			}
			saveRecipe(recipe);
		}
	}
	
	//@Transactional - nothing was inserted with this
	private void saveRecipe(ImportedRecipeBean recipe)
	{
		entityManager.getTransaction().begin();
		entityManager.persist(recipe);
		
		try
		{
			entityManager.getTransaction().commit();
		}
		catch (Exception m)
		{
			System.out.println("Duplicate entry: " + m.getMessage());
		}
	}
}
