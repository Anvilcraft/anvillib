package net.anvilcraft.anvillib.resources;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import cpw.mods.fml.common.Loader;
import net.anvilcraft.anvillib.api.inject.Implementation;
import net.anvilcraft.anvillib.api.inject.Inject;
import net.anvilcraft.anvillib.api.resources.IResourceSettingsHandler;

@Implementation(IResourceSettingsHandler.class)
public class ResourceSettingsHandler implements IResourceSettingsHandler {

    @Inject(IResourceSettingsHandler.class)
    public static IResourceSettingsHandler INSTANCE;
    Map<String, Object> resourceSettings = new HashMap<>();
    Map<String, Function<Object, Object>> checkedMappers = new HashMap<>();
    Set<Consumer<Map<String, Object>>> listeners = new HashSet<>();

    @Override
    public Map<String, Object> getResourceSettings() {
        return resourceSettings;
    }

    @Override
    public void registerListener(Consumer<Map<String, Object>> listener) {
        listeners.add(listener);
    }

    @Override
    public void registerCheckedSetting(String id, Function<Object, Object> mapper) {
        if (checkedMappers.containsKey(id)) {
            String modID = Loader.instance().activeModContainer().getModId();
            if (!id.startsWith(modID + ":")) {
                throw new IllegalArgumentException();
            }
        }
        checkedMappers.put(id, mapper);
    }

    @Override
    public Function<Object, Object> getCheckedMapperFor(Class<?> type) {
        return (o) -> {
            if (o == null || !type.isAssignableFrom(o.getClass())) {
                throw new IllegalArgumentException();
            }
            return o;
        };
    }
    
}
