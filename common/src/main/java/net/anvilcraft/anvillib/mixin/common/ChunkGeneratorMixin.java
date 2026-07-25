package net.anvilcraft.anvillib.mixin.common;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.anvilcraft.anvillib.event.Bus;
import net.anvilcraft.anvillib.event.StructureGenEvent;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

@Mixin(ChunkGenerator.class)
public abstract class ChunkGeneratorMixin {

    @Inject(method = "tryGenerateStructure", at = @At("HEAD"), cancellable = true)
	private void onTryGenerateStructure(
		StructureSet.StructureSelectionEntry entry,
		StructureManager manager,
		RegistryAccess registryAccess,
		RandomState random,
		StructureTemplateManager templateManager,
		long seed,
		ChunkAccess chunk,
		ChunkPos chunkPos,
		SectionPos sectionPos,
		CallbackInfoReturnable<Boolean> cir
	) {
		Level dimension = manager.level instanceof ServerLevelAccessor sla ? sla.getLevel() : null;
        StructureGenEvent event = new StructureGenEvent(dimension, entry.structure(), chunkPos);
        Bus.MAIN.fire(event);
        if (event.isCancelled()) {
            cir.setReturnValue(false);
        }
    }
}
