package net.anvilcraft.anvillib.recipe;

import java.util.function.Predicate;

import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class IngredientsContainPredicate implements Predicate<RecipeHolder<?>> {
    public Predicate<Ingredient> pred;

    public IngredientsContainPredicate(Predicate<Ingredient> pred) {
        this.pred = pred;
    }

    @Override
    public boolean test(RecipeHolder<?> r) {
        return r.value().getIngredients().stream().anyMatch(this.pred);
    }
}
