package net.anvilcraft.anvillib.recipe;

import java.util.function.Consumer;

import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Block;

public class ShapelessRecipeBuilder {
    public ResourceLocation ident;
    public NonNullList<Ingredient> ingredients = NonNullList.create();
    public ItemStack output;

    public ShapelessRecipeBuilder(ResourceLocation ident, ItemStack output) {
        this.ident = ident;
        this.output = output;
    }

    public ShapelessRecipeBuilder ingredient(Ingredient i) {
        this.ingredients.add(i);
        return this;
    }

    public ShapelessRecipeBuilder ingredient(ItemStack... is) {
        return this.ingredient(Ingredient.of(is));
    }

    public ShapelessRecipeBuilder ingredient(Item i) {
        return this.ingredient(new ItemStack(i));
    }

    public ShapelessRecipeBuilder ingredient(Block b) {
        return this.ingredient(new ItemStack(b));
    }

    public ShapelessRecipeBuilder ingredient(String s) {
        if (s.charAt(0) == '#') {
            return this.tagIngredient(ResourceLocation.parse(s.substring(1)));
        }

        var ident = ResourceLocation.parse(s);
        var maybeItem = BuiltInRegistries.ITEM.getOptional(ident).orElse(Items.AIR);
        if (maybeItem == Items.AIR) {
            var maybeBlock = BuiltInRegistries.BLOCK.getOptional(ident).orElse(null);
            if (maybeBlock == null)
                throw new IllegalArgumentException(
                    "ID " + s + " not found in item or block registry!"
                );
            return this.ingredient(maybeBlock);
        }

        return this.ingredient(maybeItem);
    }

    public ShapelessRecipeBuilder ingredient(TagKey<Item> t) {
        return this.ingredient(Ingredient.of(t));
    }

    public ShapelessRecipeBuilder tagIngredient(ResourceLocation t) {
        return this.ingredient(Ingredient.of(TagKey.create(BuiltInRegistries.ITEM.key(), t)));
    }

    public ShapelessRecipeBuilder repeat(int count, Consumer<ShapelessRecipeBuilder> srb) {
        for (int i = 0; i < count; i++) {
            srb.accept(this);
        }
        return this;
    }

    public RecipeHolder<ShapelessRecipe> build() {
        return new RecipeHolder<ShapelessRecipe>(ident, new ShapelessRecipe("", CraftingBookCategory.MISC, output, ingredients));
    }
}
