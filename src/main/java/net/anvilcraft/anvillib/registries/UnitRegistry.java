package net.anvilcraft.anvillib.registries;

import java.util.HashMap;
import java.util.Map;

import net.anvilcraft.anvillib.api.units.IUnit;
import net.anvilcraft.anvillib.api.units.IUnitRegistry;

public class UnitRegistry implements IUnitRegistry {

    public static IUnitRegistry INSTANCE = new UnitRegistry();

    private Map<String, IUnit> registry = new HashMap<>();

    @Override
    public void register(IUnit unit) {
        registry.put(unit.getID(), unit);
    }

    @Override
    public IUnit get(String id) {
        return registry.get(id);
    }

    @Override
    public IUnit getOrRegister(IUnit unit) {
        if (this.contains(unit.getID())) {
            return registry.get(unit.getID());
        }
        register(unit);
        return unit;
    }

    @Override
    public boolean contains(String id) {
        return registry.containsKey(id);
    }
    
}
