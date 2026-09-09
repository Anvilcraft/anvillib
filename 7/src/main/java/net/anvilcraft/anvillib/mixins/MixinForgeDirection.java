package net.anvilcraft.anvillib.mixins;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;

import net.anvilcraft.anvillib.api.convertible.IConvertible;
import net.anvilcraft.anvillib.api.types.IDirection;
import net.anvilcraft.anvillib.util.DirectionConverter;
import net.minecraftforge.common.util.ForgeDirection;

@Mixin(ForgeDirection.class)
public abstract class MixinForgeDirection implements IDirection, IConvertible {

    @Override
    public <T> Optional<T> convertTo(Class<T> target) {
        return DirectionConverter.INSTANCE.convertTo(target, (ForgeDirection)(Object)this);
    }

    @Override
    public IDirection getOpposite() {
        return (IDirection)(Object)((ForgeDirection)(Object)this).getOpposite();
    }

    @Override
    public boolean isDown() {
        return this.equals(ForgeDirection.DOWN);
    }

    @Override
    public boolean isEast() {
        return this.equals(ForgeDirection.EAST);
    }

    @Override
    public boolean isNorth() {
        return this.equals(ForgeDirection.NORTH);
    }

    @Override
    public boolean isSouth() {
        return this.equals(ForgeDirection.SOUTH);
    }

    @Override
    public boolean isUp() {
        return this.equals(ForgeDirection.UP);
    }

    @Override
    public boolean isWest() {
        return this.equals(ForgeDirection.WEST);
    }
    
}
