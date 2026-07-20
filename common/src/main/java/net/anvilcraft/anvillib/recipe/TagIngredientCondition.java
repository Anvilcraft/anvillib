package net.anvilcraft.anvillib.recipe;

import net.minecraft.world.item.crafting.Ingredient.Value;
import net.minecraft.world.item.crafting.Ingredient.TagValue;
import net.minecraft.resources.ResourceLocation;

public class TagIngredientCondition extends AbstractIngredientCondition {
    public ResourceLocation id;

    public TagIngredientCondition(ResourceLocation id) {
        this.id = id;
    }

    @Override
    public boolean entryMatches(Value e) {
        return e instanceof TagValue te && te.tag().location().equals(this.id);
    }
}
