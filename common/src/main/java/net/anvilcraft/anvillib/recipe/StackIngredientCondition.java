package net.anvilcraft.anvillib.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient.Entry;
import net.minecraft.recipe.Ingredient.StackEntry;

public class StackIngredientCondition extends AbstractIngredientCondition {
    public ItemStack stack;

    public StackIngredientCondition(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean entryMatches(Entry e) {
        return e instanceof StackEntry se && se.stack.isItemEqual(this.stack);
    }
}
