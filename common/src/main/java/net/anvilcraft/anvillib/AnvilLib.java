package net.anvilcraft.anvillib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.anvilcraft.anvillib.cosmetics.ClientEventHandler;
import net.anvilcraft.anvillib.event.Bus;
import software.bernie.geckolib3.GeckoLib;

public class AnvilLib {
    public static final String MODID = "anvillib";
    public static final Logger LOGGER = LogManager.getLogger();

    public static void initialize() {
        Bus.MAIN.register(new ClientEventHandler());

        GeckoLib.initialize();
    }
}
