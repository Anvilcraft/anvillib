package net.anvilcraft.anvillib;

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
    }
}
