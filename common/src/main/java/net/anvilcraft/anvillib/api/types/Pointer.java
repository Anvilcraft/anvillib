package net.anvilcraft.anvillib.api.types;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class Pointer<T> implements IMutableReference<T> {

    private T value;
    private Set<Consumer<T>> hooks = new HashSet<>();

    public Pointer() {
        value = null;
    }

    public Pointer(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
        hooks.forEach((hook) -> hook.accept(value));
    }

    @Override
    public void notify(Consumer<T> hook) {
        hooks.add(hook);
    }

}