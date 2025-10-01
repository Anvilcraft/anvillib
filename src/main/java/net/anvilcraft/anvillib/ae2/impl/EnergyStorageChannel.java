package net.anvilcraft.anvillib.ae2.impl;

import net.anvilcraft.anvillib.api.ae2.channel.IEnergyStorageChannel;
import net.anvilcraft.anvillib.api.ae2.stack.IEnergyStack;
import net.anvilcraft.anvillib.api.units.IEnergyUnit;

public class EnergyStorageChannel extends BaseStorageChannel<IEnergyStack> implements IEnergyStorageChannel {

    public EnergyStorageChannel() {
        super(
            IEnergyStack.class, 
            o -> o instanceof IEnergyUnit ? new EnergyStack((IEnergyUnit) o): null,
            EnergyStack::readFromPacket,
            EnergyStack::readFromNBT
        );
    }
    
}
