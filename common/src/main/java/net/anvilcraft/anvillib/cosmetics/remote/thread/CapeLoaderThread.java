package net.anvilcraft.anvillib.cosmetics.remote.thread;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import com.google.common.hash.Hashing;

import net.anvilcraft.anvillib.AnvilLib;
import net.anvilcraft.anvillib.cosmetics.remote.RemoteCosmeticProvider;
import net.anvilcraft.anvillib.cosmetics.remote.model.CapeData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class CapeLoaderThread extends AbstractFileDownloaderThread {
    private String id;
    private File cacheDir;
    private URI url;
    private RemoteCosmeticProvider provider;
    private TextureManager textureManager
        = Minecraft.getInstance().getTextureManager();

    public CapeLoaderThread(
        String id, URI url, File cacheDir, RemoteCosmeticProvider provider
    ) {
        super(AnvilLib.VERSION);
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
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath("anvillib", "textures/cape/" + data.id);
        String hash = Hashing.sha1().hashUnencodedChars(data.id).toString();
        AbstractTexture texture = this.textureManager.getTexture(location);
        if (texture instanceof SimpleTexture) {
            File file = new File(
                this.cacheDir, hash.length() > 2 ? hash.substring(0, 2) : "xx"
            );
            File file2 = new File(file, hash);
            texture = new HttpTexture(
                file2,
                data.url,
                ResourceLocation.withDefaultNamespace("textures/block/dirt"),
                false,
                null
            );
            this.textureManager.register(location, texture);
        }
        this.provider.capes.put(data.id, location);
        this.provider.markDirty();
    }
}
