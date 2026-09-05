package ley.modding.tileralib.api;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IRegistry {
    String getModID();

    Item registerItem(Item item);

    Block registerBlock(Block block);

    Item getItem(String id);

    Block getBlock(String id);

    void addShapedRecipe(ItemStack output, String[] pattern, IIngredient[] ingredients);

    void addShapelessRecipe(ItemStack output, IIngredient[] input);
}
