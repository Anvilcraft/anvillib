package net.anvilcraft.anvillib.cosmetics;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class CosmeticModel extends AnimatedGeoModel<CosmeticItem> {
    @Override
    public Identifier getAnimationFileLocation(CosmeticItem animatable) {
        return animatable.getCosmetic().getAnimationFileLocation();
    }

    @Override
    public Identifier getModelLocation(CosmeticItem animatable) {
        return animatable.getCosmetic().getModelLocation();
    }

    @Override
    public Identifier getTextureLocation(CosmeticItem animatable) {
        return animatable.getCosmetic().getTextureLocation();
    }
}
