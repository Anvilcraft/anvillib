package net.anvilcraft.anvillib.event;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.tileentity.TileEntity;

public class TileLoadedEvent extends Event {
    
    public TileEntity tile;

    public TileLoadedEvent(TileEntity tile) {
        this.tile = tile;
    }

}
