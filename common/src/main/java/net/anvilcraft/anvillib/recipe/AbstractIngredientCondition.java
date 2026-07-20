package net.anvilcraft.anvillib.recipe;

import java.util.Arrays;
import java.util.function.Predicate;

import net.anvilcraft.anvillib.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;

public abstract class AbstractIngredientCondition implements Predicate<Ingredient> {
    public static AbstractIngredientCondition of(String s) {
        return s.charAt(0) == '#'
            ? new TagIngredientCondition(ResourceLocation.parse(s.substring(1)))
            : new StackIngredientCondition(Util.stackFromRegistry(ResourceLocation.parse(s)));
    }

    @Override
    public boolean test(Ingredient i) {
        return Arrays.stream(i.values).anyMatch(this::entryMatches);
    }

    public abstract boolean entryMatches(Ingredient.Value e);
}
