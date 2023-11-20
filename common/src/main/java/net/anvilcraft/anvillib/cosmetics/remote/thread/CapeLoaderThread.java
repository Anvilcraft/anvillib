package net.anvilcraft.anvillib.cosmetics.remote.thread;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import com.google.common.hash.Hashing;

import net.anvilcraft.anvillib.AnvilLib;
import net.anvilcraft.anvillib.cosmetics.remote.RemoteCosmeticProvider;
import net.anvilcraft.anvillib.cosmetics.remote.model.CapeData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

public class CapeLoaderThread extends FileDownloaderThread {

    private String id;
    private File cacheDir;
    private URI url;
    private RemoteCosmeticProvider provider;
    private TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();

    public CapeLoaderThread(String id, URI url, File cacheDir, RemoteCosmeticProvider provider) {
        super("0.2.0");
        this.id = id;
        this.url = url;
        this.cacheDir = cacheDir;
        this.provider = provider;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void run() {
        CapeData data = null;
        try {
            data = this.loadJson(url, CapeData.class);
        } catch (IOException e) {
            AnvilLib.LOGGER.error("Can't load cape: {}", id, e);
            return;
        }
        Identifier location = new Identifier("anvillib", "textures/cape/"+data.id);
        String hash = Hashing.sha1().hashUnencodedChars(data.id).toString();
        AbstractTexture texture = this.textureManager.getOrDefault(location, MissingSprite.getMissingSpriteTexture());
        if (texture == MissingSprite.getMissingSpriteTexture()) {
            File file = new File(this.cacheDir, hash.length() > 2 ? hash.substring(0, 2) : "xx");
            File file2 = new File(file, hash);
            texture = new PlayerSkinTexture(file2, data.url, new Identifier("textures/block/dirt.png"), false, null);
            this.textureManager.registerTexture(location, texture);
        }
        this.provider.capes.put(data.id, location);
        this.provider.markDirty();
    }
    
}
