package net.anvilcraft.anvillib.api.inject;

public interface IInjectionHandler {
    
    <C, T extends C> void inject(T obj, Class<C> type);

    <C, T extends C> void inject(String id, T obj, Class<C> type);

}
