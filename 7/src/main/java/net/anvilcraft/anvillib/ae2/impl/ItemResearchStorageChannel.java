package net.anvilcraft.anvillib.ae2.impl;

import appeng.api.AEApi;
import appeng.api.storage.channels.IItemStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import net.anvilcraft.anvillib.api.ae2.channel.IItemResearchStorageChannel;
import net.anvilcraft.anvillib.api.ae2.stack.IItemResearchStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemResearchStorageChannel extends BaseStorageChannel<IItemResearchStack> implements IItemResearchStorageChannel {

    public ItemResearchStorageChannel() {
        super(
            IItemResearchStack.class, 
            o -> {
                if (o instanceof IAEItemStack) {
                    return new ItemResearchStack((IAEItemStack) o);
                } else if (o instanceof ItemStack) {
                    IAEItemStack stack = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(o);
                    return new ItemResearchStack(stack);
                } else if (o instanceof Item) {
                    IAEItemStack stack = AEApi.instance().storage().getStorageChannel(IItemStorageChannel.class).createStack(new ItemStack((Item) o));
                    return new ItemResearchStack(stack);
                }
                return null;
            }, 
            ItemResearchStack::readFromPacket, 
            ItemResearchStack::readFromNBT
        );
    }
    
}
