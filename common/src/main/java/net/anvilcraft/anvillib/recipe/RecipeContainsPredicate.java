package net.anvilcraft.anvillib.recipe;

import java.util.function.Predicate;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public class RecipeContainsPredicate implements Predicate<RecipeHolder<?>> {
    public ItemStack item;

    public RecipeContainsPredicate(ItemStack item) {
        this.item = item;
    }

    @Override
    public boolean test(RecipeHolder<?> r) {
        return r.value().getIngredients() == null
            ? false
            : r.value().getIngredients().stream().anyMatch(new StackIngredientCondition(this.item)
              ) || ItemStack.isSameItem(r.value().getResultItem(RegistryAccess.EMPTY), this.item);
    }
}
