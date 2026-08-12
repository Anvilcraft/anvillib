package net.anvilcraft.anvillib.serialization;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapLike;

public class ReflectedJavaOps implements DynamicOps<Object> {

    public static ReflectedJavaOps INSTANCE = new ReflectedJavaOps(true);
    private final boolean includeSuperclasses;

    public ReflectedJavaOps(boolean includeSuperclasses) {
        this.includeSuperclasses = includeSuperclasses;
    }

    @Override
    public Object empty() {
        return null;
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, Object input) {
        if (input == null) {
            return outOps.empty();
        }
        if (input instanceof Map) {
            return convertMap(outOps, input);
        }
        if (input instanceof Collection || input instanceof Object[]) {
            return convertList(outOps, input);
        }
        if (input instanceof final String value) {
            return outOps.createString(value);
        }
        if (input instanceof final Boolean value) {
            return outOps.createBoolean(value);
        }
        if (input instanceof final Byte value) {
            return outOps.createByte(value);
        }
        if (input instanceof final Short value) {
            return outOps.createShort(value);
        }
        if (input instanceof final Integer value) {
            return outOps.createInt(value);
        }
        if (input instanceof final Long value) {
            return outOps.createLong(value);
        }
        if (input instanceof final Float value) {
            return outOps.createFloat(value);
        }
        if (input instanceof final Double value) {
            return outOps.createDouble(value);
        }
        if (input instanceof final Number value) {
            return outOps.createNumeric(value);
        }
        return convertMap(outOps, input);
    }

    @Override
    public DataResult<Number> getNumberValue(final Object input) {
        if (input instanceof final Number value) {
            return DataResult.success(value);
        }
        return DataResult.error(() -> "Not a number: " + input);
    }

    @Override
    public Object createNumeric(Number i) {
        return i;
    }

    @Override
    public DataResult<String> getStringValue(final Object input) {
        if (input instanceof final String value) {
            return DataResult.success(value);
        }
        return DataResult.error(() -> "Not a string: " + input);
    }

    @Override
    public Object createString(String value) {
        return value;
    }

    @Override
    public DataResult<Boolean> getBooleanValue(final Object input) {
        if (input instanceof final Boolean value) {
            return DataResult.success(value);
        }
        return DataResult.error(() -> "Not a boolean: " + input);
    }

    @Override
    public Object createBoolean(final boolean value) {
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataResult<Object> mergeToList(Object list, Object value) {
        if (list == null) {
            return DataResult.success(List.of(value));
        } else if (list instanceof Collection col) {
            List<Object> newList = new ArrayList<>(col);
            newList.add(value);
            return DataResult.success(newList);
        }
        return DataResult.error(() -> "Not a list: " + list);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataResult<Object> mergeToList(Object list, List<Object> values) {
        if (list == null) {
            return DataResult.success(values);
        } else if (list instanceof Collection col) {
            List<Object> newList = new ArrayList<>(col);
            newList.addAll(values);
            return DataResult.success(newList);
        }
        return DataResult.error(() -> "Not a list: " + list);
    }

    @Override
    public DataResult<Object> mergeToMap(Object map, Object key, Object value) {
        if (map == null) {
            return DataResult.success(Map.of(key, value));
        } else if (map instanceof Map<?, ?> m) {
            Map<Object, Object> newMap = new HashMap<>(m);
            newMap.put(key, value);
            return DataResult.success(newMap);
        } else if (key instanceof String name) {
            try {
                Field field = getInstanceField(map, name);
                field.setAccessible(true);
                field.set(map, value);
                return DataResult.success(map);
            } catch (Exception e) {
                return DataResult.error(e::getMessage);
            }
        }
        return DataResult.error(() -> "Not a map: " + map);
    }

    @Override
    public DataResult<Object> mergeToMap(Object map, Map<Object, Object> values) {
        if (map instanceof Map<?, ?> m) {
            Map<Object, Object> newMap = new HashMap<>(m);
            newMap.putAll(values);
            return DataResult.success(newMap);
        }
        return DynamicOps.super.mergeToMap(map, values);
    }

    @Override
    public DataResult<Object> mergeToMap(Object map, MapLike<Object> values) {
        if (map instanceof Map<?, ?> m) {
            Map<Object, Object> newMap = new HashMap<>(m);
            values.entries().forEach(p -> newMap.put(p.getFirst(), p.getSecond()));
            return DataResult.success(newMap);
        }
        return DynamicOps.super.mergeToMap(map, values);
    }

    @Override
    public DataResult<Stream<Pair<Object, Object>>> getMapValues(Object input) {
        if (input instanceof Map<?, ?> map) {
            return DataResult.success(map.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue())));
        } else if (input != null) {
            return DataResult.success(getInstanceFields(input)
                .filter(f -> {
                    try {
                        f.setAccessible(true);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(f -> {
                    try {
                        Object obj = f.get(input);
                        Object name = f.getName();
                        return Pair.of(name, obj);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull));
        }
        return DataResult.error(() -> "Value must not be null");
    }

    @Override
    public Object createMap(Stream<Pair<Object, Object>> map) {
        return map.collect(Collectors.toMap(Pair::getFirst, Pair::getSecond));
    }

    @Override
    public DataResult<Stream<Object>> getStream(Object input) {
        if (input instanceof Collection<?> col) {
            return DataResult.success(col.stream().map(o -> o));
        } else if (input instanceof Object[] arr) {
            return DataResult.success(Arrays.stream(arr));
        } else if (input instanceof Map<?, ?> map) {
            return DataResult.success(map.values().stream().map(o -> o));
        }
        return DataResult.error(() -> "Not an list: " + input);
    }

    @Override
    public Object createList(Stream<Object> input) {
        return input.collect(Collectors.toList());
    }

    @Override
    public Object remove(Object input, String key) {
        if (input instanceof Map<?, ?> map) {
            Map<?, ?> modified = new HashMap<>(map);
            modified.remove(key);
            return modified;
        }
        return input;
    }

    @Override
    public DataResult<Object> getGeneric(Object input, Object key) {
        if (input instanceof Map) {
            return DynamicOps.super.getGeneric(input, key);
        } else if (input instanceof List list && key instanceof Integer i) {
            try {
                return DataResult.success(list.get(i));
            } catch (Exception e) {
                return DataResult.error(e::getMessage);
            }
        } else if (key instanceof String name) {
            try {   
                Field field = getInstanceField(input, name);
                field.setAccessible(true);
                return DataResult.success(field.get(input));
            } catch (Exception e) {
                return DataResult.error(e::getMessage);
            }
        }
        return DataResult.error(() -> "Object " + input + " does not have a member " + key);
    }

    public Field getInstanceField(Object instance, String name) throws NoSuchFieldException {
        if (instance == null) throw new NoSuchFieldError("null");
        Class<?> clazz = instance instanceof Class c ? c : instance.getClass();
        Field field = null;
        try {
            field = clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            if (includeSuperclasses && !clazz.equals(Object.class)) {
                return getInstanceField(clazz.getSuperclass(), name);
            }
            throw e;
        }
        if (Modifier.isStatic(field.getModifiers())) throw new NoSuchFieldException("Field must not be static");
        return field;
    }

    public Stream<Field> getInstanceFields(Object instance) {
        if (instance == null) return Stream.empty();
        Class<?> clazz = instance instanceof Class c ? c : instance.getClass();
        Stream.Builder<Stream<Field>> builder = Stream.builder();
        do {
            if (clazz == Object.class) break;
            builder.accept(Arrays.stream(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        } while (includeSuperclasses);
        return builder
            .build()
            .flatMap(s -> s)
            .filter(f -> !Modifier.isStatic(f.getModifiers()));
    }
    
}
