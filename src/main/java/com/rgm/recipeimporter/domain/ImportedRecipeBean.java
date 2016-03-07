package com.rgm.recipeimporter.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@Entity
@Table(name="imported_recipe")
public class ImportedRecipeBean
{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Lob @Column(name="attributes_blob")
	private String attributes;
	
	@Lob @Column(name="ingredients_blob")
	private String ingredients;
	
	@Lob @Column(name="directions_blob")
	private String directions;
	
	private String name;
	private String url;
	private String notes;
	
	public ImportedRecipeBean()
	{	
	}

	public ImportedRecipeBean(String name, List<String> attributes, List<String> ingredients, List<String> directions, String url, List<String> notes)
	{
		this.name = name;
		this.attributes = StringUtils.join(attributes, "\\n");
		this.ingredients = StringUtils.join(ingredients, "\\n");
		this.directions = StringUtils.join(directions, "\\n");
		this.url = url;
		this.notes = CollectionUtils.isNotEmpty(notes) ? StringUtils.join(notes, "\\n") : null;
	}
	
	public String getNotes()
	{
		return notes;
	}

	public void setNotes(String notes)
	{
		this.notes = notes;
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getAttributes()
	{
		return attributes;
	}

	public void setAttributes(String attributes)
	{
		this.attributes = attributes;
	}

	public String getIngredients()
	{
		return ingredients;
	}

	public void setIngredients(String ingredients)
	{
		this.ingredients = ingredients;
	}

	public String getDirections()
	{
		return directions;
	}

	public void setDirections(String directions)
	{
		this.directions = directions;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}
}
