package net.anvilcraft.anvillib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.anvilcraft.anvillib.worldgen.AdvancedStructurePoolFeatureConfig;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.math.Box;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

@Mixin(StructurePoolBasedGenerator.class)
public class StructurePoolBasedGeneratorMixin {
    // this is a lambda
    @ModifyVariable(method = "method_39824", at = @At(value = "STORE", ordinal = 0))
    private static Box adjustAdvBoundingBox(
        Box box, PoolStructurePiece alec, StructurePoolFeatureConfig conf
    ) {
        if (conf instanceof AdvancedStructurePoolFeatureConfig aconf) {
            return new Box(
                box.minX + 80 - aconf.maxDistanceFromCenter,
                box.minY + 80 - aconf.maxDistanceFromCenter,
                box.minZ + 80 - aconf.maxDistanceFromCenter,
                box.maxX - 80 + aconf.maxDistanceFromCenter,
                box.maxY - 80 + aconf.maxDistanceFromCenter,
                box.maxZ - 80 + aconf.maxDistanceFromCenter
            );
        }
        return box;
    }
}
