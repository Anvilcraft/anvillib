package net.anvilcraft.anvillib.vector;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * An extension of a Vec3 that also includes a World.
 */
public class WorldVec extends Vec3 {
    public World world;

    public WorldVec(WorldVec wv) {
        super(wv);
        this.world = wv.world;
    }

    public WorldVec(World world, double x, double y, double z) {
        super(x, y, z);
        this.world = world;
    }

    public WorldVec(World world, Vec3 vec) {
        super(vec);
        this.world = world;
    }

    public WorldVec(Entity ent) {
        super(ent);
        this.world = ent.worldObj;
    }

    public WorldVec(TileEntity te) {
        super(te);
        this.world = te.getWorldObj();
    }

    /**
     * Gets the block at the position pointed to by this vector.
     */
    public Block getBlock() {
        return this.world.getBlock(this.intX(), this.intY(), this.intZ());
    }

    public int getBlockMeta() {
        return this.world.getBlockMetadata(this.intX(), this.intY(), this.intZ());
    }

    /**
     * Gets the tile entity at the position pointed to by this vector.
     */
    public TileEntity getTileEntity() {
        return this.world.getTileEntity(this.intX(), this.intY(), this.intZ());
    }

    public void markForUpdate() {
        this.world.markBlockForUpdate(this.intX(), this.intY(), this.intZ());
    }

    public void markForRenderUpdate() {
        this.world.markBlockRangeForRenderUpdate(
            this.intX(), this.intY(), this.intZ(), this.intX(), this.intY(), this.intZ()
        );
    }

    public TargetPoint targetPoint(double range) {
        return new TargetPoint(
            this.world.provider.dimensionId, this.x, this.y, this.z, range
        );
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((world == null) ? 0 : world.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorldVec other = (WorldVec) obj;
        if (world == null) {
            if (other.world != null)
                return false;
        } else if (world != other.world)
            return false;
        return true;
    }
}
