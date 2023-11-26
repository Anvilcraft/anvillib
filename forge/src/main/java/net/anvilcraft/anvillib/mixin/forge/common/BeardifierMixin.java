package net.anvilcraft.anvillib.mixin.forge.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.anvilcraft.anvillib.mixinutils.BeardifierLocals;
import net.anvilcraft.anvillib.worldgen.AdvancedStructurePoolFeatureConfig;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructureStart;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.StructureWeightSampler;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

/**
 * This mixin is responsible for reimplementing 1.19's BEARD_BOX which causes terrain
 * to be removed around ancient city structures.
 */
@Mixin(StructureWeightSampler.class)
public class BeardifierMixin {
    private static ThreadLocal<BeardifierLocals> currentLocals = new ThreadLocal<>();
    @Unique
    private Map<Object, ConfiguredStructureFeature<?, ?>> featureMap = new HashMap<>();

    // This is different than the fabric implementation where this is @ModifyArgs, but
    // that's borked on Forge (as per usual).
    @Inject(
        // for this nonsense to work in the devenv, replace this with method_38319
        // for prod, use m_208194_
        // very elegant!
        method = "m_208194_",
        at = @At(
            value = "INVOKE",
            target = "Lit/unimi/dsi/fastutil/objects/ObjectList;add(Ljava/lang/Object;)Z"
        ),
        remap = false,
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void
    addStructurePieceToMap(
        ChunkPos alec1,
        int alec2,
        int alec3,
        StructureStart ss,
        CallbackInfo ci,
        Iterator<?> alec4,
        StructurePiece sp
    ) {
        featureMap.put(sp, ss.getFeature());
    }

    @Inject(
        method = "sample",
        at = @At(
            value = "INVOKE",
            target
            = "Lnet/minecraft/structure/StructurePiece;getWeightType()Lnet/minecraft/world/gen/StructureWeightType;"
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void
    collectVariables(
        DensityFunction.NoisePos alec1,
        CallbackInfoReturnable<Double> ci,
        int i,
        int j,
        int k,
        double alec2,
        StructurePiece structurePiece,
        BlockBox boundingBox,
        int l,
        int m
    ) {
        currentLocals.set(new BeardifierLocals(i, j, k, structurePiece, boundingBox, l, m)
        );
    }

    @Shadow
    protected static double getStructureWeight(int l, int m, int n) {
        throw new AssertionError();
    }

    @Redirect(
        method = "sample",
        at = @At(
            value = "INVOKE",
            target
            = "Lnet/minecraft/world/gen/StructureWeightSampler;getStructureWeight(III)D"
        )
    )
    private double
    beardContribution(int l, int m, int n) {
        BeardifierLocals locals = currentLocals.get();
        if (locals == null)
            return getStructureWeight(l, m, n);
        currentLocals.remove();

        ConfiguredStructureFeature<?, ?> sf = this.featureMap.get(locals.structurePiece());
        if (sf == null /* WTF */ || !(sf.config instanceof AdvancedStructurePoolFeatureConfig && ((AdvancedStructurePoolFeatureConfig) sf.config).useBoxBeardifier))
            return getStructureWeight(l, m, n);

        int q = Math.max(0, Math.max(-m, locals.y() - locals.boundingBox().getMaxY()));

        return getAncientCityBeardContribution(l, q, n, m);
    }

    private static double getAncientCityBeardContribution(int i, int j, int k, int l) {
        int m = i + 12;
        int n = j + 12;
        int o = k + 12;
        if (!(m >= 0 && m < 24 && n >= 0 && n < 24 && o >= 0 && o < 24)) {
            return 0.0;
        }
        double d = (double) l + 0.5;
        double e = MathHelper.squaredMagnitude(i, d, k);
        double f = -d * MathHelper.fastInverseSqrt(e / 2.0) / 2.0;
        return f * (double) computeBeardContribution(o - 12, m - 12, n - 12);
    }

    private static double computeBeardContribution(int i, double d, int j) {
        double e = MathHelper.squaredMagnitude(i, d, j);
        double f = Math.pow(Math.E, -e / 16.0);
        return f;
    }
}
