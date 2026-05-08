package net.anvilcraft.anvillib.api.resources;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IResourceSettingsHandler {
    
    Map<String, Object> getResourceSettings();

    void registerListener(Consumer<Map<String, Object>> listener);

    void registerCheckedSetting(String id, Function<Object, Object> mapper);

    Function<Object, Object> getCheckedMapperFor(Class<?> type);

}
