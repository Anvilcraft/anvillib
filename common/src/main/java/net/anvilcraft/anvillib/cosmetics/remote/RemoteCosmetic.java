package net.anvilcraft.anvillib.cosmetics.remote;

import net.anvilcraft.anvillib.cosmetics.CosmeticsManager;
import net.anvilcraft.anvillib.cosmetics.ICosmetic;
import net.anvilcraft.anvillib.cosmetics.remote.model.AnimationData;
import net.anvilcraft.anvillib.cosmetics.remote.model.TextureData;
import net.anvilcraft.anvillib.cosmetics.CosmeticParts;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.file.AnimationFile;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public class RemoteCosmetic implements ICosmetic {
    private Identifier id;
    private boolean loadedModel = false;
    private boolean loadedTexture = false;
    private boolean loadedAnimations = false;
    private Identifier modelLocation;
    private Identifier textureLocation;
    private Identifier animationsLocation;
    private CosmeticParts parts = new CosmeticParts();
    private String idleAnimation = null;
    private int frameTime = 1;
    private int frameCount = 1;

    public RemoteCosmetic(String id) {
        this.id = new Identifier("anvillib", id);
        this.modelLocation = new Identifier("anvillib", "models/remote/" + id);
        this.textureLocation = new Identifier("anvillib", "textures/remote/" + id);
        this.animationsLocation = new Identifier("anvillib", "animations/remote/" + id);
    }

    @Override
    public Identifier getAnimationFileLocation() {
        return this.animationsLocation;
    }

    @Override
    public Identifier getModelLocation() {
        return this.modelLocation;
    }

    @Override
    public Identifier getTextureLocation() {
        return this.textureLocation;
    }

    @Override
    public Identifier getID() {
        return this.id;
    }

    @Override
    public boolean readyToRender() {
        return this.loadedModel && this.loadedTexture && this.loadedAnimations;
    }

    public void loadModel(GeoModel model) {
        CosmeticsManager.loadModel(this.modelLocation, model);
        this.parts = new CosmeticParts(model);
        this.loadedModel = true;
    }

    public void loadTexture(TextureData data) {
        this.frameCount = data.frameCount;
        this.frameTime = data.frameTime;
        this.loadedTexture = true;
    }

    public void loadAnimations(AnimationFile file, AnimationData data) {
        if (data == null || file == null) {
            this.animationsLocation = null;
        } else {
            CosmeticsManager.loadAnimations(this.animationsLocation, file);
            this.idleAnimation = data.idleAnimation;
        }
        this.loadedAnimations = true;
    }

    @Override
    public String getBody() {
        return this.parts.body ? this.parts.bodyName : null;
    }

    @Override
    public String getHead() {
        return this.parts.head ? this.parts.headName : null;
    }

    @Override
    public String getLeftArm() {
        return this.parts.leftArm ? this.parts.leftArmName : null;
    }

    @Override
    public String getLeftLeg() {
        return this.parts.leftLeg ? this.parts.leftLegName : null;
    }

    @Override
    public String getRightArm() {
        return this.parts.rightArm ? this.parts.rightArmName : null;
    }

    @Override
    public String getRightLeg() {
        return this.parts.rightLeg ? this.parts.rightLegName : null;
    }

    @Override
    public String getIdleAnimationName() {
        return this.idleAnimation;
    }

    @Override
    public int getTotalFrames() {
        return this.frameCount;
    }

    @Override
    public int getFrameTime() {
        return this.frameTime;
    }
}
