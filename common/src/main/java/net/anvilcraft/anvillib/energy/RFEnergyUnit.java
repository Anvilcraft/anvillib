package net.anvilcraft.anvillib.energy;

import net.anvilcraft.anvillib.api.units.IEnergyUnit;

public class RFEnergyUnit implements IEnergyUnit {

    @Override
    public String getID() {
        return "cofhlib:rf";
    }

    @Override
    public String getName() {
        return "Redstone Flux";
    }

    @Override
    public String getAbbreviation() {
        return "RF";
    }

    @Override
    public double joulesConversionRatio() {
        return 2.5;
    }
    
}
