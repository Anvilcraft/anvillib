package net.anvilcraft.anvillib.impl.serialize;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.anvilcraft.anvillib.api.serialize.IObjectManipulator;
import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.api.serialize.IObjectType;
import net.anvilcraft.anvillib.api.types.Pair;
import net.anvilcraft.anvillib.api.types.Result;

public class ReflectedJavaObjectType implements IObjectType<Object> {

    private boolean includeSuperclasses = true;

    private IObjectSerializer<Object, String> strings = new IObjectSerializer<Object,String>() {

        @Override
        public Result<Object, Exception> serialize(String object) {
            return Result.ok(object);
        }

        @Override
        public Result<String, Exception> deserialize(Object encoded) throws IllegalArgumentException {
            if (!canDeserialize(encoded)) return Result.error(new IllegalArgumentException("Not a string: " + encoded));
            return Result.ok((String) encoded);
        }

        @Override
        public boolean canDeserialize(Object encoded) {
            return encoded instanceof String;
        }

    };
    private IObjectSerializer<Object, Boolean> booleans = new IObjectSerializer<Object,Boolean>() {

        @Override
        public Result<Object, Exception> serialize(Boolean object) {
            return Result.ok(object);
        }

        @Override
        public Result<Boolean, Exception> deserialize(Object encoded) throws IllegalArgumentException {
            if (!canDeserialize(encoded)) return Result.error(new IllegalArgumentException("Not a boolean: " + encoded));
            return Result.ok((Boolean) encoded);
        }

        @Override
        public boolean canDeserialize(Object encoded) {
            return encoded instanceof Boolean;
        }
        
    };
    private IObjectSerializer<Object, Long> integers = new IObjectSerializer<Object,Long>() {

        @Override
        public Result<Object, Exception> serialize(Long object) {
            return Result.ok(object);
        }

        @Override
        public Result<Long, Exception> deserialize(Object encoded) throws IllegalArgumentException {
            if (!canDeserialize(encoded)) return Result.error(new IllegalArgumentException("Not an integer: " + encoded));
            return Result.ok(((Number) encoded).longValue());
        }

        @Override
        public boolean canDeserialize(Object encoded) {
            return encoded instanceof Number && ((Number) encoded).doubleValue() % 1 == 0;
        }
        
    };
    private IObjectSerializer<Object, Double> floats = new IObjectSerializer<Object,Double>() {

        @Override
        public Result<Object, Exception> serialize(Double object) {
            return Result.ok(object);
        }

        @Override
        public Result<Double, Exception> deserialize(Object encoded) throws IllegalArgumentException {
            if (!canDeserialize(encoded)) return Result.error(new IllegalArgumentException("Not a number: " + encoded));
            return Result.ok(((Number) encoded).doubleValue());
        }

        @Override
        public boolean canDeserialize(Object encoded) {
            return encoded instanceof Number;
        }
        
    };

    private IObjectManipulator<Object, Integer> arrays = new IObjectManipulator<Object,Integer>() {

        @Override
        public Object get(Object object, Integer index) throws IllegalArgumentException, IndexOutOfBoundsException {
            if (object instanceof List) {
                return ((List<?>) object).get(index);
            } else if (isArray(object)) {
                return Array.get(object, index);
            }
            throw new IllegalArgumentException("Not an array: " + object);
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object set(Object object, Integer index, Object value) throws IllegalArgumentException {
            if (object instanceof List) {
                try {
                    List<Object> list = (List<Object>) object;
                    list.set(index, value);
                    return list;
                } catch (Exception e) {
                    List<Object> list = new ArrayList<>((List<?>) object);
                    list.set(index, value);
                    return list;
                }
            } else if (isArray(object)) {
                Array.set(object, index, value);
            }
            throw new IllegalArgumentException("Not an array: " + object);
        }

        @Override
        public int getSize(Object object) {
            if (object instanceof List) {
                return ((List<?>) object).size();
            } else if (isArray(object)) {
                return Array.getLength(object);
            }
            return 0;
        }

        @Override
        public Collection<Integer> getIndices(Object object) {
            return IntStream.range(0, getSize(object)).mapToObj(i -> (Integer) i).collect(Collectors.toList());
        }

        @Override
        public Object create() {
            return new ArrayList<>();
        }

        @Override
        public boolean isInstance(Object object) {
            return object instanceof List || this.isArray(object);
        }

        private boolean isArray(Object object) {
            return object != null && object.getClass().isArray();
        }

        @Override
        public Stream<Entry<Integer, Object>> getValues(Object object) {
            return getIndices(object).stream().map(i -> new Pair<>(i, get(object, i)));
        }
        
    };

    private IObjectManipulator<Object, String> objects = new IObjectManipulator<Object,String>() {

        @Override
        public Object get(Object object, String index) throws IllegalArgumentException, IndexOutOfBoundsException {
            if (object instanceof Map) {
                return ((Map<?, ?>) object).get(index);
            }
            try {   
                Field field = getInstanceField(object, index);
                field.setAccessible(true);
                return field.get(object);
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object set(Object object, String index, Object value) throws IllegalArgumentException {
            if (object instanceof Map) {
                try {
                    Map<String, Object> map = (Map<String, Object>) object;
                    map.put(index, value);
                    return map;
                } catch (Exception e) {
                    Map<String, Object> map = new HashMap<>();
                    ((Map<?, ?>) object).forEach((k, v) -> map.put(k.toString(), v));
                    map.put(index, value);
                    return map;
                }
            }
            try {   
                Field field = getInstanceField(object, index);
                field.setAccessible(true);
                field.set(object, value);
                return object;
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public int getSize(Object object) {
            if (object instanceof Map) {
                return (int) ((Map<?, ?>) object).keySet().stream().filter(k -> k instanceof String).count();
            }
            try {
                return (int) getInstanceFields(object).count();
            } catch (Exception e) {
                return 0;
            }
        }

        @Override
        public Collection<String> getIndices(Object object) {
            if (object instanceof Map) {
                return ((Map<?, ?>) object).keySet().stream().filter(k -> k instanceof String).map(k -> (String) k).collect(Collectors.toSet());
            }
            try {
                return getInstanceFields(object).map(f -> f.getName()).collect(Collectors.toSet());
            } catch (Exception e) {
                return Collections.emptySet();
            }
        }

        @Override
        public Object create() {
            return new HashMap<String, Object>();
        }

        @Override
        public boolean isInstance(Object object) {
            return true;
        }

        @SuppressWarnings("unchecked")
        public Stream<Map.Entry<String,Object>> getValues(Object object) {
            if (object instanceof Map) {
                return ((Map<?, ?>) object).entrySet().stream().filter(e -> e.getKey() instanceof String).map(e -> (Entry<String, Object>) e);
            }
            return getInstanceFields(object)
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
                        Object obj = f.get(object);
                        String name = f.getName();
                        return Pair.of(name, obj);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .map(v -> (Entry<String, Object>) v);
        };
        
    };

    private IObjectManipulator<Object, Object> collections = new IObjectManipulator<Object,Object>() {

        @Override
        public Object get(Object object, Object index) throws IllegalArgumentException, IndexOutOfBoundsException {
            if (object instanceof Collection && ((Collection<?>)object).contains(index)) {
                return index;
            }
            throw new IndexOutOfBoundsException();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object set(Object object, Object index, Object value) throws IllegalArgumentException {
            if (!(object instanceof Collection)) throw new IllegalArgumentException("Not a collection: " + object);
            if (index != value) throw new IllegalArgumentException("Index must be queal to value");
            ((Collection<Object>) object).add(value);
            return object;
        }

        @Override
        public int getSize(Object object) {
            if (object instanceof Collection) {
                return ((Collection<?>) object).size();
            }
            return 0;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Collection<Object> getIndices(Object object) {
            if (object instanceof Collection) {
                return ((Collection<Object>) object);
            }
            return Collections.emptySet();
        }

        @Override
        public Object create() {
            return new HashSet<>();
        }

        @Override
        public boolean isInstance(Object object) {
            return object instanceof Collection;
        }

        public Stream<Map.Entry<Object,Object>> getValues(Object object) {
            if (object instanceof Collection) {
                return ((Collection<?>) object).stream().map(e -> new Pair<>(e, e));
            }
            return Stream.empty();
        };

        public boolean contains(Object object, Object value) {
            if (object instanceof Collection) {
                return ((Collection<?>) object).contains(value);
            }
            return false;
        };
        
    };

    @Override
    public IObjectSerializer<Object, String> strings() {
        return this.strings;
    }

    @Override
    public IObjectSerializer<Object, Boolean> booleans() {
        return this.booleans;
    }

    @Override
    public IObjectSerializer<Object, Long> integers() {
        return this.integers;
    }

    @Override
    public IObjectSerializer<Object, Double> floats() {
        return this.floats;
    }

    @Override
    public IObjectManipulator<Object, Integer> arrays() {
        return this.arrays;
    }

    @Override
    public IObjectManipulator<Object, String> objects() {
        return this.objects;
    }

    @Override
    public IObjectManipulator<Object, Object> collections() {
        return this.collections;
    }

    @Override
    public Object createNull() {
        return null;
    }

    @Override
    public boolean isNull(Object object) {
        return object == null;
    }

    public Field getInstanceField(Object instance, String name) throws NoSuchFieldException {
        if (instance == null) throw new NoSuchFieldError("null");
        Class<?> clazz = instance instanceof Class ? (Class<?>) instance : instance.getClass();
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
        Class<?> clazz = instance instanceof Class ? (Class<?>) instance : instance.getClass();
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
