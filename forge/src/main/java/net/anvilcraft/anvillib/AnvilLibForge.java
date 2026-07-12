package net.anvilcraft.anvillib;

import net.anvilcraft.anvillib.event.ApplyRecipesEvent;
import net.anvilcraft.anvillib.event.Bus;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SinglePreparationResourceReloader;
import net.minecraft.util.profiler.Profiler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AnvilLib.MODID)
public class AnvilLibForge {
    public AnvilLibForge() {
        AnvilLib.initialize();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.<FMLClientSetupEvent>addListener(alec -> AnvilLib.initializeClient());

        MinecraftForge.EVENT_BUS.<AddReloadListenerEvent>addListener(event -> {
            RecipeManager rm = event.getServerResources().getRecipeManager();
            event.addListener(new SinglePreparationResourceReloader<Void>() {
                @Override
                protected Void prepare(ResourceManager resourceManager, Profiler profiler) {
                    return null;
                }

                @Override
                protected void apply(Void prepared, ResourceManager resourceManager, Profiler profiler) {
                    Bus.MAIN.fire(new ApplyRecipesEvent(rm));
                }
            });
        });
    }
}
