package net.anvilcraft.anvillib;

import net.minecraftforge.fml.common.Mod;

@Mod(AnvilLib.MODID)
public class AnvilLibForge {
    public AnvilLibForge() {
        AnvilLib.initialize();
    }
}
