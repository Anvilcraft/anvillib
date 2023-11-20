package net.anvilcraft.anvillib;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;

public class AnvilLibFabric implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        AnvilLib.initialize();
    }

    @Override
    public void onInitializeClient() {
        AnvilLib.initializeClient();
    }
}
