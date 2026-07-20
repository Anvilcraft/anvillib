package net.anvilcraft.anvillib;

import net.anvilcraft.anvillib.event.ApplyRecipesEvent;
import net.anvilcraft.anvillib.event.Bus;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import org.apache.maven.artifact.versioning.ArtifactVersion;

@Mod(AnvilLib.MODID)
public class AnvilLibNeoForge {
    public AnvilLibNeoForge(IEventBus modEventBus) {
        String version = ModList.get()
                .getModContainerById("anvillib")
                .map(ModContainer::getModInfo)
                .map(IModInfo::getVersion)
                .map(ArtifactVersion::toString)
                .orElse("0.2.0");
        AnvilLib.initialize(version);
        modEventBus.addListener(this::onClientSetup);
        NeoForge.EVENT_BUS.addListener(this::onAddReloadListeners);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        AnvilLib.initializeClient();
    }

    private void onAddReloadListeners(AddReloadListenerEvent event) {
        RecipeManager rm = event.getServerResources().getRecipeManager();
        event.addListener(new SimplePreparableReloadListener<Void>() {
            @Override
            protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
                return null;
            }

            @Override
            protected void apply(
                Void prepared, ResourceManager resourceManager, ProfilerFiller profiler
            ) {
                Bus.MAIN.fire(new ApplyRecipesEvent(rm));
            }
        });
    }
}
