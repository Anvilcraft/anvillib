package net.anvilcraft.anvillib.cosmetics;

import net.anvilcraft.anvillib.mixin.accessor.GeoModelAccessor;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.GeckoLibConstants;
import software.bernie.geckolib.animation.Animation;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.loading.object.BakedAnimations;
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

    @Override
    public Animation getAnimation(CosmeticItem animatable, String name) {
        ResourceLocation location
            = ((CosmeticItem) animatable).getCosmetic().getAnimationFileLocation();
        BakedAnimations animation = CosmeticsManager.getAnimations(location);
        if (animation == null) {
            animation = GeckoLibCache.getBakedAnimations().get(location);
        }

        if (animation == null) {
            throw GeckoLibConstants.exception(
                location, "Could not find animation file. Please double check name."
            );
        }

        return animation.getAnimation(name);
    }

    @Override
    public BakedGeoModel getBakedModel(ResourceLocation location) {
        BakedGeoModel model = CosmeticsManager.getModel(location);
        if (model == null) {
            model = GeckoLibCache.getBakedModels().get(location);
        }

        GeoModelAccessor accessor = (GeoModelAccessor) this;
        if (model != accessor.getCurrentModel()) {
            accessor.getProcessor().setActiveModel(model);
            accessor.setCurrentModel(model);
        }

        return model;
    }
    
}
