package net.anvilcraft.anvillib.api.types;

import java.util.function.Consumer;

public interface IMutableReference<T> extends IReference<T> {

    void set(T value);

    void notify(Consumer<T> hook);

}
