package net.anvilcraft.anvillib.cosmetics;

import java.util.Objects;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoQuad;
import software.bernie.geckolib.cache.object.GeoVertex;
import software.bernie.geckolib.cache.texture.AnimatableTexture;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;

public class CosmeticArmorRenderer implements GeoRenderer<CosmeticItem> {
    private final CosmeticModel geoModel = new CosmeticModel();

    protected CosmeticItem currentArmorItem;
    protected LivingEntity entityLiving;
    protected int currentFrame = 0;

    public String headBone = null;
    public String bodyBone = null;
    public String rightArmBone = null;
    public String leftArmBone = null;
    public String rightLegBone = null;
    public String leftLegBone = null;

    private HumanoidModel<?> playerModel;

    @Override
    public GeoModel<CosmeticItem> getGeoModel() {
        return this.geoModel;
    }

    @Override
    public CosmeticItem getAnimatable() {
        return this.currentArmorItem;
    }

    @Override
    public void fireCompileRenderLayersEvent() {}

    @Override
    public boolean firePreRenderEvent(
        PoseStack poseStack,
        BakedGeoModel model,
        MultiBufferSource bufferSource,
        float partialTick,
        int packedLight
    ) {
        return true;
    }

    @Override
    public void firePostRenderEvent(
        PoseStack poseStack,
        BakedGeoModel model,
        MultiBufferSource bufferSource,
        float partialTick,
        int packedLight
    ) {}

    @Override
    public void updateAnimatedTextureFrame(CosmeticItem animatable) {
        if (this.entityLiving != null)
            AnimatableTexture.setAndUpdate(getTextureLocation(animatable));
    }

    public CosmeticArmorRenderer setCurrentItem(LivingEntity entity, CosmeticItem item) {
        this.entityLiving = entity;
        this.currentArmorItem = item;
        return this;
    }

    public void applyEntityStats(HumanoidModel<AbstractClientPlayer> model) {
        this.playerModel = model;
    }

    public void filterBones() {
        this.headBone = getCurrentCosmetic().getHead();
        this.bodyBone = getCurrentCosmetic().getBody();
        this.leftArmBone = getCurrentCosmetic().getLeftArm();
        this.rightArmBone = getCurrentCosmetic().getRightArm();
        this.leftLegBone = getCurrentCosmetic().getLeftLeg();
        this.rightLegBone = getCurrentCosmetic().getRightLeg();
    }

    public void render(
        CosmeticItem animatable,
        float partialTick,
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        int packedOverlay
    ) {
        this.currentArmorItem = animatable;
        defaultRender(poseStack, animatable, bufferSource, null, null, 0, partialTick, packedLight);
        this.currentArmorItem = null;
    }

    @Override
    public void preRender(
        PoseStack poseStack,
        CosmeticItem animatable,
        BakedGeoModel model,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay,
        int colour
    ) {
        if (!isReRender) {
            poseStack.translate(0, 24 / 16F, 0);
            poseStack.scale(-1, -1, 1);
        }
    }

    @Override
    public void actuallyRender(
        PoseStack poseStack,
        CosmeticItem animatable,
        BakedGeoModel model,
        RenderType renderType,
        MultiBufferSource bufferSource,
        VertexConsumer buffer,
        boolean isReRender,
        float partialTick,
        int packedLight,
        int packedOverlay,
        int colour
    ) {
        if (!isReRender) {
            if (this.entityLiving != null) {
                double currentTick = this.entityLiving.tickCount
                    / (double) getCurrentCosmetic().getFrameTime();
                this.currentFrame = ((int) currentTick) % getCurrentCosmetic().getTotalFrames();
            }

            AnimationState<CosmeticItem> animationState = new AnimationState<>(
                animatable, 0, 0, partialTick, false
            );
            long instanceId = getInstanceId(animatable);
            getGeoModel().addAdditionalStateData(animatable, instanceId, animationState::setData);
            getGeoModel().handleAnimations(animatable, instanceId, animationState, partialTick);

            setBoneVisibility(model, this.headBone, this.headBone != null);
            setBoneVisibility(model, this.bodyBone, this.bodyBone != null);
            setBoneVisibility(model, this.leftArmBone, this.leftArmBone != null);
            setBoneVisibility(model, this.rightArmBone, this.rightArmBone != null);
            setBoneVisibility(model, this.leftLegBone, this.leftLegBone != null);
            setBoneVisibility(model, this.rightLegBone, this.rightLegBone != null);
        }

        fitToBiped(model);
        GeoRenderer.super.actuallyRender(
            poseStack, animatable, model, renderType, bufferSource,
            buffer, isReRender, partialTick, packedLight, packedOverlay, colour
        );
    }

    @Override
    public void createVerticesOfQuad(
        GeoQuad quad,
        Matrix4f poseState,
        Vector3f normal,
        VertexConsumer buffer,
        int packedLight,
        int packedOverlay,
        int colour
    ) {
        for (GeoVertex vertex : quad.vertices()) {
            Vector3f position = vertex.position();
            Vector4f vector4f = poseState.transform(
                new Vector4f(position.x(), position.y(), position.z(), 1.0f)
            );
            buffer.addVertex(
                vector4f.x(), vector4f.y(), vector4f.z(),
                colour,
                vertex.texU(), calcVOffset(vertex.texV()),
                packedOverlay, packedLight,
                normal.x(), normal.y(), normal.z()
            );
        }
    }

    protected float calcVOffset(float v) {
        float totalFrames = (float) getCurrentCosmetic().getTotalFrames();
        float currentTextureOffset = (float) this.currentFrame / totalFrames;
        return (v / totalFrames) + currentTextureOffset;
    }

    protected void fitToBiped(BakedGeoModel model) {
        if (this.playerModel == null) return;

        if (this.headBone != null) {
            model.getBone(this.headBone).ifPresent(bone -> {
                bone.setRotX(this.playerModel.head.xRot);
                bone.setRotY(this.playerModel.head.yRot);
                bone.setRotZ(this.playerModel.head.zRot);
                bone.setPosX(this.playerModel.head.x);
                bone.setPosY(-this.playerModel.head.y);
                bone.setPosZ(this.playerModel.head.z);
            });
        }
        if (this.bodyBone != null) {
            model.getBone(this.bodyBone).ifPresent(bone -> {
                bone.setRotX(this.playerModel.body.xRot);
                bone.setRotY(this.playerModel.body.yRot);
                bone.setRotZ(this.playerModel.body.zRot);
                bone.setPosX(this.playerModel.body.x);
                bone.setPosY(-this.playerModel.body.y);
                bone.setPosZ(this.playerModel.body.z);
            });
        }
        if (this.rightArmBone != null) {
            model.getBone(this.rightArmBone).ifPresent(bone -> {
                bone.setRotX(this.playerModel.rightArm.xRot);
                bone.setRotY(this.playerModel.rightArm.yRot);
                bone.setRotZ(this.playerModel.rightArm.zRot);
                bone.setPosX(this.playerModel.rightArm.x + 5);
                bone.setPosY(2 - this.playerModel.rightArm.y);
                bone.setPosZ(this.playerModel.rightArm.z);
            });
        }
        if (this.leftArmBone != null) {
            model.getBone(this.leftArmBone).ifPresent(bone -> {
                bone.setRotX(this.playerModel.leftArm.xRot);
                bone.setRotY(this.playerModel.leftArm.yRot);
                bone.setRotZ(this.playerModel.leftArm.zRot);
                bone.setPosX(this.playerModel.leftArm.x - 5);
                bone.setPosY(2 - this.playerModel.leftArm.y);
                bone.setPosZ(this.playerModel.leftArm.z);
            });
        }
        if (this.rightLegBone != null) {
            model.getBone(this.rightLegBone).ifPresent(bone -> {
                bone.setRotX(this.playerModel.rightLeg.xRot);
                bone.setRotY(this.playerModel.rightLeg.yRot);
                bone.setRotZ(this.playerModel.rightLeg.zRot);
                bone.setPosX(this.playerModel.rightLeg.x + 2);
                bone.setPosY(12 - this.playerModel.rightLeg.y);
                bone.setPosZ(this.playerModel.rightLeg.z);
            });
        }
        if (this.leftLegBone != null) {
            model.getBone(this.leftLegBone).ifPresent(bone -> {
                bone.setRotX(this.playerModel.leftLeg.xRot);
                bone.setRotY(this.playerModel.leftLeg.yRot);
                bone.setRotZ(this.playerModel.leftLeg.zRot);
                bone.setPosX(this.playerModel.leftLeg.x - 2);
                bone.setPosY(12 - this.playerModel.leftLeg.y);
                bone.setPosZ(this.playerModel.leftLeg.z);
            });
        }
    }

    protected void setBoneVisibility(BakedGeoModel model, String boneName, boolean isVisible) {
        if (boneName == null) return;
        model.getBone(boneName).ifPresent(bone -> bone.setHidden(!isVisible));
    }

    @Override
    public long getInstanceId(CosmeticItem animatable) {
        return Objects.hash(
            this.currentArmorItem.getCosmetic().getID(),
            this.entityLiving != null ? this.entityLiving.getUUID() : 0
        );
    }

    public ICosmetic getCurrentCosmetic() {
        return this.currentArmorItem.getCosmetic();
    }
}
