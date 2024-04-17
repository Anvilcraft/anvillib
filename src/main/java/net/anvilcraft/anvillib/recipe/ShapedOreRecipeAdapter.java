package net.anvilcraft.anvillib.recipe;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ShapedOreRecipeAdapter implements IRecipeAdapter {
    ItemStack output;
    String[] pattern;
    ArrayList<Object> ingredients = new ArrayList<>();

    @Override
    public void setOutput(ItemStack item) {
        this.output = item;
    }

    @Override
    public void setPattern(String... pattern) {
        this.pattern = pattern;
    }

    @Override
    public void addIngredient(Object... ingredients) {
        this.ingredients.add(ingredients[0]);
        this.ingredients.add(ingredients[1]);
    }

    @Override
    public IRecipe create() {
        ArrayList<Object> args = new ArrayList<>();

        for (String pat : this.pattern)
            args.add(pat);

        for (Object ing : this.ingredients)
            args.add(ing);

        return new ShapedOreRecipe(this.output, args.toArray());
    }
}
