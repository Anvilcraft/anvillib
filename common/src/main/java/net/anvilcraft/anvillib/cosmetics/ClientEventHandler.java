package net.anvilcraft.anvillib.cosmetics;

import java.io.File;
import java.net.URI;
import java.util.Objects;
import java.util.Map.Entry;

import net.anvilcraft.anvillib.AnvilLib;
import net.anvilcraft.anvillib.Compat;
import net.anvilcraft.anvillib.cosmetics.remote.RemoteCosmeticProvider;
import net.anvilcraft.anvillib.event.AddEntityRenderLayersEvent;
import net.anvilcraft.anvillib.event.Bus;
import net.anvilcraft.anvillib.event.IEventBusRegisterable;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.player.Player;

public class ClientEventHandler implements IEventBusRegisterable {
    private void onAddLayers(AddEntityRenderLayersEvent ev) {
        for (Entry<PlayerSkin.Model, EntityRenderer<? extends Player>> entry :
             ev.skinMap().entrySet())
            if (entry.getValue() instanceof PlayerRenderer render)
                render.addLayer(new CosmeticFeatureRenderer(render));
    }

    public static void registerRemoteCosmetics(File assetsCache) {
        File cacheDir = new File(Objects.requireNonNull(assetsCache), "anvillib");
        try {
            URI playerBase = new URI("https://api.tilera.xyz/anvillib/data/players/");
            URI cosmeticBase = new URI("https://api.tilera.xyz/anvillib/data/cosmetics/");
            URI capeBase = new URI("https://api.tilera.xyz/anvillib/data/capes/");
            CosmeticsManager.registerProvider(
                new RemoteCosmeticProvider(playerBase, cosmeticBase, capeBase, cacheDir)
            );
        } catch (Exception e) {
            AnvilLib.LOGGER.error(e);
        }
    }

    @Override
    public void registerEventHandlers(Bus bus) {
        if (Compat.hasGeckolib()) {
            bus.register(AddEntityRenderLayersEvent.class, this::onAddLayers);
        }
    }
}
