package net.anvilcraft.anvillib.recipe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

public class ShapedRecipeBuilder {
    public Identifier ident;
    public String[] pattern;
    public Map<Character, Ingredient> ingredients = new HashMap<>();
    public ItemStack output;

    public ShapedRecipeBuilder(Identifier ident, ItemStack output) {
        this.ident = ident;
        this.output = output;
        this.ingredient(' ', Blocks.AIR);
    }

    public ShapedRecipeBuilder pattern(String... pat) {
        this.pattern = pat;
        return this;
    }

    public ShapedRecipeBuilder ingredient(char c, Ingredient i) {
        this.ingredients.put(c, i);
        return this;
    }

    public ShapedRecipeBuilder ingredient(char c, ItemStack... is) {
        return this.ingredient(c, Ingredient.ofStacks(is));
    }

    public ShapedRecipeBuilder ingredient(char c, Item i) {
        return this.ingredient(c, new ItemStack(i));
    }

    public ShapedRecipeBuilder ingredient(char c, Block b) {
        return this.ingredient(c, new ItemStack(b));
    }

    public ShapedRecipeBuilder ingredient(char c, String s) {
        if (s.charAt(0) == '#') {
            return this.tagIngredient(c, new Identifier(s.substring(1)));
        }

        var ident = new Identifier(s);
        var maybeItem = Registry.ITEM.get(ident);
        if (maybeItem == null) {
            var maybeBlock = Registry.BLOCK.get(ident);
            if (maybeBlock == null)
                throw new IllegalArgumentException(
                    "ID " + s + " not found in item or block registry!"
                );

            return this.ingredient(c, maybeBlock);
        }

        return this.ingredient(c, maybeItem);
    }

    public ShapedRecipeBuilder tagIngredient(char c, Identifier t) {
        return this.ingredient(c, Ingredient.fromTag(TagKey.of(Registry.ITEM_KEY, t)));
    }

    public ShapedRecipe build() {
        int width = -1;
        for (String line : this.pattern) {
            if (width != -1 && width != line.length())
                throw new IllegalArgumentException(
                    "Lines in crafting pattern must be same width!"
                );
            width = line.length();
        }

        DefaultedList<Ingredient> ingredients = DefaultedList.of();
        Arrays.stream(this.pattern)
            .flatMap(s -> s.chars().mapToObj(c -> (char) c))
            .map(this.ingredients::get)
            .forEach(ingredients::add);

        return new ShapedRecipe(
            this.ident, "", width, this.pattern.length, ingredients, this.output
        );
    }
}
