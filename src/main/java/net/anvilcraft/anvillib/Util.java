package net.anvilcraft.anvillib;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.registries.ForgeRegistries;

public class Util {
    public static ItemStack stackFromRegistry(Identifier id) {
        if (ForgeRegistries.ITEMS.containsKey(id)) {
            return new ItemStack(ForgeRegistries.ITEMS.getValue(id));
        } else if (ForgeRegistries.BLOCKS.containsKey(id)) {
            return new ItemStack(ForgeRegistries.BLOCKS.getValue(id));
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
