package com.rgm.recipeimporter.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

@Entity @Table(name="imported_recipe")
@NoArgsConstructor
public @Data class ImportedRecipeBean
{
	@Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Lob @Column(name="attributes_blob")
	private String attributes;
	
	@Lob @NonNull @Column(name="ingredients_blob")
	private String ingredients;
	
	@Lob @NonNull @Column(name="directions_blob")
	private String directions;
	
	@NonNull @Column(name="name")
	private String name;
	
	@NonNull @Column(name="url")
	private String url;
	
	@Column(name="notes")
	private String notes;
	
	public ImportedRecipeBean(String name, List<String> attributes, List<String> ingredients, List<String> directions, String url, List<String> notes)
	{
		this.name = name;
		this.attributes = StringUtils.join(attributes, "\n");
		this.ingredients = StringUtils.join(ingredients, "\n");
		this.directions = StringUtils.join(directions, "\n");
		this.url = url;
		this.notes = CollectionUtils.isNotEmpty(notes) ? StringUtils.join(notes, "\n") : null;
	}
}
