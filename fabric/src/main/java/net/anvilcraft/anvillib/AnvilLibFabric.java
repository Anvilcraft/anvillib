package net.anvilcraft.anvillib;

import net.fabricmc.api.ModInitializer;

public class AnvilLibFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AnvilLib.initialize();
    }
}
