package net.anvilcraft.anvillib.cosmetics;

import java.util.Map.Entry;

import net.anvilcraft.anvillib.event.AddEntityRenderLayersEvent;
import net.anvilcraft.anvillib.event.Bus;
import net.anvilcraft.anvillib.event.IEventBusRegisterable;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;

public class ClientEventHandler implements IEventBusRegisterable {
    private void onAddLayers(AddEntityRenderLayersEvent ev) {
        for (Entry<String, EntityRenderer<? extends PlayerEntity>> skin : ev.skinMap().entrySet())
            if (skin.getValue() instanceof PlayerEntityRenderer render)
                render.addFeature(new CosmeticFeatureRenderer(render, skin.getKey()));
    }

    @Override
    public void registerEventHandlers(Bus bus) {
        bus.register(AddEntityRenderLayersEvent.class, this::onAddLayers);
    }
}
