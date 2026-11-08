package net.anvilcraft.anvillib.impl.serialize;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.anvilcraft.anvillib.api.inject.Implementation;
import net.anvilcraft.anvillib.api.serialize.IObjectManipulator;
import net.anvilcraft.anvillib.api.serialize.IObjectSerializer;
import net.anvilcraft.anvillib.api.serialize.IObjectType;
import net.anvilcraft.anvillib.api.types.Result;
import net.anvilcraft.anvillib.api.types.Pair;

@Implementation(value = IObjectType.class, id = "gson")
public class GsonManipulator implements IObjectType<JsonElement> {

    private GsonManipulator() {
        
    }

    private final IObjectSerializer<JsonElement, String> stringSerializer = new IObjectSerializer<JsonElement, String>() {

        @Override
        public Result<JsonElement, Exception> serialize(String object) {
            return Result.ok(new JsonPrimitive(object));
        }

        @Override
        public Result<String, Exception> deserialize(JsonElement encoded) throws IllegalArgumentException {
            return Result.create(encoded, JsonElement::getAsString);
        }

        @Override
        public boolean canDeserialize(JsonElement encoded) {
            return encoded != null && encoded.isJsonPrimitive() && encoded.getAsJsonPrimitive().isString();
        }
        
    };
    private final IObjectSerializer<JsonElement, Boolean> booleanSerializer = new IObjectSerializer<JsonElement, Boolean>() {

        @Override
        public Result<JsonElement, Exception> serialize(Boolean object) {
            return Result.ok(new JsonPrimitive(object));
        }

        @Override
        public Result<Boolean, Exception> deserialize(JsonElement encoded) throws IllegalArgumentException {
            return Result.create(encoded, JsonElement::getAsBoolean);
        }

        @Override
        public boolean canDeserialize(JsonElement encoded) {
            return encoded != null && encoded.isJsonPrimitive() && encoded.getAsJsonPrimitive().isBoolean();
        }
        
    };
    private final IObjectSerializer<JsonElement, Long> integerSerializer = new IObjectSerializer<JsonElement, Long>() {

        @Override
        public Result<JsonElement, Exception> serialize(Long object) {
            return Result.ok(new JsonPrimitive(object));
        }

        @Override
        public Result<Long, Exception> deserialize(JsonElement encoded) throws IllegalArgumentException {
            return Result.create(encoded, JsonElement::getAsLong);
        }

        @Override
        public boolean canDeserialize(JsonElement encoded) {
            return encoded != null && encoded.isJsonPrimitive() && encoded.getAsJsonPrimitive().isNumber();
        }
        
    };
    private final IObjectSerializer<JsonElement, Double> floatSerializer = new IObjectSerializer<JsonElement, Double>() {

        @Override
        public Result<JsonElement, Exception> serialize(Double object) {
            return Result.ok(new JsonPrimitive(object));
        }

        @Override
        public Result<Double, Exception> deserialize(JsonElement encoded) throws IllegalArgumentException {
            return Result.create(encoded, JsonElement::getAsDouble);
        }

        @Override
        public boolean canDeserialize(JsonElement encoded) {
            return encoded != null && encoded.isJsonPrimitive() && encoded.getAsJsonPrimitive().isNumber();
        }
        
    };
    private final IObjectManipulator<JsonElement, Integer> arrayManipulator = new IObjectManipulator<JsonElement, Integer>() {

        @Override
        public JsonElement get(JsonElement object, Integer index) throws IllegalArgumentException, IndexOutOfBoundsException {
            try {
                return object.getAsJsonArray().get(index);
            } catch(IllegalStateException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public JsonElement set(JsonElement object, Integer index, JsonElement value) throws IllegalArgumentException {
            int size = getSize(object);
            if (index == size) {
                try {
                    object.getAsJsonArray().add(value);
                    return object;
                } catch(Exception e) {
                    throw new IllegalArgumentException(e);
                }
            } else if (index < size) {
                try {
                    Field field = JsonArray.class.getDeclaredField("elements");
                    field.setAccessible(true);
                    List<JsonElement> list = (List<JsonElement>) field.get(object);
                    list.set(size, value);
                    return object;
                } catch (Exception e) {
                    throw new IllegalArgumentException(e);
                }
            } else {
                throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public int getSize(JsonElement object) {
            try {
                return object.getAsJsonArray().size();
            } catch(Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public Collection<Integer> getIndices(JsonElement object) {
            return IntStream.range(0, getSize(object)).boxed().collect(Collectors.toList());
        }

        @Override
        public JsonElement create() {
            return new JsonArray();
        }

        @Override
        public boolean isInstance(JsonElement object) {
            return object != null && object.isJsonArray();
        }

        @Override
        public Stream<Entry<Integer, JsonElement>> getValues(JsonElement object) {
            try {
                return getIndices(object).stream().map(i -> new Pair<>(i, get(object, i)));
            } catch(Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        
    };
    private final IObjectManipulator<JsonElement, String> objectManipulator = new IObjectManipulator<JsonElement, String>() {

        @Override
        public JsonElement get(JsonElement object, String index) throws IllegalArgumentException, IndexOutOfBoundsException {
            try {
                return object.getAsJsonObject().get(index);
            } catch(Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public JsonElement set(JsonElement object, String index, JsonElement value) throws IllegalArgumentException {
            try {
                object.getAsJsonObject().add(index, value);
                return object;
            } catch(Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public int getSize(JsonElement object) {
            try {
                return object.getAsJsonObject().entrySet().size();
            } catch(Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public Collection<String> getIndices(JsonElement object) {
            try {
                return object.getAsJsonObject().entrySet().stream().map((e) -> e.getKey()).collect(Collectors.toList());
            } catch(Exception e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public JsonElement create() {
            return new JsonObject();
        }

        @Override
        public boolean isInstance(JsonElement object) {
            return object != null && object.isJsonObject();
        }

        @Override
        public Stream<Entry<String, JsonElement>> getValues(JsonElement object) {
            try {
                return object.getAsJsonObject().entrySet().stream();
            } catch(Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        
    };

    private final IObjectManipulator<JsonElement, JsonElement> collectionManipulator = new IObjectManipulator<JsonElement,JsonElement>() {

        @Override
        public JsonElement get(JsonElement object, JsonElement index) throws IllegalArgumentException, IndexOutOfBoundsException {
            return getValues(object)
                .map(e -> e.getKey())
                .filter(Objects::nonNull)
                .filter(v -> v.equals(index))
                .findFirst()
                .orElseThrow(IndexOutOfBoundsException::new);
        }

        @Override
        public JsonElement set(JsonElement object, JsonElement index, JsonElement value) throws IllegalArgumentException {
            if (contains(object, value)) return object;
            return arrayManipulator.set(object, getSize(object), value);
        }

        @Override
        public int getSize(JsonElement object) {
            return arrayManipulator.getSize(object);
        }

        @Override
        public Collection<JsonElement> getIndices(JsonElement object) {
            return arrayManipulator.getValues(object).map(e -> e.getValue()).collect(Collectors.toSet());
        }

        @Override
        public JsonElement create() {
            return arrayManipulator.create();
        }

        @Override
        public boolean isInstance(JsonElement object) {
            return arrayManipulator.isInstance(object);
        }

        @Override
        public Stream<Entry<JsonElement, JsonElement>> getValues(JsonElement object) {
            return arrayManipulator.getValues(object).map(e -> new Pair<>(e.getValue(), e.getValue()));
        }

        
        
    };

    @Override
    public JsonElement createNull() {
        return JsonNull.INSTANCE;
    }

    @Override
    public boolean isNull(JsonElement object) {
        return object == null || object.isJsonNull();
    }

    @Override
    public IObjectSerializer<JsonElement, String> strings() {
        return this.stringSerializer;
    }

    @Override
    public IObjectSerializer<JsonElement, Boolean> booleans() {
        return this.booleanSerializer;
    }

    @Override
    public IObjectSerializer<JsonElement, Long> integers() {
        return this.integerSerializer;
    }

    @Override
    public IObjectSerializer<JsonElement, Double> floats() {
        return this.floatSerializer;
    }

    @Override
    public IObjectManipulator<JsonElement, Integer> arrays() {
        return this.arrayManipulator;
    }

    @Override
    public IObjectManipulator<JsonElement, String> objects() {
        return this.objectManipulator;
    }

    @Override
    public IObjectManipulator<JsonElement, JsonElement> collections() {
        return this.collectionManipulator;
    }
    
}
