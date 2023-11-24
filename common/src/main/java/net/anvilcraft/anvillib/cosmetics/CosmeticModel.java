package net.anvilcraft.anvillib.cosmetics;

import net.anvilcraft.anvillib.mixin.accessor.AnimatedGeoModelAccessor;
import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.file.AnimationFile;
import software.bernie.geckolib3.geo.exception.GeckoLibException;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;
import software.bernie.geckolib3.resource.GeckoLibCache;

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

    @Override
    public Animation getAnimation(String name, IAnimatable animatable) {
        Identifier location
            = ((CosmeticItem) animatable).getCosmetic().getAnimationFileLocation();
        AnimationFile animation = CosmeticsManager.getAnimations(location);
        if (animation == null) {
            animation = GeckoLibCache.getInstance().getAnimations().get(location);
        }

        if (animation == null) {
            throw new GeckoLibException(
                location, "Could not find animation file. Please double check name."
            );
        }

        return animation.getAnimation(name);
    }

    @Override
    public GeoModel getModel(Identifier location) {
        GeoModel model = CosmeticsManager.getModel(location);
        if (model == null) {
            model = GeckoLibCache.getInstance().getGeoModels().get(location);
        }

        AnimatedGeoModelAccessor accessor = (AnimatedGeoModelAccessor) this;
        if (model != accessor.getCurrentModel()) {
            accessor.getAnimationProcessor().clearModelRendererList();
            accessor.setCurrentModel(model);

            for (GeoBone bone : model.topLevelBones) {
                registerBone(bone);
            }
        }

        return model;
    }
}
