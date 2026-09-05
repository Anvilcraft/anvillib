package net.anvilcraft.anvillib.api.units;

public interface IUnitRegistry {
    
    /**
     * Registers a new unit, overwriting existing units with the same ID.
     * @param unit the unit to register
     */
    void register(IUnit unit);

    /**
     * Registers a new unit, if no unit with the same ID has been registered.
     * Returns the registered unit or the unit with the same ID from the registry.
     * @param unit the unit to register
     * @return the unit from the registry
     */
    IUnit getOrRegister(IUnit unit);

    /**
     * Gets a unit from an ID.
     * @param id the ID of the unit
     * @return the requested unit or null
     */
    IUnit get(String id);

    /**
     * Checks, if the registry contains a unit with a specific ID:
     * @param id the ID of the unit
     * @return true, if the registry contains that unit
     */
    boolean contains(String id);

}
