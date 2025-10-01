package net.anvilcraft.anvillib.api.ae2.stack;

import appeng.api.storage.data.IAEStack;
import net.anvilcraft.anvillib.api.units.IUnit;

public interface IManaStack extends IAEStack<IManaStack> {
    
    /**
     * @return the unit of this stack
     */
    IUnit getManaType();

}
