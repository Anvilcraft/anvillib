package net.anvilcraft.anvillib.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.tileentity.TileEntity;

public class TileUnloadedEvent extends Event {
    
    public TileEntity tile;

    public TileUnloadedEvent(TileEntity tile) {
        this.tile = tile;
    }

}
