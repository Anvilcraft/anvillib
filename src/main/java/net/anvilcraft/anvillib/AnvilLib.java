package net.anvilcraft.anvillib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import software.bernie.geckolib3.GeckoLib;

@Mod(AnvilLib.MODID)
public class AnvilLib {
    public static final String MODID = "anvillib";
    public static final Logger LOGGER = LogManager.getLogger();

    public AnvilLib() {
        GeckoLib.initialize();
    }
}
