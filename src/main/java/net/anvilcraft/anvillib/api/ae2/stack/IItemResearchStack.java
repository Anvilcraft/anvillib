package net.anvilcraft.anvillib.api.ae2.stack;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IItemResearchStack extends IAEStack<IItemResearchStack> {

    /**
     * gives the research progress for this item. 
     * 0 = not researched, 1 = fully researched
     * @return the research progress
     */
    double getResearchProgress();

    /**
     * sets a new research progress for this item.
     * 0 = not researched, 1 = fully researched
     * @param researchProgress the research progress
     */
    void setResearchProgress(double researchProgress);

    /**
     * creates a IAEItemStack for the item.
     *
     * @return new IAEItemStack
     */
    IAEItemStack getItemStack();

    /**
     * quick way to get access to the MC Item Definition.
     *
     * @return item definition
     */
    Item getItem();

    /**
     * @return the items damage value
     */
    int getItemDamage();

    /**
     * compare the item/damage/nbt of the stack.
     *
     * @param stored to be compared item
     * @return true if it is the same type (same item, damage, nbt)
     */
    boolean isSameType(IItemResearchStack stack);

    /**
     * compare the item/damage/nbt of the stack.
     *
     * @param otherStack to be compared item
     * @return true if it is the same type (same item, damage, nbt)
     */
    boolean isSameType(IAEItemStack otherStack);

    /**
     * compare the item/damage/nbt of the stack.
     *
     * @param stored to be compared item
     * @return true if it is the same type (same item, damage, nbt)
     */
    boolean isSameType(ItemStack stored);
    
}
