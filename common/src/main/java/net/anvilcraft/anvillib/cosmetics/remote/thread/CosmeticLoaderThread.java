package net.anvilcraft.anvillib.cosmetics.remote.thread;

import java.io.IOException;
import java.net.URI;

import net.anvilcraft.anvillib.AnvilLib;
import net.anvilcraft.anvillib.cosmetics.remote.RemoteCosmetic;
import net.anvilcraft.anvillib.cosmetics.remote.RemoteCosmeticProvider;
import net.anvilcraft.anvillib.cosmetics.remote.model.CosmeticData;

public class CosmeticLoaderThread extends AbstractFileDownloaderThread {
    private URI url;
    private RemoteCosmeticProvider provider;

    public CosmeticLoaderThread(URI url, RemoteCosmeticProvider provider) {
        super("0.2.0");
        this.url = url;
        this.provider = provider;
    }

    @Override
    public void run() {
        try {
            CosmeticData data = this.loadJson(url, CosmeticData.class);
            if (data == null)
                throw new IOException("Cosmetic not found");
            RemoteCosmetic cosmetic = new RemoteCosmetic(data.id);
            this.provider.cosmetics.put(data.id, cosmetic);
            this.provider.loadAssets(data, cosmetic);
        } catch (IOException e) {
            AnvilLib.LOGGER.error("Can't load cosmetic", e);
        }
    }
}
