package net.anvilcraft.anvillib.recipe;

import java.util.function.Function;

import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * IRecipeMapper describes a class that knows how to conditionally replace recipes.
 */
public interface IRecipeMapper extends Function<RecipeHolder<?>, RecipeHolder<?>> {
    boolean shouldMap(RecipeHolder<?> recipe);
}
