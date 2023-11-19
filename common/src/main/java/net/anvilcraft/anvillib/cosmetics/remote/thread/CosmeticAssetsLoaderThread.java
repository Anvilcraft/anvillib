package net.anvilcraft.anvillib.cosmetics.remote.thread;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;

import com.google.common.hash.Hashing;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.anvilcraft.anvillib.AnvilLib;
import net.anvilcraft.anvillib.cosmetics.remote.RemoteCosmetic;
import net.anvilcraft.anvillib.cosmetics.remote.RemoteCosmeticProvider;
import net.anvilcraft.anvillib.cosmetics.remote.model.AnimationData;
import net.anvilcraft.anvillib.cosmetics.remote.model.CosmeticData;
import net.anvilcraft.anvillib.cosmetics.remote.model.TextureData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.molang.MolangParser;
import software.bernie.geckolib3.file.AnimationFile;
import software.bernie.geckolib3.geo.exception.GeckoLibException;
import software.bernie.geckolib3.geo.raw.pojo.Converter;
import software.bernie.geckolib3.geo.raw.pojo.FormatVersion;
import software.bernie.geckolib3.geo.raw.pojo.RawGeoModel;
import software.bernie.geckolib3.geo.raw.tree.RawGeometryTree;
import software.bernie.geckolib3.geo.render.GeoBuilder;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.util.json.JsonAnimationUtils;

public class CosmeticAssetsLoaderThread extends FileDownloaderThread {

    private RemoteCosmetic cosmetic;
    private CosmeticData data;
    private MolangParser parser = new MolangParser();
    private TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
    private File cacheDir;
    private RemoteCosmeticProvider provider;

    public CosmeticAssetsLoaderThread(RemoteCosmetic cosmetic, CosmeticData data, File cacheDir, RemoteCosmeticProvider provider) {
        super("0.2.0");
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
            this.cosmetic.loadAnimations(null, anim);
            return;
        }
        AnimationFile animations = null;
        try {
            URI url = new URI(this.data.animationData.url);
            JsonObject data = this.loadJson(url, JsonObject.class);
            animations = this.buildAnimationFile(data);
        } catch (IOException | URISyntaxException | NullPointerException e) {
            AnvilLib.LOGGER.error("Could not load animation: {}", this.data.animationData.url, e);
        }
        this.cosmetic.loadAnimations(animations, anim);
    }

    private void loadTexture(TextureData data) {
        String hash = Hashing.sha1().hashUnencodedChars(this.data.id).toString();
        AbstractTexture texture = this.textureManager.getOrDefault(this.cosmetic.getTextureLocation(), MissingSprite.getMissingSpriteTexture());
        if (texture == MissingSprite.getMissingSpriteTexture()) {
            File file = new File(this.cacheDir, hash.length() > 2 ? hash.substring(0, 2) : "xx");
            File file2 = new File(file, hash);
            texture = new PlayerSkinTexture(file2, data.url, new Identifier("textures/block/dirt.png"), false, null);
            this.textureManager.registerTexture(this.cosmetic.getTextureLocation(), texture);
        }
        this.cosmetic.loadTexture(data);
    }

    private void loadModel(String url) {
        try {
            URI uri = new URI(url);
            String data = Objects.requireNonNull(this.getStringForURL(uri));
            GeoModel model = this.buildModel(data);
            this.cosmetic.loadModel(model);
        } catch (NullPointerException | URISyntaxException | IOException | GeckoLibException e) {
            AnvilLib.LOGGER.error("Can't load remote model: {}", url, e);
            this.handleFailure();
        }
    }

    private void handleFailure() {
        this.provider.failCosmeticLoading(this.data.id);
    }

    private AnimationFile buildAnimationFile(JsonObject json) {
        AnimationFile animationFile = new AnimationFile();
		for (Map.Entry<String, JsonElement> entry : JsonAnimationUtils.getAnimations(json)) {
			String animationName = entry.getKey();
			Animation animation;
			try {
				animation = JsonAnimationUtils.deserializeJsonToAnimation(
						JsonAnimationUtils.getAnimation(json, animationName), parser);
				animationFile.putAnimation(animationName, animation);
			} catch (Exception e) {
				AnvilLib.LOGGER.error("Could not load animation: {}", animationName, e);
				throw new RuntimeException(e);
			}
		}
		return animationFile;
    }

    private GeoModel buildModel(String json) throws IOException {
        Identifier location = this.cosmetic.getModelLocation();
        RawGeoModel rawModel = Converter.fromJsonString(json);
		if (rawModel.getFormatVersion() != FormatVersion.VERSION_1_12_0) {
			throw new GeckoLibException(location, "Wrong geometry json version, expected 1.12.0");
		}
		RawGeometryTree rawGeometryTree = RawGeometryTree.parseHierarchy(rawModel);
		return GeoBuilder.getGeoBuilder(location.getNamespace()).constructGeoModel(rawGeometryTree);
    }
    
}
