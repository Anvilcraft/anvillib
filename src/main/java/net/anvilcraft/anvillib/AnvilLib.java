package net.anvilcraft.anvillib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import net.anvilcraft.anvillib.network.AnvilChannel;
import net.anvilcraft.anvillib.network.PacketUpdateUserCache;
import net.anvilcraft.anvillib.proxy.CommonProxy;
import net.anvilcraft.anvillib.usercache.UserCache;
import net.anvilcraft.anvillib.usercache.UserCacheEventHandler;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "anvillib", version = "{VERSION}", name = "AnvilLib")
public class AnvilLib {
    public static final Logger LOGGER = LogManager.getLogger("AnvilLib");

    @SidedProxy(
        modId = "anvillib",
        serverSide = "net.anvilcraft.anvillib.proxy.CommonProxy",
        clientSide = "net.anvilcraft.anvillib.proxy.ClientProxy"
    )
    public static CommonProxy proxy;

    public static AnvilChannel channel;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent ev) {
        proxy.loadUserCache(UserCache.INSTANCE);
        UserCacheEventHandler uceh = new UserCacheEventHandler();
        MinecraftForge.EVENT_BUS.register(uceh);
        FMLCommonHandler.instance().bus().register(uceh);

        Runtime.getRuntime().addShutdownHook(
            new Thread(() -> proxy.saveUserCache(UserCache.INSTANCE))
        );

        channel = new AnvilChannel("anvillib");
        channel.register(PacketUpdateUserCache.class);
    }

    @EventHandler
    public static void init(FMLInitializationEvent ev) {
        proxy.init();
    }

    @EventHandler
    public static void shutdown(FMLServerStoppedEvent ev) {
        proxy.saveUserCache(UserCache.INSTANCE);
    }
}
