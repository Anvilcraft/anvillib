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
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
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

    public static SimpleNetworkWrapper channel;

    @EventHandler
    public static void preInit(FMLPreInitializationEvent ev) {
        proxy.loadUserCache(UserCache.INSTANCE);
        UserCacheEventHandler uceh = new UserCacheEventHandler();
        MinecraftForge.EVENT_BUS.register(uceh);
        FMLCommonHandler.instance().bus().register(uceh);

        Runtime.getRuntime().addShutdownHook(
            new Thread(() -> proxy.saveUserCache(UserCache.INSTANCE))
        );

        channel = NetworkRegistry.INSTANCE.newSimpleChannel("anvillib");
        int pktid = 0;
        channel.registerMessage(
            PacketUpdateUserCache.Handler.class,
            PacketUpdateUserCache.class,
            pktid++,
            Side.CLIENT
        );
    }

    @EventHandler
    public static void init(FMLInitializationEvent ev) {}

    @EventHandler
    public static void shutdown(FMLServerStoppedEvent ev) {
        proxy.saveUserCache(UserCache.INSTANCE);
    }
}
