package net.anvilcraft.anvillib.cosmetics.remote;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import net.anvilcraft.anvillib.AnvilLib;
import net.anvilcraft.anvillib.cosmetics.ICosmetic;
import net.anvilcraft.anvillib.cosmetics.ICosmeticProvider;
import net.anvilcraft.anvillib.cosmetics.remote.model.CosmeticData;
import net.anvilcraft.anvillib.cosmetics.remote.thread.CosmeticAssetsLoaderThread;
import net.anvilcraft.anvillib.cosmetics.remote.thread.CosmeticLoaderThread;
import net.anvilcraft.anvillib.cosmetics.remote.thread.PlayerCosmeticLoaderThread;
import net.minecraft.util.Util;

public class RemoteCosmeticProvider implements ICosmeticProvider {

    public static RemoteCosmeticProvider INSTANCE = null;

    public final Map<String, RemoteCosmetic> cosmetics = new ConcurrentHashMap<>();
    public final Map<UUID, Set<String>> playerCosmetics = new ConcurrentHashMap<>();
    private final Map<String, Boolean> knownCosmetics = new ConcurrentHashMap<>();
    private boolean dirty = false;

    public final URI playerBase;
    public final URI cosmeticBase;
    private final File cacheDir;

    public RemoteCosmeticProvider(URI playerBase, URI cosmeticBase, File cacheDir) {
        this.playerBase = playerBase;
        this.cosmeticBase = cosmeticBase;
        this.cacheDir = cacheDir;
    }

    @Override
    public boolean requestsRefresh() {
        return this.dirty;
    }

    @Override
    public void addCosmetics(UUID player, Consumer<ICosmetic> cosmeticAdder) {
        this.dirty = false;
        if (playerCosmetics.containsKey(player)) {
            for (String id : playerCosmetics.get(player)) {
                if (!this.cosmetics.containsKey(id) || !this.cosmetics.get(id).readyToRender()) continue;
                cosmeticAdder.accept(this.cosmetics.get(id));
            }
        } else {
            try {
                this.loadNewPlayer(player);
            } catch (MalformedURLException e) {
                AnvilLib.LOGGER.error(e);
            }
        }
    }

    public void markDirty() {
        synchronized(this) {
            this.dirty = true;
        }
    }

    private void loadNewPlayer(UUID id) throws MalformedURLException {
        this.playerCosmetics.putIfAbsent(id, new HashSet<>());
        URI url = playerBase.resolve(id.toString());
        Util.getMainWorkerExecutor().execute(new PlayerCosmeticLoaderThread(url, this));
    }

    public void loadCosmetic(String id) throws MalformedURLException {
        if (this.cosmetics.containsKey(id) || knownCosmetics.containsKey(id)) return;
        this.knownCosmetics.put(id, true);
        URI url = cosmeticBase.resolve(id);
        Util.getMainWorkerExecutor().execute(new CosmeticLoaderThread(url, this));
    }

    public void loadAssets(CosmeticData data, RemoteCosmetic cosmetic) {
        Util.getMainWorkerExecutor().execute(new CosmeticAssetsLoaderThread(cosmetic, data, this.cacheDir, this));
    }

    public void failCosmeticLoading(String id) {
        AnvilLib.LOGGER.error("Cosmetic loading failed: {}", id);
        this.cosmetics.remove(id);
    }
    
}
