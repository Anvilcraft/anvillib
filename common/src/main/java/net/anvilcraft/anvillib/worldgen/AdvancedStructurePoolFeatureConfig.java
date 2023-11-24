package net.anvilcraft.anvillib.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.structure.pool.StructurePool;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.StructurePoolFeatureConfig;

public class AdvancedStructurePoolFeatureConfig extends StructurePoolFeatureConfig {
    public static final Codec<StructurePoolFeatureConfig> CODEC
        = RecordCodecBuilder.create((instance) -> {
              return instance
                  .group(
                      StructurePool.REGISTRY_CODEC.fieldOf("start_pool")
                          .forGetter(
                              (self)
                                  -> ((AdvancedStructurePoolFeatureConfig) self)
                                         .getStartPool()
                          ),
                      Codec.intRange(0, 7).fieldOf("size").forGetter(
                          (self) -> ((AdvancedStructurePoolFeatureConfig) self).getSize()
                      ),
                      Codec.INT.fieldOf("max_distance_from_center")
                          .forGetter(
                              (self)
                                  -> ((AdvancedStructurePoolFeatureConfig) self)
                                         .maxDistanceFromCenter
                          )
                  )
                  .apply(instance, AdvancedStructurePoolFeatureConfig::new);
          });

    public final int maxDistanceFromCenter;

    public AdvancedStructurePoolFeatureConfig(
        RegistryEntry<StructurePool> startPool, int size, int maxDistanceFromCenter
    ) {
        super(startPool, size);
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }
}
