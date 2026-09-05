package net.anvilcraft.anvillib.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bus {
    public static final Bus MAIN = new Bus();

    private final Map<Class<?>, List<IEventHandler<?>>> handlerMap = new HashMap<>();

    public void register(IEventBusRegisterable obj) {
        obj.registerEventHandlers(this);
    }

    public <T> void register(Class<T> clazz, IEventHandler<T> handler) {
        handlerMap.computeIfAbsent(clazz, alec -> new ArrayList<>()).add(handler);
        handlerMap.get(clazz).sort(null);
    }

    @SuppressWarnings("unchecked")
    public <T> void fire(T ev) {
        Class<?> clazz = ev.getClass();
        if (handlerMap.containsKey(clazz)) {
            for (IEventHandler<?> handler : handlerMap.get(clazz)) {
                ((IEventHandler<T>) handler).accept(ev);
            }
        }
    }
}