package net.anvilcraft.anvillib.util;

import java.util.Optional;

import net.anvilcraft.anvillib.api.convertible.IConverter;
import net.anvilcraft.anvillib.api.convertible.IConvertible;
import net.anvilcraft.anvillib.api.inject.Implementation;
import net.anvilcraft.anvillib.api.inject.Inject;
import net.anvilcraft.anvillib.api.types.Direction;
import net.anvilcraft.anvillib.api.types.IDirection;
import net.minecraftforge.common.util.ForgeDirection;

@Implementation (IConverter.class)
public class DirectionConverter implements IConverter<ForgeDirection> {

    @Inject(IConverter.class)
    public static IConverter<ForgeDirection> INSTANCE;

    @Override
    public Optional<ForgeDirection> convertFrom(Object input) {
        if (input instanceof ForgeDirection) {
            return Optional.of((ForgeDirection) input);
        } else if (input instanceof IDirection) {
            ForgeDirection side = ForgeDirection.UNKNOWN;
            IDirection dir = (IDirection) input;
            if (dir.isDown()) {
                side = ForgeDirection.DOWN;
            } else if (dir.isEast()) {
                side = ForgeDirection.EAST;
            } else if (dir.isNorth()) {
                side = ForgeDirection.NORTH;
            } else if (dir.isSouth()) {
                side = ForgeDirection.SOUTH;
            } else if (dir.isUp()) {
                side = ForgeDirection.UP;
            } else if (dir.isWest()) {
                side = ForgeDirection.WEST;
            }
            return Optional.of(side);
        } else if (input instanceof IConvertible) {
            return ((IConvertible) input).convertTo(ForgeDirection.class);
        } else {
            return Optional.empty();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E> Optional<E> convertTo(Class<E> clazz, ForgeDirection direction) {
        if (clazz == IDirection.class) {
            return Optional.of((E)direction);
        } else if (clazz == Direction.class) {
            Direction side;
            switch (direction) {
                case DOWN:
                    side = Direction.DOWN;
                    break;
                case EAST:
                    side = Direction.EAST;
                    break;
                case NORTH:
                    side = Direction.NORTH;
                    break;
                case SOUTH:
                    side = Direction.SOUTH;
                    break;
                case UP:
                    side = Direction.UP;
                    break;
                case WEST:
                    side = Direction.WEST;
                    break;
                default:
                    side = Direction.UNKNOWN;
                    break;
            }
            return Optional.of((E)side);
        }
        return Optional.empty();
    }
    
}
