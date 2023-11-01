package net.anvilcraft.anvillib.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class Bus {
    public static final Bus MAIN = new Bus();

    private final Map<Class<?>, List<Consumer<?>>> handlerMap = new HashMap<>();

    public void register(IEventBusRegisterable obj) {
        obj.registerEventHandlers(this);
    }

    public <T> void register(Class<T> clazz, Consumer<T> handler) {
        handlerMap.computeIfAbsent(clazz, alec -> new ArrayList<>()).add(handler);
    }

    @SuppressWarnings("unchecked")
    public <T> void fire(T ev) {
        var clazz = ev.getClass();
        if (handlerMap.containsKey(clazz)) {
            for (Consumer<?> handler : handlerMap.get(clazz)) {
                ((Consumer<T>) handler).accept(ev);
            }
        }
    }
}
