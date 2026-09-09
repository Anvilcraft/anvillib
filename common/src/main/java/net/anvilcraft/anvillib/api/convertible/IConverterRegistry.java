package net.anvilcraft.anvillib.api.convertible;

import java.util.Collection;
import java.util.Optional;

public interface IConverterRegistry {

    <T> void registerConverter(Class<T> clazz, IConverter<T> converter);

    <T> Collection<IConverter<T>> getConvertersFor(Class<T> clazz);

    void addTarget(IConverter<?> converter, Class<?> target);

    <T> Optional<IConverter<T>> getForTarget(Class<T> clazz, Class<?> target);
    
}
