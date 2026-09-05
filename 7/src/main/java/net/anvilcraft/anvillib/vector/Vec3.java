package net.anvilcraft.anvillib.vector;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * A 3-Dimensional vector represented as 3 doubles.
 */
public class Vec3 {
    public double x;
    public double y;
    public double z;

    public Vec3(Vec3 vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    public Vec3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3(Entity ent) {
        this(ent.posX, ent.posY, ent.posZ);
    }

    public Vec3(TileEntity te) {
        this(te.xCoord, te.yCoord, te.zCoord);
    }

    public Vec3(MovingObjectPosition mop) {
        this(mop.blockX, mop.blockY, mop.blockZ);
    }

    /**
     * Reads this Vec3 from a byte buf.
     *
     * @see writeToByteBuf
     */
    public static Vec3 readFromByteBuf(ByteBuf buf) {
        return new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    public int intX() {
        return (int) Math.floor(this.x);
    }

    public int intY() {
        return (int) Math.floor(this.y);
    }

    public int intZ() {
        return (int) Math.floor(this.z);
    }

    /**
     * Convert this Vec3 to a WorldVec, adding a given world.
     */
    public WorldVec withWorld(World world) {
        return new WorldVec(world, this.x, this.y, this.z);
    }

    /**
     * Writes this Vec3 to a byte buffer.
     *
     * @see readFromByteBuf
     */
    public void writeToByteBuf(ByteBuf buf) {
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }

    /**
     * Offsets this BlockVec in the given direction by the given amount.
     *
     * @param dir The direction to move in.
     * @param amt The amount to move by.
     *
     * @return this. This BlockVec is also modified.
     */
    public Vec3 offset(ForgeDirection dir, double amt) {
        this.x += dir.offsetX * amt;
        this.y += dir.offsetY * amt;
        this.z += dir.offsetZ * amt;
        return this;
    }

    /**
     * Offsets this BlockVec by one block in the given direction.
     *
     * @param dir The direction to move in.
     *
     * @return this. This BlockVec is also modified.
     */
    public Vec3 offset(ForgeDirection dir) {
        return this.offset(dir, 1);
    }

    /**
     * Moves the Vec3 by the given offset. Modifies this.
     */
    public Vec3 offset(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    /**
     * Moves the Vec3 by the given vector. Modifies this.
     */
    public Vec3 offset(Vec3 other) {
        return this.offset(other.x, other.y, other.z);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vec3 other = (Vec3) obj;
        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
            return false;
        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
            return false;
        if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Vec (" + this.x + " " + this.y + " " + this.z + ")";
    }
}
