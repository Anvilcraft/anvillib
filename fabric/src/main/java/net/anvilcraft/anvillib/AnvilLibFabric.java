package net.anvilcraft.anvillib;

import net.anvilcraft.anvillib.event.ApplyRecipesEvent;
import net.anvilcraft.anvillib.event.Bus;
import net.anvilcraft.anvillib.structure.StructureRule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;

public class AnvilLibFabric implements ModInitializer, ClientModInitializer {
    @Override
    public void onInitialize() {
        String version = FabricLoader.getInstance()
                .getModContainer("anvillib")
                .map(ModContainer::getMetadata)
                .map(ModMetadata::getVersion)
                .map(Version::getFriendlyString)
                .orElse("0.2.0");
        DynamicRegistries.register(StructureRule.REGISTRY_KEY, StructureRule.CODEC);
        AnvilLib.initialize(version);
        ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, resourceManager, success) -> {
            if (success) Bus.MAIN.fire(new ApplyRecipesEvent(server.getRecipeManager(), server.registryAccess()));
        });
    }

    @Override
    public void onInitializeClient() {
        AnvilLib.initializeClient();
    }
}
