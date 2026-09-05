package net.anvilcraft.anvillib.recipe;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class ShapelessOreRecipeAdapter implements IRecipeAdapter {
    ItemStack output;
    ArrayList<Object> ingredients = new ArrayList<>();

    @Override
    public void setOutput(ItemStack item) {
        this.output = item;
    }

    @Override
    public void setPattern(String... pattern) {
        throw new UnsupportedOperationException("Shapeless recipe has no pattern!");
    }

    @Override
    public void addIngredient(Object... ingredients) {
        this.ingredients.add(ingredients[0]);
    }

    @Override
    public IRecipe create() {
        return new ShapelessOreRecipe(this.output, this.ingredients.toArray());
    }
}
