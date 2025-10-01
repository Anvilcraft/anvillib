package net.anvilcraft.anvillib.api.units;

public interface IUnit {
    
    /**
     * Gives the ID of this unit. Should be in a namespaced format (modid:id).
     * @return the ID of this unit
     */
    String getID();

    /**
     * @return the name of this unit
     */
    String getName();

    /**
     * @return the abbreviation of this unit
     */
    String getAbbreviation();

}
