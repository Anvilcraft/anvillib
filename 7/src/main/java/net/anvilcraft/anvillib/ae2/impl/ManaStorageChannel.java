package net.anvilcraft.anvillib.ae2.impl;

import net.anvilcraft.anvillib.api.ae2.channel.IManaStorageChannel;
import net.anvilcraft.anvillib.api.ae2.stack.IManaStack;
import net.anvilcraft.anvillib.api.units.IUnit;

public class ManaStorageChannel extends BaseStorageChannel<IManaStack> implements IManaStorageChannel {

    public ManaStorageChannel() {
        super(
            IManaStack.class, 
            o -> o instanceof IUnit ? new ManaStack((IUnit) o) : null, 
            ManaStack::readFromPacket, 
            ManaStack::readFromNBT
        );
    }
    
}
