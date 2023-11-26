package net.anvilcraft.anvillib.mixinutils;

import net.minecraft.structure.StructurePiece;
import net.minecraft.util.math.BlockBox;

public record BeardifierLocals(
    int x,
    int y,
    int z,
    StructurePiece structurePiece,
    BlockBox boundingBox,
    int l,
    int m
) {}
