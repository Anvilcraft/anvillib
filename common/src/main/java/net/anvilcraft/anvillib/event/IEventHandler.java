package net.anvilcraft.anvillib.event;

import java.util.function.Consumer;

@FunctionalInterface
public interface IEventHandler<T> extends Comparable<IEventHandler<T>>, Consumer<T> {
    
    default int order() {
        return 0;
    }

    @Override
    default int compareTo(IEventHandler<T> o) {
        return this.order() - o.order();
    }

}
