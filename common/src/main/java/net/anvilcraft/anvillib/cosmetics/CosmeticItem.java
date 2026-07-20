package net.anvilcraft.anvillib.cosmetics;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CosmeticItem implements GeoAnimatable {
    private final ICosmetic cosmetic;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final RawAnimation idleAnimation;

    public CosmeticItem(ICosmetic cosmetic) {
        this.cosmetic = cosmetic;
        if (cosmetic.getIdleAnimationName() != null) {
            this.idleAnimation = RawAnimation.begin().thenLoop(cosmetic.getIdleAnimationName());
        } else {
            this.idleAnimation = null;
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar registrar) {
        if (this.idleAnimation != null) {
            registrar.add(new AnimationController<>(this, "controller", 0, state -> {
                state.getController().setAnimation(this.idleAnimation);
                return PlayState.CONTINUE;
            }));
        }
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    @Override
    public double getTick(Object entity) {
        return 0;
    }

    public ICosmetic getCosmetic() {
        return this.cosmetic;
    }
}
