package net.anvilcraft.anvillib.recipe;

import java.util.Arrays;
import java.util.function.Predicate;

import net.anvilcraft.anvillib.Util;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

public abstract class AbstractIngredientCondition implements Predicate<Ingredient> {
    public static AbstractIngredientCondition of(String s) {
        return s.charAt(0) == '#'
            ? new TagIngredientCondition(new Identifier(s.substring(1)))
            : new StackIngredientCondition(Util.stackFromRegistry(new Identifier(s)));
    }

    @Override
    public boolean test(Ingredient i) {
        return Arrays.stream(i.entries).anyMatch(this::entryMatches);
    }

    public abstract boolean entryMatches(Ingredient.Entry e);
}
