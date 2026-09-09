package net.anvilcraft.anvillib.api.types;

public interface IDirection {
    
    boolean isUp();

    boolean isDown();

    boolean isNorth();

    boolean isSouth();

    boolean isWest();

    boolean isEast();

    IDirection getOpposite();

    default int getXOffset() {
        if (this.isEast()) {
            return 1;
        } else if (this.isWest()) {
            return -1;
        } else {
            return 0;
        }
    }

    default int getYOffset() {
        if (this.isUp()) {
            return 1;
        } else if (this.isDown()) {
            return -1;
        } else {
            return 0;
        }
    }

    default int getZOffset() {
        if (this.isSouth()) {
            return 1;
        } else if (this.isNorth()) {
            return -1;
        } else {
            return 0;
        }
    }

}
