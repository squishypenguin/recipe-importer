package com.rgm.recipeimporter.dao;

import java.util.List;

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.persist.Transactional;
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
			saveRecipe(recipe);
		}
	}
	
	@Transactional //- nothing was inserted with this
	private void saveRecipe(ImportedRecipeBean recipe)
	{
		//entityManager.getTransaction().begin();
		entityManager.persist(recipe);
		/*
		try
		{
			entityManager.getTransaction().commit();
		}
		catch (Exception m)
		{
			System.out.println("Duplicate entry: " + m.getMessage());
		}*/
	}
}
