package net.anvilcraft.anvillib.cosmetics.remote.thread;

import java.io.IOException;
import java.net.URI;

import net.anvilcraft.anvillib.cosmetics.remote.RemoteCosmeticProvider;
import net.anvilcraft.anvillib.cosmetics.remote.model.PlayerData;

public class PlayerCosmeticLoaderThread extends FileDownloaderThread{
  
    private URI config;
    private RemoteCosmeticProvider provider;

    public PlayerCosmeticLoaderThread(URI config, RemoteCosmeticProvider provider) {
        super("0.2.0");
        this.config = config;
        this.provider = provider;
    }

    @Override
    public void run() {
        try {
            PlayerData player = this.loadJson(config, PlayerData.class);
            if (player == null) return;
            for (String id : player.cosmetics) {
                this.provider.loadCosmetic(id);
                this.provider.playerCosmetics.get(player.uuid).add(id);
            }
            if (player.cape != null) {
                this.provider.loadCape(player.cape);
                this.provider.playerCapes.put(player.uuid, player.cape);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
