package net.anvilcraft.anvillib.api.types;

@FunctionalInterface
public interface IThrowingFunction<I, O, E extends Exception> {
    
    O accept(I input) throws E;

}
