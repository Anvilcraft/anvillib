package net.anvilcraft.anvillib.recipe;

import java.util.function.Predicate;

import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;

public class IngredientsContainPredicate implements Predicate<Recipe<?>> {
    public Predicate<Ingredient> pred;

    public IngredientsContainPredicate(Predicate<Ingredient> pred) {
        this.pred = pred;
    }

    @Override
    public boolean test(Recipe<?> r) {
        return r.getIngredients().stream().anyMatch(this.pred);
    }
}
