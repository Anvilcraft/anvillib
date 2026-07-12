package net.anvilcraft.anvillib;

import net.anvilcraft.anvillib.event.ApplyRecipesEvent;
import net.anvilcraft.anvillib.event.Bus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class AnvilLibFabric implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        AnvilLib.initialize();
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) Bus.MAIN.fire(new ApplyRecipesEvent(server.getRecipeManager()));
        });
    }

    @Override
    public void onInitializeClient() {
        AnvilLib.initializeClient();
    }
}
