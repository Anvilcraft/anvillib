package net.anvilcraft.anvillib.api.convertible;

import java.util.Optional;

public interface IConverter<T> {
    
    Optional<T> convertFrom(Object input);

    <E> Optional<E> convertTo(Class<E> clazz, T input);

}
