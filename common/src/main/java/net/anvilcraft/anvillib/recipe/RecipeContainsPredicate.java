package net.anvilcraft.anvillib.recipe;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

public class RecipeContainsPredicate implements Predicate<Recipe<?>> {
    public ItemStack item;

    public RecipeContainsPredicate(ItemStack item) {
        this.item = item;
    }

    @Override
    public boolean test(Recipe<?> r) {
        return r.getIngredients() == null
            ? false
            : r.getIngredients().stream().anyMatch(new StackIngredientCondition(this.item)
              ) || r.getOutput().isItemEqual(this.item);
    }
}
