package net.anvilcraft.anvillib.api.types;

import java.util.Optional;

import net.anvilcraft.anvillib.api.convertible.IConverter;
import net.anvilcraft.anvillib.api.convertible.IConvertible;

public enum Direction implements IDirection {
    
    DOWN,
    UP,
    NORTH,
    SOUTH,
    WEST,
    EAST,
    UNKNOWN;

    public static Direction from(Object obj) {
        return Optional.ofNullable(obj)
            .filter(o -> o instanceof IConvertible)
            .map(o -> (IConvertible) o)
            .flatMap(c -> c.convertTo(Direction.class))
            .orElse(UNKNOWN);
    }

    public <T> Optional<T> to(IConverter<T> converter) {
        return converter.convertFrom(this);
    }

    @Override
    public boolean isUp() {
        return this == UP;
    }

    @Override
    public boolean isDown() {
        return this == DOWN;
    }

    @Override
    public boolean isNorth() {
        return this == NORTH;
    }

    @Override
    public boolean isSouth() {
        return this == SOUTH;
    }

    @Override
    public boolean isWest() {
        return this == WEST;
    }

    @Override
    public boolean isEast() {
        return this == EAST;
    }

    @Override
    public IDirection getOpposite() {
        switch (this) {
            case DOWN: return UP;
            case EAST: return WEST;
            case NORTH: return SOUTH;
            case SOUTH: return NORTH;
            case UP: return DOWN;
            case WEST: return EAST;
            default: return UNKNOWN;
        }
    }

}
