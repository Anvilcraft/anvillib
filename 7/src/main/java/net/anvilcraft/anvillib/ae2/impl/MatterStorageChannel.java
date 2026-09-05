package net.anvilcraft.anvillib.ae2.impl;

import net.anvilcraft.anvillib.api.ae2.channel.IMatterStorageChannel;
import net.anvilcraft.anvillib.api.ae2.stack.IMatterStack;
import net.anvilcraft.anvillib.api.units.IUnit;

public class MatterStorageChannel extends BaseStorageChannel<IMatterStack> implements IMatterStorageChannel {

    public MatterStorageChannel() {
        super(
            IMatterStack.class, 
            o -> o instanceof IUnit ? new MatterStack((IUnit) o) : null, 
            MatterStack::readFromPacket, 
            MatterStack::readFromNBT
        );
    }
    
}
