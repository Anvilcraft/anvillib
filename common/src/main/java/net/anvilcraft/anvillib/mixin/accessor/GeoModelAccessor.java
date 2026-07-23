package net.anvilcraft.anvillib.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import software.bernie.geckolib.animation.AnimationProcessor;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.model.GeoModel;

@Mixin(GeoModel.class)
public interface GeoModelAccessor {
    
    @Accessor(remap = false)
    AnimationProcessor getProcessor();

    @Accessor(remap = false)
    BakedGeoModel getCurrentModel();

    @Accessor(remap = false)
    void setCurrentModel(BakedGeoModel model);

}
