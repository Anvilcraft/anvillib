package net.anvilcraft.anvillib.client;

import net.anvilcraft.anvillib.AnvilLib;
import net.anvilcraft.anvillib.event.AddEntityRenderLayersEvent;
import net.anvilcraft.anvillib.event.Bus;
import net.anvilcraft.anvillib.mixin.forge.accessor.AddLayersAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(
    modid = AnvilLib.MODID, bus = EventBusSubscriber.Bus.MOD, value = { Dist.CLIENT }
)
public class ClientEventHandler {
    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers ev) {
        Bus.MAIN.fire(new AddEntityRenderLayersEvent(((AddLayersAccessor) ev).getSkinMap()
        ));
    }
}
