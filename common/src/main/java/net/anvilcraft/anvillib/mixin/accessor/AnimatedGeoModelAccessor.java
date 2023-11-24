package net.anvilcraft.anvillib.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import software.bernie.geckolib3.core.processor.AnimationProcessor;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

@Mixin(AnimatedGeoModel.class)
public interface AnimatedGeoModelAccessor {
    @Accessor(remap = false)
    AnimationProcessor getAnimationProcessor();

    @Accessor(remap = false)
    GeoModel getCurrentModel();

    @Accessor(remap = false)
    void setCurrentModel(GeoModel model);
}
