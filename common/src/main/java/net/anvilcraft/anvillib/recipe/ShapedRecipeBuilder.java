package net.anvilcraft.anvillib.recipe;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.block.Block;

public class ShapedRecipeBuilder {
    public ResourceLocation ident;
    public String[] pattern;
    public Map<Character, Ingredient> ingredients = new HashMap<>();
    public ItemStack output;

    public ShapedRecipeBuilder(ResourceLocation ident, ItemStack output) {
        this.ident = ident;
        this.output = output;
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
        return this.ingredient(c, Ingredient.of(is));
    }

    public ShapedRecipeBuilder ingredient(char c, Item i) {
        return this.ingredient(c, new ItemStack(i));
    }

    public ShapedRecipeBuilder ingredient(char c, Block b) {
        return this.ingredient(c, new ItemStack(b));
    }

    public ShapedRecipeBuilder ingredient(char c, String s) {
        if (s.charAt(0) == '#') {
            return this.tagIngredient(c, ResourceLocation.parse(s.substring(1)));
        }

        var ident = ResourceLocation.parse(s);
        var maybeItem = BuiltInRegistries.ITEM.getOptional(ident).orElse(Items.AIR);
        if (maybeItem == Items.AIR) {
            var maybeBlock = BuiltInRegistries.BLOCK.getOptional(ident).orElse(null);
            if (maybeBlock == null)
                throw new IllegalArgumentException(
                    "ID " + s + " not found in item or block registry!"
                );
            return this.ingredient(c, maybeBlock);
        }

        return this.ingredient(c, maybeItem);
    }

    public ShapedRecipeBuilder tagIngredient(char c, ResourceLocation t) {
        return this.ingredient(c, Ingredient.of(TagKey.create(BuiltInRegistries.ITEM.key(), t)));
    }

    public ShapedRecipe build() {
        ShapedRecipePattern pattern = ShapedRecipePattern.of(this.ingredients, Arrays.asList(this.pattern));
        return new ShapedRecipe("", CraftingBookCategory.MISC, pattern, this.output);
    }
}
