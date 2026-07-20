package net.anvilcraft.anvillib;

import java.util.Optional;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public class Util {
    public static ItemStack stackFromRegistry(ResourceLocation id) {
        Optional<Item> optItem = BuiltInRegistries.ITEM.getOptional(id);
        if (optItem.isPresent() && optItem.get() != Items.AIR) {
            return new ItemStack(optItem.get());
        }
        Optional<Block> optBlock = BuiltInRegistries.BLOCK.getOptional(id);
        if (optBlock.isPresent()) {
            return new ItemStack(optBlock.get());
        }
        throw new IllegalArgumentException("No block or item with ID " + id + "!");
    }

    public static Ingredient ingredientFromString(String s) {
        if (s.charAt(0) == '#')
            return Ingredient.of(
                TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.parse(s.substring(1)))
            );

        return Ingredient.of(stackFromRegistry(ResourceLocation.parse(s)));
    }
}
