package net.anvilcraft.anvillib.energy;

import net.anvilcraft.anvillib.api.units.IEnergyUnit;

public class UEEnergyUnit implements IEnergyUnit {

    @Override
    public String getID() {
        return "universalelectricity:joules";
    }

    @Override
    public String getName() {
        return "Joules";
    }

    @Override
    public String getAbbreviation() {
        return "J";
    }

    @Override
    public double joulesConversionRatio() {
        return 1;
    }
    
}
