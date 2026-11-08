package net.anvilcraft.anvillib.api.types;

import java.util.function.Function;
import java.util.function.Supplier;

public interface IReference<T> extends Supplier<T> {

    default boolean isNull() {
        return get() == null;
    }

    default <E> E apply(Function<T, E> function) {
        if (isNull()) return null;
        return function.apply(get());
    }

}