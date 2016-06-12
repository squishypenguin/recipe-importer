package com.rgm.recipeimporter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

// TODO tests
public class IngredientTagsBuilder
{
	private List<String> ingredientsBuffer;
	
	public IngredientTagsBuilder withIngredientsList(List<String> ingredientsBuffer)
	{
		this.ingredientsBuffer = ingredientsBuffer;
		return this;
	}
	
	public IngredientTagsBuilder withIngredientsBlob(String ingredientsBlob)
	{
		final String[] lines = StringUtils.split(ingredientsBlob, "\n");
		this.ingredientsBuffer = Lists.newArrayList(lines);
		return this;
	}
	
	public Set<String> build()
	{
		// TODO put these into filters, rules, etc to repackage and reuse
		final Set<String> uniqueIngredients = new HashSet<>();
		final String[] stopWords = {"drain","diagonally","if","juice","using","canned","are","nice","but","the","little","melted","brushing","drizzling","loaves","extra","firm","one","two","th","wet","shavings","pure","cold","lb","raw","unsweetened","strained","preference","depending","tbsp","tsp","lengthwise","refrigerated","package","uncooked","piece","smashed","julienne","good","toasted","lightly","julienned","head","few","dashes","your","spun","rounds","heaping","cooked","crumbled","boxes","defrosted","zested","splash","squeezed","handfuls","pitted","roasted","well","coarsely","on","salting","beaten","blend","squeezed","dry","toasted","grated","mixed","in","thin","shaved","the","of","turns","favorite","defrosted","crumbled","grated","lightly","packed","steamed","freshly","bottle","shelled","deveined","dressed","from","toppings","choose","additional","about","half","palmful","handfuls","hot","heat","level","vine","sprinkling","optional","serving","suggestion","cookied","trimmed","halves","pieces","pint","quartered","quarters","grated","medium","pan","your","parts","ground","for","whole","garnish","prepared","container","grilled","grill","jarred","jar","ounces","slices","dried","at","box","frozen","crushed","cloves","clove","ribs","stemmed","split","fresh","finely","ripe","andor","bunch","halved","pounds","pound","inch","thick","seeded","chunks","wedges","more","juiced","cups","cup","oz","can","tablespoons","bag","thawed","large","small","tablespoon","torn","shredded","small","chopped","cut","into","sliced","peeled","diced","to","taste","plus","a","pinch","teaspoon","minced","recipe","follows","thinly","preferably","teaspoons","roughly","cans","drained","rinsed"};
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
