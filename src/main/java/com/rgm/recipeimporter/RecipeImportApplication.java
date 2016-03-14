package com.rgm.recipeimporter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Cleanup;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
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
			String currentLine = lineIterator.next().trim();
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
				else
				{
					final CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
					if (!encoder.canEncode(currentLine))
					{
						System.out.println("ERROR: Fixme: recipe '" + recipeBuilder.getName() + "' has line with non-utf-8 character: '" + currentLine + "'");
					}
					currentLine = StringUtils.replaceEach(currentLine, new String[]{"1½","1¾","1⅓","½","¼","⅓","*","⅔","¾"}, new String[]{"1-1/2","1-3/4","1-1/3","1/2","1/4","1/3","","2/3","3/4"});
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
						recipeBuilder.withIngredientsTags(generateIngredientsTags(buffer));
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
	
	private Set<String> generateIngredientsTags(List<String> ingredientsBuffer)
	{
		// TODO put these into filters, rules, etc to repackage and reuse
		final Set<String> uniqueIngredients = new HashSet<>();
		final String[] stopWords = {"are","nice","but","the","little","melted","brushing","drizzling","loaves","extra","firm","one","two","th","wet","shavings","pure","cold","lb","raw","unsweetened","strained","preference","depending","tbsp","tsp","lengthwise","refrigerated","package","uncooked","piece","smashed","julienne","good","toasted","lightly","julienned","head","few","dashes","your","spun","rounds","heaping","cooked","crumbled","boxes","defrosted","zested","splash","squeezed","handfuls","pitted","roasted","well","coarsely","on","salting","beaten","blend","squeezed","dry","toasted","grated","mixed","in","thin","shaved","the","of","turns","favorite","defrosted","crumbled","grated","lightly","packed","steamed","freshly","bottle","shelled","deveined","dressed","from","toppings","choose","additional","about","half","palmful","handfuls","hot","heat","level","vine","sprinkling","optional","serving","suggestion","cookied","trimmed","halves","pieces","pint","quartered","quarters","grated","medium","pan","your","parts","ground","for","whole","garnish","prepared","container","grilled","grill","jarred","jar","ounces","slices","dried","at","box","frozen","crushed","cloves","clove","ribs","stemmed","split","fresh","finely","ripe","andor","bunch","halved","pounds","pound","inch","thick","seeded","chunks","wedges","more","juiced","cups","cup","oz","can","tablespoons","bag","thawed","large","small","tablespoon","torn","shredded","small","chopped","cut","into","sliced","peeled","diced","to","taste","plus","a","pinch","teaspoon","minced","recipe","follows","thinly","preferably","teaspoons","roughly","cans","drained","rinsed"};
		for (String ingredientLine : ingredientsBuffer)
		{
			String nonAlphaStripped = ingredientLine.replaceAll("\\(.*?\\)","");
			nonAlphaStripped = StringUtils.removePattern(nonAlphaStripped, "[/.\\d,]").trim();
			
			List<String> wordsList = Arrays.asList(nonAlphaStripped.split(" "));
			List<String> newWords = wordsList.stream().filter(s -> StringUtils.isAlpha(s.trim().toLowerCase()) && !ArrayUtils.contains(stopWords, s.trim().toLowerCase())).collect(Collectors.toList());
			nonAlphaStripped = StringUtils.join(newWords, " ").trim();
			nonAlphaStripped = StringUtils.removeEnd(nonAlphaStripped, " and");
			nonAlphaStripped = StringUtils.removeStart(nonAlphaStripped, "and ");
			nonAlphaStripped = StringUtils.removeEnd(nonAlphaStripped, " or");
			nonAlphaStripped = StringUtils.removeStart(nonAlphaStripped, "or ");
			nonAlphaStripped = StringUtils.removeEnd(nonAlphaStripped, " pass table");
			nonAlphaStripped = StringUtils.removeEnd(nonAlphaStripped, " as needed");
			nonAlphaStripped = StringUtils.removeEnd(nonAlphaStripped, " homemade or canned");
			
			if (nonAlphaStripped.contains(" and "))
			{
				String[] ingreds = nonAlphaStripped.split("\\sand\\s");
				uniqueIngredients.addAll(Arrays.asList(ingreds));
			}
			else if (nonAlphaStripped.contains(" or "))
			{
				String[] ingreds = nonAlphaStripped.split("\\sor\\s");
				uniqueIngredients.addAll(Arrays.asList(ingreds));
			}
			else
			{
				uniqueIngredients.add(nonAlphaStripped);
			}
			// but somehow handle: scallions white and green
			// bug: Before [1 teaspoon chili-garlic sauce] after [sauce]
			// bug: Before [2 teaspoons chicken-flavored powdered consomme] after [powdered consomme]
			// bug: Before [2 tablespoons chopped rosemary and/or oregano] after [rosemary oregano]
			// bug: Before [* 1 (15-ounce) container whole milk ricotta cheese] after [milk ricotta cheese]
			// handle: Before [2 pounds large shrimp, shells and tails on] after [shrimp shells and tails on]
			// handle: Before [4 strips bacon, cooked until crispy and crumbled] after [strips bacon cooked until crispy and crumbled]
			// bug: Before [* 1/2 pound orzo or rice-shaped pasta] after [orzo or pasta]
			// bug: Before [Optional toppings: shaved Parmesan, shredded cheddar or crumbled bacon] after [Parmesan cheddar or bacon]
			// bug: [1 tbsp. hot sauce (Cholula works well)] after [sauce]
			System.out.println("Before [" + ingredientLine + "] after [" + nonAlphaStripped + "]");
		}
		System.out.println("------------ Recipe Ingredients: " + StringUtils.join(uniqueIngredients,"|"));
		return uniqueIngredients;
	}
}
