package net.anvilcraft.anvillib.api.serialize;

public interface IObjectType<T> {

    IObjectSerializer<T, String> strings();

    IObjectSerializer<T, Boolean> booleans();

    IObjectSerializer<T, Long> integers();

    IObjectSerializer<T, Double> floats();

    IObjectManipulator<T, Integer> arrays();

    IObjectManipulator<T, String> objects();

    IObjectManipulator<T, T> collections();

    T createNull();

    boolean isNull(T object);

}
