package net.anvilcraft.anvillib.world;

import java.util.function.Supplier;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.anvilcraft.anvillib.api.inject.Implementation;
import net.anvilcraft.anvillib.api.inject.Inject;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

@Implementation(Supplier.class)
public class WorldState implements Supplier<World> {
    
    @Inject(Supplier.class)
    public static Supplier<World> INSTANCE;
    private World currentWorld = null;

    private WorldState () {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public World get() {
        return this.currentWorld;
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load ev) {
        if (!ev.world.isRemote && ev.world.provider.dimensionId == 0) {
            this.currentWorld = ev.world;
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload ev) {
        if (!ev.world.isRemote && ev.world.provider.dimensionId == 0) {
            this.currentWorld = null;
        }
    }

}
