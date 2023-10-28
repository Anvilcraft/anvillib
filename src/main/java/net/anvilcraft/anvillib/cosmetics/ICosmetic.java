package net.anvilcraft.anvillib.cosmetics;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.core.builder.AnimationBuilder;

public interface ICosmetic {
    Identifier getAnimationFileLocation();

    Identifier getModelLocation();

    Identifier getTextureLocation();

    default String getHead() {
        return null; //head
    }

    default String getBody() {
        return null; //body
    }

    default String getLeftArm() {
        return null; //arm_left
    }

    default String getRightArm() {
        return null; //arm_right
    }

    default String getLeftLeg() {
        return null; //leg_left
    }

    default String getRightLeg() {
        return null; //leg_right
    }

    void addAnimations(AnimationBuilder builder);

    default boolean readyToRender() {
        return true;
    }

    Identifier getID();
}
