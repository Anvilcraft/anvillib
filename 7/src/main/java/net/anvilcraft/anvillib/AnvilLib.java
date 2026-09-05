package net.anvilcraft.anvillib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import net.anvilcraft.anvillib.ae2.AEIntegration;
import net.anvilcraft.anvillib.energy.RFEnergyUnit;
import net.anvilcraft.anvillib.energy.UEEnergyUnit;
import net.anvilcraft.anvillib.garbagecollection.GCManager;
import net.anvilcraft.anvillib.inject.InjectionHandler;
import net.anvilcraft.anvillib.inject.TargetDiscoverer;
import net.anvilcraft.anvillib.network.AnvilChannel;
import net.anvilcraft.anvillib.network.PacketUpdateUserCache;
import net.anvilcraft.anvillib.proxy.CommonProxy;
import net.anvilcraft.anvillib.registries.UnitRegistry;
import net.anvilcraft.anvillib.usercache.UserCacheEventHandler;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "anvillib", version = "{VERSION}", name = "AnvilLib", dependencies = "before:appliedenergistics2@[rv3-beta-26,)")
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
        UserCacheEventHandler uceh = new UserCacheEventHandler();
        MinecraftForge.EVENT_BUS.register(uceh);
        FMLCommonHandler.instance().bus().register(uceh);
        
        InjectionHandler injectionHandler = new InjectionHandler();
        TargetDiscoverer targetDiscoverer = new TargetDiscoverer(ev.getAsmData());
        targetDiscoverer.getInjectionTargets().forEach(injectionHandler::addTarget);
        targetDiscoverer.getSingletonImplementations().forEach(injectionHandler::addInstance);
        injectionHandler.populateModInstances();
        injectionHandler.initializeSingletons();
        injectionHandler.injectSelf();
        injectionHandler.injectSingletons();

        channel = new AnvilChannel("anvillib");
        channel.register(PacketUpdateUserCache.class);
    }

    @EventHandler
    public static void init(FMLInitializationEvent ev) {
        proxy.init();
        UnitRegistry.INSTANCE.register(new UEEnergyUnit());
        UnitRegistry.INSTANCE.register(new RFEnergyUnit());
        if (Loader.isModLoaded("appliedenergistics2")) {
            AEIntegration.load();
        }
    }

    @EventHandler
    public void onServerStop(FMLServerStoppedEvent ev) {
        GCManager.INSTANCE.unloadWorld();
    }
}
