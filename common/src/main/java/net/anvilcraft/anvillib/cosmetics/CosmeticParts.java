package net.anvilcraft.anvillib.cosmetics;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;

public class CosmeticParts {
    public boolean head = false;
    public boolean body = false;
    public boolean leftArm = false;
    public boolean leftLeg = false;
    public boolean rightArm = false;
    public boolean rightLeg = false;

    public final String headName = "head";
    public final String bodyName = "body";
    public final String leftArmName = "arm_left";
    public final String leftLegName = "leg_left";
    public final String rightArmName = "arm_right";
    public final String rightLegName = "leg_right";

    public CosmeticParts() {}

    public CosmeticParts(BakedGeoModel model) {
        var maybeRoot = model.getBone("root");
        if (maybeRoot.isEmpty()) return;
        GeoBone root = maybeRoot.get();
        for (GeoBone bone : root.getChildBones()) {
            switch (bone.getName()) {
                case headName -> this.head = true;
                case bodyName -> this.body = true;
                case leftArmName -> this.leftArm = true;
                case leftLegName -> this.leftLeg = true;
                case rightArmName -> this.rightArm = true;
                case rightLegName -> this.rightLeg = true;
            }
        }
    }
}
