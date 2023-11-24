package net.anvilcraft.anvillib.cosmetics;

import java.util.Arrays;
import java.util.Objects;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimatableModel;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.controller.AnimationController.ModelFetcher;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.processor.IBone;
import software.bernie.geckolib3.core.util.Color;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.geo.render.built.GeoQuad;
import software.bernie.geckolib3.geo.render.built.GeoVertex;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.renderers.geo.IGeoRenderer;
import software.bernie.geckolib3.util.EModelRenderCycle;
import software.bernie.geckolib3.util.GeoUtils;
import software.bernie.geckolib3.util.IRenderCycle;
import software.bernie.geckolib3.util.RenderUtils;

public class CosmeticArmorRenderer extends BipedEntityModel<PlayerEntity>
    implements IGeoRenderer<CosmeticItem>, ModelFetcher<CosmeticItem> {
    protected CosmeticItem currentArmorItem;
    protected LivingEntity entityLiving;
    protected float widthScale = 1;
    protected float heightScale = 1;
    protected Matrix4f dispatchedMat = new Matrix4f();
    protected Matrix4f renderEarlyMat = new Matrix4f();
    protected int currentFrame = 0;

    public String headBone = null;
    public String bodyBone = null;
    public String rightArmBone = null;
    public String leftArmBone = null;
    public String rightLegBone = null;
    public String leftLegBone = null;

    private final AnimatedGeoModel<CosmeticItem> modelProvider;

    protected VertexConsumerProvider rtb = null;

    private IRenderCycle currentModelRenderCycle = EModelRenderCycle.INITIAL;

    { AnimationController.addModelFetcher(this); }

    @Override
    public IAnimatableModel<CosmeticItem> apply(IAnimatable t) {
        if (t instanceof CosmeticItem)
            return this.getGeoModelProvider();
        return null;
    }

    public CosmeticArmorRenderer() {
        super(MinecraftClient.getInstance().getEntityModelLoader().getModelPart(
            EntityModelLayers.PLAYER_INNER_ARMOR
        ));

        this.modelProvider = new CosmeticModel();
    }

    @Override
    public void render(
        MatrixStack poseStack,
        VertexConsumer buffer,
        int packedLight,
        int packedOverlay,
        float red,
        float green,
        float blue,
        float alpha
    ) {
        this.render(0, poseStack, buffer, packedLight);
    }

    public void render(
        float partialTick, MatrixStack poseStack, VertexConsumer buffer, int packedLight
    ) {
        GeoModel model = this.modelProvider.getModel(
            this.modelProvider.getModelLocation(this.currentArmorItem)
        );
        AnimationEvent<CosmeticItem> animationEvent = new AnimationEvent<>(
            this.currentArmorItem,
            0,
            0,
            MinecraftClient.getInstance().getTickDelta(),
            false, // can also be getLastFrameDuration()
            Arrays.asList(this.entityLiving)
        );

        poseStack.push();
        poseStack.translate(0, 24 / 16F, 0);
        poseStack.scale(-1, -1, 1);

        double currentTick = entityLiving.age; // TODO: Custom frametime/animation speed
        currentFrame
            = ((int) (currentTick * 1.0F)) % this.getCurrentCosmetic().getTotalFrames();
        ;

        //this.dispatchedMat = poseStack.last().pose().copy();
        this.dispatchedMat = poseStack.peek().getPositionMatrix().copy();

        this.modelProvider.setCustomAnimations(
            this.currentArmorItem, getInstanceId(this.currentArmorItem), animationEvent
        );
        setCurrentModelRenderCycle(EModelRenderCycle.INITIAL);
        fitToBiped();
        RenderSystem.setShaderTexture(0, getTextureLocation(this.currentArmorItem));

        Color renderColor = getRenderColor(
            this.currentArmorItem, partialTick, poseStack, null, buffer, packedLight
        );
        RenderLayer renderType = getRenderType(
            this.currentArmorItem,
            partialTick,
            poseStack,
            null,
            buffer,
            packedLight,
            getTextureLocation(this.currentArmorItem)
        );

        render(
            model,
            this.currentArmorItem,
            partialTick,
            renderType,
            poseStack,
            null,
            buffer,
            packedLight,
            OverlayTexture.DEFAULT_UV,
            renderColor.getRed() / 255f,
            renderColor.getGreen() / 255f,
            renderColor.getBlue() / 255f,
            renderColor.getAlpha() / 255f
        );

        //if (ModList.get().isLoaded("patchouli"))
        //    PatchouliCompat.patchouliLoaded(poseStack);

        poseStack.pop();
    }

    @Override
    public void renderEarly(
        CosmeticItem animatable,
        MatrixStack poseStack,
        float partialTick,
        VertexConsumerProvider bufferSource,
        VertexConsumer buffer,
        int packedLight,
        int packedOverlay,
        float red,
        float green,
        float blue,
        float alpha
    ) {
        //this.renderEarlyMat = poseStack.last().pose().copy();
        this.renderEarlyMat = poseStack.peek().getPositionMatrix().copy();
        this.currentArmorItem = animatable;

        IGeoRenderer.super.renderEarly(
            animatable,
            poseStack,
            partialTick,
            bufferSource,
            buffer,
            packedLight,
            packedOverlay,
            red,
            green,
            blue,
            alpha
        );
    }

    @Override
    public void renderRecursively(
        GeoBone bone,
        MatrixStack poseStack,
        VertexConsumer buffer,
        int packedLight,
        int packedOverlay,
        float red,
        float green,
        float blue,
        float alpha
    ) {
        if (bone.isTrackingXform()) {
            //Matrix4f poseState = poseStack.last().pose();
            Matrix4f poseState = poseStack.peek().getPositionMatrix();
            Vec3d renderOffset = getRenderOffset(this.currentArmorItem, 1);
            Matrix4f localMatrix
                = RenderUtils.invertAndMultiplyMatrices(poseState, this.dispatchedMat);

            bone.setModelSpaceXform(
                RenderUtils.invertAndMultiplyMatrices(poseState, this.renderEarlyMat)
            );
            //localMatrix.translate(new Vec3f(renderOffset));
            localMatrix.addToLastColumn(new Vec3f(renderOffset));
            bone.setLocalSpaceXform(localMatrix);
        }

        IGeoRenderer.super.renderRecursively(
            bone, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha
        );
    }

    public Vec3d getRenderOffset(CosmeticItem entity, float partialTick) {
        return Vec3d.ZERO;
    }

    protected void fitToBiped() {
        if (this.headBone != null) {
            IBone headBone = this.modelProvider.getBone(this.headBone);

            GeoUtils.copyRotations(this.head, headBone);
            headBone.setPositionX(this.head.pivotX);
            headBone.setPositionY(-this.head.pivotY);
            headBone.setPositionZ(this.head.pivotZ);
        }

        if (this.bodyBone != null) {
            IBone bodyBone = this.modelProvider.getBone(this.bodyBone);

            GeoUtils.copyRotations(this.body, bodyBone);
            bodyBone.setPositionX(this.body.pivotX);
            bodyBone.setPositionY(-this.body.pivotY);
            bodyBone.setPositionZ(this.body.pivotZ);
        }

        if (this.rightArmBone != null) {
            IBone rightArmBone = this.modelProvider.getBone(this.rightArmBone);

            GeoUtils.copyRotations(this.rightArm, rightArmBone);
            rightArmBone.setPositionX(this.rightArm.pivotX + 5);
            rightArmBone.setPositionY(2 - this.rightArm.pivotY);
            rightArmBone.setPositionZ(this.rightArm.pivotZ);
        }

        if (this.leftArmBone != null) {
            IBone leftArmBone = this.modelProvider.getBone(this.leftArmBone);

            GeoUtils.copyRotations(this.leftArm, leftArmBone);
            leftArmBone.setPositionX(this.leftArm.pivotX - 5);
            leftArmBone.setPositionY(2 - this.leftArm.pivotY);
            leftArmBone.setPositionZ(this.leftArm.pivotZ);
        }

        if (this.rightLegBone != null) {
            IBone rightLegBone = this.modelProvider.getBone(this.rightLegBone);

            GeoUtils.copyRotations(this.rightLeg, rightLegBone);
            rightLegBone.setPositionX(this.rightLeg.pivotX + 2);
            rightLegBone.setPositionY(12 - this.rightLeg.pivotY);
            rightLegBone.setPositionZ(this.rightLeg.pivotZ);
        }

        if (this.leftLegBone != null) {
            IBone leftLegBone = this.modelProvider.getBone(this.leftLegBone);

            GeoUtils.copyRotations(this.leftLeg, leftLegBone);
            leftLegBone.setPositionX(this.leftLeg.pivotX - 2);
            leftLegBone.setPositionY(12 - this.leftLeg.pivotY);
            leftLegBone.setPositionZ(this.leftLeg.pivotZ);
        }
    }

    @Override
    public AnimatedGeoModel<CosmeticItem> getGeoModelProvider() {
        return this.modelProvider;
    }

    @Override
    public IRenderCycle getCurrentModelRenderCycle() {
        return this.currentModelRenderCycle;
    }

    @Override
    public void setCurrentModelRenderCycle(IRenderCycle currentModelRenderCycle) {
        this.currentModelRenderCycle = currentModelRenderCycle;
    }

    @Override
    public float getWidthScale(CosmeticItem animatable) {
        return this.widthScale;
    }

    @Override
    public float getHeightScale(CosmeticItem entity) {
        return this.heightScale;
    }

    @Override
    public Identifier getTextureLocation(CosmeticItem animatable) {
        return this.modelProvider.getTextureLocation(animatable);
    }

    /**
     * Everything after this point needs to be called every frame before rendering
     */
    public CosmeticArmorRenderer setCurrentItem(LivingEntity entity, CosmeticItem item) {
        this.entityLiving = entity;
        this.currentArmorItem = item;

        return this;
    }

    public final CosmeticArmorRenderer
    applyEntityStats(BipedEntityModel<AbstractClientPlayerEntity> defaultArmor) {
        this.child = defaultArmor.child;
        this.sneaking = defaultArmor.sneaking;
        this.riding = defaultArmor.riding;
        this.rightArmPose = defaultArmor.rightArmPose;
        this.leftArmPose = defaultArmor.leftArmPose;

        return this;
    }

    public void filterBones() {
        this.headBone = getCurrentCosmetic().getHead();
        this.bodyBone = getCurrentCosmetic().getBody();
        this.leftArmBone = getCurrentCosmetic().getLeftArm();
        this.rightArmBone = getCurrentCosmetic().getRightArm();
        this.leftLegBone = getCurrentCosmetic().getLeftLeg();
        this.rightLegBone = getCurrentCosmetic().getRightLeg();

        getGeoModelProvider().getModel(getCurrentCosmetic().getModelLocation());

        this.setBoneVisibility(this.headBone, getCurrentCosmetic().getHead() != null);
        this.setBoneVisibility(this.bodyBone, getCurrentCosmetic().getBody() != null);
        this.setBoneVisibility(
            this.leftArmBone, getCurrentCosmetic().getLeftArm() != null
        );
        this.setBoneVisibility(
            this.rightArmBone, getCurrentCosmetic().getRightArm() != null
        );
        this.setBoneVisibility(
            this.leftLegBone, getCurrentCosmetic().getLeftLeg() != null
        );
        this.setBoneVisibility(
            this.rightLegBone, getCurrentCosmetic().getRightLeg() != null
        );
    }

    /**
     * Sets a specific bone (and its child-bones) to visible or not
     * @param boneName The name of the bone
     * @param isVisible Whether the bone should be visible
     */
    protected void setBoneVisibility(String boneName, boolean isVisible) {
        if (boneName == null)
            return;

        this.modelProvider.getBone(boneName).setHidden(!isVisible);
    }

    /**
     * Use {@link CosmeticArmorRenderer#setBoneVisibility(String, boolean)}
     */
    @Deprecated(forRemoval = true)
    protected IBone getAndHideBone(String boneName) {
        setBoneVisibility(boneName, false);

        return this.modelProvider.getBone(boneName);
    }

    @Override
    public int getInstanceId(CosmeticItem animatable) {
        return Objects.hash(
            this.currentArmorItem.getCosmetic().getID(), this.entityLiving.getUuid()
        );
    }

    @Override
    public void setCurrentRTB(VertexConsumerProvider bufferSource) {
        this.rtb = bufferSource;
    }

    @Override
    public VertexConsumerProvider getCurrentRTB() {
        return this.rtb;
    }

    public ICosmetic getCurrentCosmetic() {
        return this.currentArmorItem.getCosmetic();
    }

    public float calcVOffset(float v) {
        float totalFrames = (float) this.getCurrentCosmetic().getTotalFrames();
        float currentTextureOffset = (float) currentFrame / totalFrames;
        return (v / totalFrames) + currentTextureOffset;
    }

    @Override
    public void createVerticesOfQuad(
        GeoQuad quad,
        Matrix4f poseState,
        Vec3f normal,
        VertexConsumer buffer,
        int packedLight,
        int packedOverlay,
        float red,
        float green,
        float blue,
        float alpha
    ) {
        for (GeoVertex vertex : quad.vertices) {
            Vector4f vector4f = new Vector4f(
                vertex.position.getX(), vertex.position.getY(), vertex.position.getZ(), 1
            );

            vector4f.transform(poseState);
            buffer.vertex(
                vector4f.getX(),
                vector4f.getY(),
                vector4f.getZ(),
                red,
                green,
                blue,
                alpha,
                vertex.textureU,
                calcVOffset(vertex.textureV),
                packedOverlay,
                packedLight,
                normal.getX(),
                normal.getY(),
                normal.getZ()
            );
        }
    }
}
