package net.anvilcraft.anvillib.cosmetics;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class CosmeticFeatureRenderer extends ArmorFeatureRenderer<
    AbstractClientPlayerEntity,
    PlayerEntityModel<AbstractClientPlayerEntity>,
    BipedEntityModel<AbstractClientPlayerEntity>> {
    private static final Map<ICosmetic, CosmeticItem> modelCache = new HashMap<>();
    private static CosmeticArmorRenderer cosmeticRenderer = null;
    PlayerEntityRenderer renderer;
    String skin;

    public CosmeticFeatureRenderer(PlayerEntityRenderer renderer, String skin) {
        super(renderer, null, null);
        this.renderer = renderer;
        this.skin = skin;
    }

    @Override
    public void render(
        MatrixStack matrix,
        VertexConsumerProvider buffer,
        int light,
        AbstractClientPlayerEntity player,
        float limbSwing,
        float limbSwingAmount,
        float partialTicks,
        float ageInTicks,
        float netHeadYaw,
        float headPitch
    ) {
        for (ICosmetic c : CosmeticsManager.getCosmeticsForPlayer(player.getUuid())) {
            if (c.readyToRender())
                this.renderCosmetic(matrix, buffer, player, light, c, partialTicks);
        }
    }

    private void renderCosmetic(
        MatrixStack matrix,
        VertexConsumerProvider buffer,
        AbstractClientPlayerEntity player,
        int light,
        ICosmetic cosmetic,
        float partialTicks
    ) {
        if (cosmeticRenderer == null)
            cosmeticRenderer = new CosmeticArmorRenderer();
        if (!modelCache.containsKey(cosmetic))
            modelCache.put(cosmetic, new CosmeticItem(cosmetic));
        CosmeticItem item = modelCache.get(cosmetic);
        copyRotations(this.renderer.getModel(), cosmeticRenderer);
        cosmeticRenderer.applyEntityStats(this.renderer.getModel());
        cosmeticRenderer.setCurrentItem(player, item);
        cosmeticRenderer.filterBones();

        VertexConsumer vertex = ItemRenderer.getArmorGlintConsumer(
            buffer,
            RenderLayer.getArmorCutoutNoCull(cosmetic.getTextureLocation()),
            false,
            false
        );
        cosmeticRenderer.render(partialTicks, matrix, vertex, light);
    }

    private static void copyRotations(BipedEntityModel<?> from, BipedEntityModel<?> to) {
        copyRotations(from.head, to.head);
        copyRotations(from.hat, to.hat);
        copyRotations(from.body, to.body);
        copyRotations(from.leftArm, to.leftArm);
        copyRotations(from.rightArm, to.rightArm);
        copyRotations(from.rightLeg, to.rightLeg);
        copyRotations(from.leftLeg, to.leftLeg);
    }

    private static void copyRotations(ModelPart from, ModelPart to) {
        to.copyTransform(from);
    }
}
