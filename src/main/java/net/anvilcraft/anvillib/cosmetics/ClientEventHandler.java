package net.anvilcraft.anvillib.cosmetics;

import net.anvilcraft.anvillib.AnvilLib;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = AnvilLib.MODID, bus = Bus.MOD, value = { Dist.CLIENT })
public class ClientEventHandler {
    @SubscribeEvent
    public static void clientSetup(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins())
            if (event.getSkin(skin) instanceof PlayerEntityRenderer render)
                render.addFeature(new CosmeticFeatureRenderer(render, skin));
    }
}
