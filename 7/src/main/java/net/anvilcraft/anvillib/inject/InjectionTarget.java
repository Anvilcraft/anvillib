package net.anvilcraft.anvillib.inject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import net.anvilcraft.alec.jalec.factories.AlecUnexpectedRuntimeErrorExceptionFactory;
import net.anvilcraft.anvillib.api.inject.InjectionPriority;

public class InjectionTarget {
    
    Type type;
    Class<?> targetClass;
    Field targetField;
    String preferredImplementation;
    private boolean isStatic;
    private Function<Class<?>, Collection<Object>> instanceHandle = (c) -> Collections.emptySet();

    public InjectionTarget(Type type, Class<?> targetClass, Field targetField, String preferredImplementation) {
        this.type = getActualType(type, targetField);
        this.targetClass = targetClass;
        this.targetField = targetField;
        this.preferredImplementation = preferredImplementation;
        this.isStatic = Modifier.isStatic(targetField.getModifiers());
    }

    private Type getActualType(Type target, Field field) {
        Type inner = field.getGenericType();
        if (Map.class.equals(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType generics = (ParameterizedType) field.getGenericType();
            if (generics.getActualTypeArguments().length != 2) throw AlecUnexpectedRuntimeErrorExceptionFactory.PLAIN.createAlecException();
            if (String.class.equals(generics.getActualTypeArguments()[0])) inner = generics.getActualTypeArguments()[1];
        } else if (Collection.class.equals(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType generics = (ParameterizedType) field.getGenericType();
            if (generics.getActualTypeArguments().length != 1) throw AlecUnexpectedRuntimeErrorExceptionFactory.PLAIN.createAlecException();
            inner = generics.getActualTypeArguments()[0];
        } else if (Consumer.class.equals(field.getType()) && field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType generics = (ParameterizedType) field.getGenericType();
            if (generics.getActualTypeArguments().length != 1) throw AlecUnexpectedRuntimeErrorExceptionFactory.PLAIN.createAlecException();
            inner = generics.getActualTypeArguments()[0];
        }

        if (inner instanceof ParameterizedType) {
            ParameterizedType generic = (ParameterizedType) inner;
            if (target.equals(generic.getRawType())) return generic;
        } else if (inner.equals(target)) {
            return inner;
        }
        throw new IllegalArgumentException("Type mismatch: " + inner + " != " + target);
    }

    public void setInstances(Function<Class<?>, Collection<Object>> instances) {
        this.instanceHandle = instances;
    }

    public boolean isInjectable(Object obj) {
        if (obj == null) return false;
        if (type instanceof ParameterizedType) {
            ParameterizedType genericType = findGenericSupertype(obj.getClass(), ((ParameterizedType) type).getRawType());
            return type.equals(genericType);
        } 
        return Optional.of(type)
            .filter(t -> t instanceof Class)
            .map(t -> (Class<?>) t)
            .map(t -> t.isAssignableFrom(obj.getClass()))
            .orElse(false);
    }

    @SuppressWarnings({"rawtypes", "unchecked", "ALEC"})
    public void inject(Object obj, String id) {
        if (obj == null) return;
        Set<Object> inst = new HashSet<>();
        if (isStatic) {
            inst.add(null);
        } else {
            inst.addAll(instanceHandle.apply(targetClass));
        }

        targetField.setAccessible(true);

        int objectPriority = preferredImplementation.equals(id) ? Integer.MAX_VALUE : 
            Optional.ofNullable(getClass().getDeclaredAnnotation(InjectionPriority.class))
                .map(InjectionPriority::value)
                .orElse(0);
        
        Consumer<Object> inj = (o) -> {};

        if (Map.class.equals(targetField.getType())) {
            inj = (instance) -> {
                try {
                    Map map = Optional.ofNullable((Map) targetField.get(instance)).orElseGet(HashMap::new);
                    map.put(id, obj);
                    targetField.set(instance, map);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            };     
        } else if (Collection.class.equals(targetField.getType())) {
            inj = (instance) -> {
                try {
                    Collection col = Optional.ofNullable((Collection) targetField.get(instance)).orElseGet(HashSet::new);
                    col.add(obj);
                    targetField.set(instance, col);
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            };
        } else if (Consumer.class.equals(targetField.getType())) {
            inj = (instance) -> {
                try {
                    Optional.ofNullable((Consumer) targetField.get(instance)).ifPresent(c -> c.accept(obj));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            };
        } else if (targetField.getType().isAssignableFrom(obj.getClass())) {
            inj = (instance) -> {
                try {
                    Optional<Object> curr = Optional.ofNullable(targetField.get(instance));
                    int oldPrio = curr.map(o -> o.getClass().getDeclaredAnnotation(InjectionPriority.class))
                        .map(InjectionPriority::value)
                        .orElseGet(() -> curr.isPresent() ? 0 : Integer.MIN_VALUE);
                    if (objectPriority > oldPrio) {
                        targetField.set(instance, obj);
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            };
        }

        for (Object instance : inst) {
            inj.accept(instance);
        }
    }

    public ParameterizedType findGenericSupertype(Class<?> base, Type targetType) {
        Queue<Type> toCheck = new ArrayDeque<>();
        toCheck.add(base);
        for (Type t = toCheck.poll(); t != null; t = toCheck.poll()) {
            if (t.equals(Object.class)) {
                continue;
            } else if (t.equals(targetType)) {
                return null;
            } else if (t instanceof ParameterizedType && ((ParameterizedType) t).getRawType().equals(targetType)) {
                return (ParameterizedType) t;
            } else if (t instanceof ParameterizedType) {
                toCheck.add(((ParameterizedType) t).getRawType());
            } else if (t instanceof Class) {
                Class<?> clazz = (Class<?>) t;
                Optional.ofNullable(clazz.getGenericSuperclass()).ifPresent(toCheck::add);
                toCheck.addAll(Arrays.asList(clazz.getGenericInterfaces()));
            }
        }
        return null;
    }

}
