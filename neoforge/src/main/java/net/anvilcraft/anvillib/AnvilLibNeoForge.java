package net.anvilcraft.anvillib;

import net.anvilcraft.anvillib.event.AddModResourcepacksEvent;
import net.anvilcraft.anvillib.event.ApplyRecipesEvent;
import net.anvilcraft.anvillib.event.Bus;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
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
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforgespi.language.IModInfo;
import net.neoforged.neoforgespi.locating.IModFile;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import java.nio.file.Path;
import java.util.Optional;

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
        modEventBus.addListener(AnvilLibNeoForge::onAddPackFinders);
        NeoForge.EVENT_BUS.addListener(this::onAddReloadListeners);
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        AnvilLib.initializeClient();
    }

    private static void onAddPackFinders(AddPackFindersEvent event) {
        PackType packType = event.getPackType();
        var addEvent = new AddModResourcepacksEvent(packType);
        Bus.MAIN.fire(addEvent);

        for (AddModResourcepacksEvent.Entry entry : addEvent.getEntries()) {
            var modFileInfo = ModList.get().getModFileById(entry.modId());
            if (modFileInfo == null) {
                AnvilLib.LOGGER.error("mod '{}' not found, resourcepack '{}' will not be loaded", entry.modId(), entry.packId());
                continue;
            }
            IModFile modFile = modFileInfo.getFile();

            event.addRepositorySource(consumer -> {
                Path packPath = modFile.findResource("resourcepacks", entry.packId());
                PackLocationInfo info = new PackLocationInfo(
                    entry.modId() + ":" + entry.packId(),
                    Component.literal(entry.packId()),
                    PackSource.BUILT_IN,
                    Optional.empty()
                );
                Pack.ResourcesSupplier supplier = new Pack.ResourcesSupplier() {
                    @Override
                    public net.minecraft.server.packs.PackResources openPrimary(PackLocationInfo i) {
                        return new PathPackResources(i, packPath);
                    }

                    @Override
                    public net.minecraft.server.packs.PackResources openFull(
                        PackLocationInfo i, Pack.Metadata metadata
                    ) {
                        return this.openPrimary(i);
                    }
                };
                Pack p = Pack.readMetaAndCreate(info, supplier, packType, new PackSelectionConfig(true, Pack.Position.TOP, true));
                if (p != null) consumer.accept(p);
            });
        }
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
