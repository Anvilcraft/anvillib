package net.anvilcraft.anvillib.cosmetics;

import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CosmeticModel extends GeoModel<CosmeticItem> {
    @Override
    public ResourceLocation getModelResource(CosmeticItem animatable) {
        return animatable.getCosmetic().getModelLocation();
    }

    @Override
    public ResourceLocation getTextureResource(CosmeticItem animatable) {
        return animatable.getCosmetic().getTextureLocation();
    }

    @Override
    public ResourceLocation getAnimationResource(CosmeticItem animatable) {
        return animatable.getCosmetic().getAnimationFileLocation();
    }
}
