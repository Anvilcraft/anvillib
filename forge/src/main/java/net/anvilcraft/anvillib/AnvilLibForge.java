package net.anvilcraft.anvillib;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod(AnvilLib.MODID)
public class AnvilLibForge {
    public AnvilLibForge() {
        AnvilLib.initialize();
    }

    @SubscribeEvent
    public void onClientInitialize(FMLClientSetupEvent event) {
        AnvilLib.initializeClient();
    }
}
