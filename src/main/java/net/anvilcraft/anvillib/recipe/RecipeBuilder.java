package net.anvilcraft.anvillib.recipe;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RecipeBuilder {
    IRecipeAdapter adapter;

    public RecipeBuilder(IRecipeAdapter adapter) {
        this.adapter = adapter;
    }

    public RecipeBuilder output(ItemStack stack) {
        this.adapter.setOutput(stack);
        return this;
    }

    public RecipeBuilder output(Item item) {
        this.adapter.setOutput(new ItemStack(item));
        return this;
    }

    public RecipeBuilder output(Block block) {
        this.adapter.setOutput(new ItemStack(block));
        return this;
    }

    public RecipeBuilder output(Block block, int count) {
        this.adapter.setOutput(new ItemStack(block, count));
        return this;
    }

    public RecipeBuilder output(Item item, int count) {
        this.adapter.setOutput(new ItemStack(item, count));
        return this;
    }

    public RecipeBuilder pattern(String... pat) {
        this.adapter.setPattern(pat);
        return this;
    }

    public RecipeBuilder ingredient(Object... i) {
        this.adapter.addIngredient(i);
        return this;
    }

    public void register() {
        GameRegistry.addRecipe(this.adapter.create());
    }
}
