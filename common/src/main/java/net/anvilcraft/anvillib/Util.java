package net.anvilcraft.anvillib;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Util {
    public static ItemStack stackFromRegistry(Identifier id) {
        if (Registry.ITEM.containsId(id)) {
            return new ItemStack(Registry.ITEM.get(id));
        } else if (Registry.BLOCK.containsId(id)) {
            return new ItemStack(Registry.BLOCK.get(id));
        } else {
            throw new IllegalArgumentException("No block or item with ID " + id + "!");
        }
    }

    public static Ingredient ingredientFromString(String s) {
        if (s.charAt(0) == '#')
            return Ingredient.fromTag(
                TagKey.of(Registry.ITEM_KEY, new Identifier(s.substring(1)))
            );

        return Ingredient.ofStacks(stackFromRegistry(new Identifier(s)));
    }
}
