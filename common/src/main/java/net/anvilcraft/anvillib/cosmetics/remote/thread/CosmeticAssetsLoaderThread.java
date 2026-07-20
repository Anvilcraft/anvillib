package net.anvilcraft.anvillib.cosmetics.remote.thread;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import com.google.common.hash.Hashing;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.anvilcraft.anvillib.AnvilLib;
import net.anvilcraft.anvillib.cosmetics.remote.RemoteCosmetic;
import net.anvilcraft.anvillib.cosmetics.remote.RemoteCosmeticProvider;
import net.anvilcraft.anvillib.cosmetics.remote.model.AnimationData;
import net.anvilcraft.anvillib.cosmetics.remote.model.CosmeticData;
import net.anvilcraft.anvillib.cosmetics.remote.model.TextureData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.loading.json.raw.Model;
import software.bernie.geckolib.loading.json.typeadapter.KeyFramesAdapter;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.loading.object.GeometryTree;

public class CosmeticAssetsLoaderThread extends AbstractFileDownloaderThread {
    private RemoteCosmetic cosmetic;
    private CosmeticData data;
    private TextureManager textureManager
        = Minecraft.getInstance().getTextureManager();
    private File cacheDir;
    private RemoteCosmeticProvider provider;

    public CosmeticAssetsLoaderThread(
        RemoteCosmetic cosmetic,
        CosmeticData data,
        File cacheDir,
        RemoteCosmeticProvider provider
    ) {
        super(AnvilLib.VERSION);
        this.cosmetic = cosmetic;
        this.data = data;
        this.cacheDir = cacheDir;
        this.provider = provider;
    }

    @Override
    public void run() {
        this.loadModel(this.data.modelUrl);
        this.loadTexture(this.data.textureData);
        this.loadAnimations(this.data.animationData);
        this.provider.markDirty();
    }

    private void loadAnimations(AnimationData anim) {
        if (anim == null) {
            this.cosmetic.loadAnimations(null, null);
            return;
        }
        BakedAnimations animations = null;
        try {
            URI url = new URI(this.data.animationData.url);
            JsonObject data = this.loadJson(url, JsonObject.class);
            animations = this.buildAnimationFile(data);
        } catch (IOException | URISyntaxException | NullPointerException e) {
            AnvilLib.LOGGER.error(
                "Could not load animation: {}", this.data.animationData.url, e
            );
        }
        this.cosmetic.loadAnimations(animations, anim);
    }

    @SuppressWarnings("deprecation")
    private void loadTexture(TextureData data) {
        String hash = Hashing.sha1().hashUnencodedChars(this.data.id).toString();
        AbstractTexture texture = this.textureManager.getTexture(
            this.cosmetic.getTextureLocation()
        );
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
            this.textureManager.register(this.cosmetic.getTextureLocation(), texture);
        }
        this.cosmetic.loadTexture(data);
    }

    private void loadModel(String url) {
        try {
            URI uri = new URI(url);
            String json = Objects.requireNonNull(this.getStringForURL(uri));
            BakedGeoModel model = this.buildModel(json);
            this.cosmetic.loadModel(model);
        } catch (NullPointerException | URISyntaxException e) {
            AnvilLib.LOGGER.error("Can't load remote model: {}", url, e);
            this.handleFailure();
        }
    }

    private void handleFailure() {
        this.provider.failCosmeticLoading(this.data.id);
    }

    private BakedAnimations buildAnimationFile(JsonObject json) {
        if (!json.has("animations")) return new BakedAnimations(java.util.Map.of());
        return KeyFramesAdapter.GEO_GSON.fromJson(json.getAsJsonObject("animations"), BakedAnimations.class);
    }

    private BakedGeoModel buildModel(String json) {
        Model rawModel = KeyFramesAdapter.GEO_GSON.fromJson(json, Model.class);
        GeometryTree tree = GeometryTree.fromModel(rawModel);
        return BakedModelFactory.DEFAULT_FACTORY.constructGeoModel(tree);
    }
}
