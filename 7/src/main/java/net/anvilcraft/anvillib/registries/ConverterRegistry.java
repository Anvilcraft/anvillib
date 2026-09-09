package net.anvilcraft.anvillib.registries;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import net.anvilcraft.anvillib.api.convertible.IConverter;
import net.anvilcraft.anvillib.api.convertible.IConverterRegistry;
import net.anvilcraft.anvillib.api.inject.Implementation;

@Implementation (IConverterRegistry.class)
public class ConverterRegistry implements IConverterRegistry {

    private Map<Class<?>, Set<IConverter<?>>> registry = new HashMap<>();
    private Set<Pair<Class<?>, IConverter<?>>> targets = new HashSet<>();

    @Override
    public void addTarget(IConverter<?> converter, Class<?> target) {
        targets.add(Pair.of(target, converter));
    }

    @SuppressWarnings({ "rawtypes", "unchecked", "ALEC" })
    @Override
    public <T> Collection<IConverter<T>> getConvertersFor(Class<T> clazz) {
        return (Set) registry.computeIfAbsent(clazz, (k) -> new HashSet<>());
    }

    @SuppressWarnings({ "unchecked", "ALEC" })
    @Override
    public <T> Optional<IConverter<T>> getForTarget(Class<T> clazz, Class<?> target) {
        return registry.computeIfAbsent(clazz, (k) -> new HashSet<>())
            .stream()
            .map(c -> Pair.of(target, c))
            .filter(targets::contains)
            .map(p -> (IConverter<T>) p.getRight())
            .findFirst();
    }

    @Override
    public <T> void registerConverter(Class<T> clazz, IConverter<T> converter) {
        registry.computeIfAbsent(clazz, (k) -> new HashSet<>()).add(converter);
    }
    
}
