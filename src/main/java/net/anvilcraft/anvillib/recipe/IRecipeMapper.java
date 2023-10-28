package net.anvilcraft.anvillib.recipe;

import java.util.function.Function;

import net.minecraft.recipe.Recipe;

/**
 * IRecipeMapper describes a class that knows how to conditionally replace recipes.
 */
public interface IRecipeMapper extends Function<Recipe<?>, Recipe<?>> {
    public boolean shouldMap(Recipe<?> recipe);
}
