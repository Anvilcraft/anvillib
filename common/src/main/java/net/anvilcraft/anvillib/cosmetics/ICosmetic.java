package net.anvilcraft.anvillib.cosmetics;

import net.minecraft.util.Identifier;

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

    default String getIdleAnimationName() {
        return null;
    }

    default boolean readyToRender() {
        return true;
    }

    Identifier getID();

    default int getTotalFrames() {
        return 1;
    }

    /**
     * Returns how many ticks a frame of the animation should take.
     */
    default int getFrameTime() {
        return 1;
    }
}
