package net.anvilcraft.anvillib.api.serialize;

import java.util.Collection;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Stream;

public interface IObjectManipulator<T, I> {
    
    T get(T object, I index) throws IllegalArgumentException, IndexOutOfBoundsException;

    T set(T object, I index, T value) throws IllegalArgumentException;

    int getSize(T object);

    Collection<I> getIndices(T object);

    T create();

    boolean isInstance(T object);

    Stream<Entry<I, T>> getValues(T object);

    @SuppressWarnings("null")
    default boolean contains(T object, T value) {
        return getValues(object)
            .map(Entry::getValue)
            .filter(Objects::nonNull)
            .anyMatch(v -> v.equals(value));
    }

}
