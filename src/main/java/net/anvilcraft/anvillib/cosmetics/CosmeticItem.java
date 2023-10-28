package net.anvilcraft.anvillib.cosmetics;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class CosmeticItem implements IAnimatable {
    private ICosmetic cosmetic = null;
    private AnimationBuilder animationBuilder = new AnimationBuilder();

    public CosmeticItem(ICosmetic cosmetic) {
        this.cosmetic = cosmetic;
        this.cosmetic.addAnimations(animationBuilder);
    }

    private <P extends IAnimatable> PlayState predicate(AnimationEvent<P> event) {
        event.getController().transitionLengthTicks = 0;
        event.getController().setAnimation(animationBuilder);
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(
            new AnimationController<>(this, "controller", 20, this::predicate)
        );
    }

    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    public ICosmetic getCosmetic() {
        return this.cosmetic;
    }
}
