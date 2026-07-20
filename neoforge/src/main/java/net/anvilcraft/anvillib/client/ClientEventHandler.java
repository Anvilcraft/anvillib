package net.anvilcraft.anvillib.client;

import java.util.HashMap;
import java.util.Map;

import net.anvilcraft.anvillib.AnvilLib;
import net.anvilcraft.anvillib.event.AddEntityRenderLayersEvent;
import net.anvilcraft.anvillib.event.Bus;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(
    modid = AnvilLib.MODID, bus = EventBusSubscriber.Bus.MOD, value = { Dist.CLIENT }
)
public class ClientEventHandler {
    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers ev) {
        Map<PlayerSkin.Model, EntityRenderer<? extends Player>> map = new HashMap<>();
        for (PlayerSkin.Model skin : ev.getSkins()) {
            EntityRenderer<?> renderer = ev.getSkin(skin);
            if (renderer != null)
                map.put(skin, (EntityRenderer<? extends Player>) renderer);
        }
        Bus.MAIN.fire(new AddEntityRenderLayersEvent(map));
    }
}
