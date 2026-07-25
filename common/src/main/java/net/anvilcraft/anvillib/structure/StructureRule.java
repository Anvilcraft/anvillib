package net.anvilcraft.anvillib.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Optional;

public record StructureRule(
    Optional<HolderSet<DimensionType>> dimensionSelector,
    Optional<HolderSet<Biome>> biomeSelector,
    Optional<HolderSet<Structure>> structureSelector,
    boolean allow,
    int priority
) {
    public static final ResourceKey<Registry<StructureRule>> REGISTRY_KEY =
        ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath("anvillib", "structure_rules"));

    public static final Codec<StructureRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        RegistryCodecs.homogeneousList(Registries.DIMENSION_TYPE).optionalFieldOf("dimension_selector").forGetter(StructureRule::dimensionSelector),
        RegistryCodecs.homogeneousList(Registries.BIOME).optionalFieldOf("biome_selector").forGetter(StructureRule::biomeSelector),
        RegistryCodecs.homogeneousList(Registries.STRUCTURE).optionalFieldOf("structure_selector").forGetter(StructureRule::structureSelector),
        Codec.BOOL.fieldOf("allow").forGetter(StructureRule::allow),
        Codec.INT.fieldOf("priority").forGetter(StructureRule::priority)
    ).apply(instance, StructureRule::new));

    public boolean matches(Level dimension, Holder<Structure> structure, ChunkPos pos) {
        if (dimensionSelector.isPresent() && !dimensionSelector.get().contains(dimension.dimensionTypeRegistration()))
            return false;
        if (structureSelector.isPresent() && !structureSelector.get().contains(structure))
            return false;
        if (biomeSelector.isPresent() && !biomeSelector.get().contains(dimension.getBiome(pos.getWorldPosition().offset(8, 64, 8))))
            return false;
        return true;
    }
}
