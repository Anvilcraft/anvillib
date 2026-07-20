package net.anvilcraft.anvillib.cosmetics;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class CosmeticFeatureRenderer
    extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    private static final Map<ICosmetic, CosmeticItem> modelCache = new HashMap<>();
    private static CosmeticArmorRenderer cosmeticRenderer = null;

    public CosmeticFeatureRenderer(PlayerRenderer renderer) {
        super(renderer);
    }

    @Override
    public void render(
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        int packedLight,
        AbstractClientPlayer player,
        float limbSwing,
        float limbSwingAmount,
        float partialTick,
        float ageInTicks,
        float netHeadYaw,
        float headPitch
    ) {
        if (player.isInvisible())
            return;
        for (ICosmetic c : CosmeticsManager.getCosmeticsForPlayer(player.getUUID())) {
            if (c.readyToRender())
                this.renderCosmetic(poseStack, bufferSource, player, packedLight, c, partialTick);
        }
    }

    private void renderCosmetic(
        PoseStack poseStack,
        MultiBufferSource bufferSource,
        AbstractClientPlayer player,
        int packedLight,
        ICosmetic cosmetic,
        float partialTick
    ) {
        if (cosmeticRenderer == null)
            cosmeticRenderer = new CosmeticArmorRenderer();
        if (!modelCache.containsKey(cosmetic))
            modelCache.put(cosmetic, new CosmeticItem(cosmetic));
        CosmeticItem item = modelCache.get(cosmetic);
        cosmeticRenderer.applyEntityStats(getParentModel());
        cosmeticRenderer.setCurrentItem(player, item);
        cosmeticRenderer.filterBones();
        cosmeticRenderer.render(item, partialTick, poseStack, bufferSource, packedLight, OverlayTexture.NO_OVERLAY);
    }
}
