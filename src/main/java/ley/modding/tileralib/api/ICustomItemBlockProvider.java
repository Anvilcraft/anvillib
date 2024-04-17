package ley.modding.tileralib.api;

import net.minecraft.item.ItemBlock;

public interface ICustomItemBlockProvider {
    Class<? extends ItemBlock> getItemBlockClass();
}
