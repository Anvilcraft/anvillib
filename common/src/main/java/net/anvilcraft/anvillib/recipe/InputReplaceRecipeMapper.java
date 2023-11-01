package net.anvilcraft.anvillib.recipe;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import net.anvilcraft.anvillib.Util;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;

public class InputReplaceRecipeMapper implements IRecipeMapper {
    public Map<Predicate<Ingredient>, Ingredient> replacements = new HashMap<>();

    public InputReplaceRecipeMapper replace(Predicate<Ingredient> p, Ingredient i) {
        this.replacements.put(p, i);
        return this;
    }

    public InputReplaceRecipeMapper replace(String p, Ingredient i) {
        return this.replace(AbstractIngredientCondition.of(p), i);
    }

    public InputReplaceRecipeMapper replace(String p, String i) {
        return this.replace(p, Util.ingredientFromString(i));
    }

    public InputReplaceRecipeMapper replace(Predicate<Ingredient> p, String i) {
        return this.replace(p, Util.ingredientFromString(i));
    }

    @Override
    public boolean shouldMap(Recipe<?> recipe) {
        var ingredients = recipe.getIngredients();
        if (ingredients == null)
            return false;

        for (var k : this.replacements.keySet())
            if (ingredients.stream().anyMatch(k))
                return true;
        return false;
    }

    @Override
    public Recipe<?> apply(Recipe<?> recipe) {
        var ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++) {
            var ing = ingredients.get(i);
            for (var entry : this.replacements.entrySet())
                if (entry.getKey().test(ing))
                    ingredients.set(i, entry.getValue());
        }
        return recipe;
    }
}
