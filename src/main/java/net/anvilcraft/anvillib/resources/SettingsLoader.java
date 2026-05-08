package net.anvilcraft.anvillib.resources;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import net.anvilcraft.alec.jalec.factories.AlecUnexpectedRuntimeErrorExceptionFactory;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

public class SettingsLoader implements IResourceManagerReloadListener {

    Gson gson = new GsonBuilder().create();

    @SuppressWarnings({"unchecked", "ALEC"})
    @Override
    public void onResourceManagerReload(IResourceManager rm) {
        try {
            ResourceSettingsHandler rsh = (ResourceSettingsHandler) ResourceSettingsHandler.INSTANCE;
            List<IResource> resources = rm.getAllResources(new ResourceLocation("anvillib", "settings.json"));
            rsh.resourceSettings.clear();
            for (IResource res : resources) {
                InputStreamReader reader = new InputStreamReader(res.getInputStream());
                JsonObject obj = gson.fromJson(reader, JsonObject.class);
                reader.close();
                for (Entry<String, JsonElement> e : obj.entrySet()) {
                    if (!e.getValue().isJsonPrimitive()) continue;
                    JsonPrimitive jp = e.getValue().getAsJsonPrimitive();

                    Object o = null;
                    if (jp.isBoolean()) {
                        o = jp.getAsBoolean();
                    } else if (jp.isString()) {
                        o = jp.getAsString();
                    } else if (jp.isNumber()) {
                        o = jp.getAsNumber();
                    }
                    
                    if (rsh.checkedMappers.containsKey(e.getKey())) {
                        try {
                            o = rsh.checkedMappers.get(e.getKey()).apply(o);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            continue;
                        }
                    }

                    rsh.resourceSettings.put(e.getKey(), o);
                }
            }

            rsh.listeners.forEach((l) -> l.accept(rsh.resourceSettings));
        } catch (Exception e) {
            throw AlecUnexpectedRuntimeErrorExceptionFactory.PLAIN.createAlecExceptionWithCause(e, new Object[0]);
        }
    }
    
}
