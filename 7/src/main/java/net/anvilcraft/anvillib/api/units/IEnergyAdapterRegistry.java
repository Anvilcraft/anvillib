package net.anvilcraft.anvillib.api.units;

public interface IEnergyAdapterRegistry {
    
    void register(IEnergyAdapter adapter);

    IEnergyAdapter getFor(Object o);

}
