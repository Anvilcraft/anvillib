package net.anvilcraft.anvillib.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

public interface IRecipeAdapter {
    public void setOutput(ItemStack item);

    public void setPattern(String... pattern);

    public void addIngredient(Object... ingredients);

    public IRecipe create();
}
