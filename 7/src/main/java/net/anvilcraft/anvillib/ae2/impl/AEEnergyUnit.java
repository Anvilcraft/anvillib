package net.anvilcraft.anvillib.ae2.impl;

import appeng.api.config.PowerUnits;
import net.anvilcraft.anvillib.api.units.IEnergyUnit;

public class AEEnergyUnit implements IEnergyUnit {

    @Override
    public String getID() {
        return "appliedenergistics2:ae";
    }

    @Override
    public String getName() {
        return "Applied Energistics";
    }

    @Override
    public String getAbbreviation() {
        return "AE";
    }

    @Override
    public double joulesConversionRatio() {
        return 1.0 / PowerUnits.MK.conversionRatio;
    }
    
}
