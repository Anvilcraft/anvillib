package net.anvilcraft.anvillib.usercache;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import net.anvilcraft.anvillib.AnvilLib;
import net.anvilcraft.anvillib.network.PacketUpdateUserCache;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.SaveHandler;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;

public class UserCacheEventHandler {
    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save ev) {
        AnvilLib.proxy.saveUserCache(UserCache.INSTANCE);
    }

    @SubscribeEvent
    public void onPlayerSave(PlayerEvent.SaveToFile ev) {
        ev.entityPlayer.getEntityData().setString(
            "anvillib:name", ev.entityPlayer.getCommandSenderName()
        );
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load ev) {
        if (ev.world.provider.dimensionId != 0
            || !(ev.world.getSaveHandler() instanceof SaveHandler))
            return;

        int loaded = 0;
        SaveHandler sh = (SaveHandler) ev.world.getSaveHandler();
        for (String playerdat : sh.getAvailablePlayerDat()) {
            try {
                File f = new File(sh.playersDirectory, playerdat + ".dat");

                UUID id = UUID.fromString(playerdat);
                NBTTagCompound tag
                    = CompressedStreamTools.readCompressed(new FileInputStream(f));

                String name = tag.getCompoundTag("ForgeData").getString("anvillib:name");
                if (!name.isEmpty()) {
                    UserCache.INSTANCE.addEntry(id, name);
                    loaded++;
                }
            } catch (Exception e) {
                continue;
            }
        }

        AnvilLib.LOGGER.info("Loaded {} usercache entries from player dat", loaded);
    }

    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public void onPlayerJoin(PlayerLoggedInEvent ev) {
        UserCache.INSTANCE.addEntry(ev.player);

        if (!(ev.player instanceof EntityPlayerMP)
            || ((EntityPlayerMP) ev.player).getPlayerIP().equals("local"))
            return;

        AnvilLib.channel.sendTo(
            new PacketUpdateUserCache(UserCache.INSTANCE.users),
            (EntityPlayerMP) ev.player
        );

        IMessage updatePkt = new PacketUpdateUserCache(
            ImmutableMap.of(ev.player.getUniqueID(), ev.player.getCommandSenderName())
        );
        for (EntityPlayerMP pl : (List<EntityPlayerMP>) MinecraftServer.getServer()
                                     .getConfigurationManager()
                                     .playerEntityList) {
            if (pl == ev.player)
                continue;

            AnvilLib.channel.sendTo(updatePkt, pl);
        }
    }
}
