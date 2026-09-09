package net.anvilcraft.anvillib.api.units;

public interface IEnergyUnit extends IUnit {
    
    /**
     * The Joule Conversion Ration is the amount of Joules (Universal Electricity/Mekanism), that one
     * unit of this energy unit is equal to.
     * @return the joules conversion ratio of this unit
     */
    double joulesConversionRatio();

    /**
     * Converts a value from this unit to another unit.
     * @param target the unit, to which the energy should be converted
     * @param value the amount of energy in terms of this unit
     * @return the amount of energy in terms of {@code target}
     */
    default double convertTo(IEnergyUnit target, double value) {
        return (value * this.joulesConversionRatio()) / target.joulesConversionRatio();
    }

}
