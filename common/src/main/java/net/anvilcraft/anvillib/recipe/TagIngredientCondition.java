package net.anvilcraft.anvillib.recipe;

import net.minecraft.recipe.Ingredient.Entry;
import net.minecraft.recipe.Ingredient.TagEntry;
import net.minecraft.util.Identifier;

public class TagIngredientCondition extends AbstractIngredientCondition {
    public Identifier id;

    public TagIngredientCondition(Identifier id) {
        this.id = id;
    }

    @Override
    public boolean entryMatches(Entry e) {
        return e instanceof TagEntry te && te.tag.id().equals(this.id);
    }
}
