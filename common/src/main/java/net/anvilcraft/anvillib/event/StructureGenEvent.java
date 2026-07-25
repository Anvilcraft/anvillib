package net.anvilcraft.anvillib.event;

import net.minecraft.core.Holder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

public class StructureGenEvent {

    public final Level dimension;
    public final Holder<Structure> structure;
    public final ChunkPos position;
    private boolean cancelled = false;

    public StructureGenEvent(Level dimension, Holder<Structure> structure, ChunkPos position) {
        this.dimension = dimension;
        this.structure = structure;
        this.position = position;
    }

    public void cancel() {
        cancelled = true;
    }

    public boolean isCancelled() {
        return cancelled;
    }

}
