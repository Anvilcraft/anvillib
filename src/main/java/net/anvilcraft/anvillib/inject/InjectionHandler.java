package net.anvilcraft.anvillib.inject;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.Loader;
import net.anvilcraft.anvillib.api.inject.IInjectionHandler;
import net.anvilcraft.anvillib.api.inject.Implementation;

public class InjectionHandler implements IInjectionHandler {

    Multimap<Class<?>, Object> instances = HashMultimap.create();
    Multimap<Class<?>, InjectionTarget> targets = HashMultimap.create();

    public InjectionHandler(Collection<InjectionTarget> targets) {
        for (InjectionTarget target : targets) {
            target.setInstances(instances::get);
            Type type = target.type;
            if (type instanceof ParameterizedType) {
                type = ((ParameterizedType) type).getRawType();
            }
            if (type instanceof Class) {
                this.targets.put((Class<?>) type, target);
            }
        }
    }

    public InjectionHandler() {
    
    }

    @Override
    public <C, T extends C> void inject(T obj, Class<C> type) {
        this.inject(obj.getClass().getCanonicalName(), obj, type);
    }

    @Override
    public <C, T extends C> void inject(String id, T obj, Class<C> type) {
        targets.get(type).stream().filter(t -> t.isInjectable(obj)).forEach(t -> t.inject(obj, id));
    }

    public void addTarget(InjectionTarget target) {
        target.setInstances(instances::get);
        Type type = target.type;
        if (type instanceof ParameterizedType) {
            type = ((ParameterizedType) type).getRawType();
        }
        if (type instanceof Class) {
            this.targets.put((Class<?>) type, target);
        }
    }

    public void addInstance(Object obj) {
        instances.put(obj.getClass(), obj);
    }

    public void populateModInstances() {
        Loader.instance()
            .getModList()
            .stream()
            .map(mc -> mc.getMod())
            .filter(Objects::nonNull)
            .forEach(this::addInstance);
    }

    public void injectSelf() {
        this.inject(this, IInjectionHandler.class);
    }

    public void initializeSingletons() {
        Collection<Object> classes = instances.get(Class.class);
        classes.stream()
            .filter(c -> c instanceof Class)
            .map(c -> (Class<?>) c)
            .map(c -> {
                try {
                    Constructor<?> con = c.getDeclaredConstructor();
                    con.setAccessible(true);
                    return con.newInstance();
                } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException e) {
                    e.printStackTrace();
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .forEach(this::addInstance);
    }

    @SuppressWarnings({"rawtypes", "unchecked", "ALEC"})
    public void injectSingletons() {
        instances.get(Class.class).stream()
            .filter(c -> c instanceof Class)
            .map(c -> (Class<?>) c)
            .flatMap(c -> instances.get(c).stream())
            .map(o -> Pair.of(o, o.getClass().getDeclaredAnnotation(Implementation.class)))
            .filter(p -> Objects.nonNull(p.getRight()))
            .flatMap(p -> Arrays.stream(p.getRight().value()).map(c -> Triple.of(p.getLeft(), p.getRight().id(), c)))
            .forEach(t -> {
                if (t.getMiddle().isEmpty()) {
                    this.inject(t.getLeft(), (Class) t.getRight());
                } else {
                    this.inject(t.getMiddle(), t.getLeft(), (Class) t.getRight());
                }
            });
    }

}
