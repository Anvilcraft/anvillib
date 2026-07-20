package net.anvilcraft.anvillib.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient.Value;
import net.minecraft.world.item.crafting.Ingredient.ItemValue;

public class StackIngredientCondition extends AbstractIngredientCondition {
    public ItemStack stack;

    public StackIngredientCondition(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public boolean entryMatches(Value e) {
        return e instanceof ItemValue se && ItemStack.isSameItem(se.item(), this.stack);
    }
}
