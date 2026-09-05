package net.anvilcraft.anvillib.api.ae2.stack;

import appeng.api.storage.data.IAEStack;
import net.anvilcraft.anvillib.api.units.IUnit;

public interface IMatterStack extends IAEStack<IMatterStack> {
    
    /**
     * @return the unit of this stack
     */
    IUnit getMatterType();

}
