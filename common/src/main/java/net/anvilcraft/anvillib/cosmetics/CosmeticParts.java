package net.anvilcraft.anvillib.cosmetics;

import java.util.Optional;

import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.geo.render.built.GeoModel;

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

    public CosmeticParts() {
        
    }

    public CosmeticParts(GeoModel model) {
        Optional<GeoBone> maybeRoot = model.getBone("root");
        if (maybeRoot.isEmpty()) return;
        GeoBone root = maybeRoot.get();
        for (GeoBone bone : root.childBones) {
            switch (bone.name) {
                case headName:
                    this.head = true;
                    break;
                case bodyName:
                    this.body = true;
                    break;
                case leftArmName:
                    this.leftArm = true;
                    break;
                case leftLegName:
                    this.leftLeg = true;
                    break;
                case rightArmName:
                    this.rightArm = true;
                    break;
                case rightLegName:
                    this.rightLeg = true;
                    break;
            }
        }
    }

}
