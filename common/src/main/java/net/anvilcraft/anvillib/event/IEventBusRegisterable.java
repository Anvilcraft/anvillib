package net.anvilcraft.anvillib.event;

/**
 * IEventBusRegisterable describes a class which contains one or more event handlers to be
 * registered on the anvillib event bus.
 */
public interface IEventBusRegisterable {
    /**
     * Register this object's event handlers on the given bus.
     */
    public void registerEventHandlers(Bus bus);
}
