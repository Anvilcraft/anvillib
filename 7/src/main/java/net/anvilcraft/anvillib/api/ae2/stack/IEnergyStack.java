package net.anvilcraft.anvillib.api.ae2.stack;

import appeng.api.storage.data.IAEStack;
import net.anvilcraft.anvillib.api.units.IEnergyUnit;

public interface IEnergyStack extends IAEStack<IEnergyStack> {

    /**
     * @return the energy unit of this stack
     */
    IEnergyUnit getEnergyType();

    /**
     * @return the energy amount of this stack in the units of {@link IEnergyStack#getEnergySystem()}
     */
    double getEnergy();

    /**
     * sets the energy amount of this stack. 
     * @param energy the new energy amount in the units of {@link IEnergyStack#getEnergySystem()}
     */
    void setEnergy(double energy);

    /**
     * @return the energy amount if this stack in AE units (0 if not convertible)
     */
    double getAsAE();
    
}
