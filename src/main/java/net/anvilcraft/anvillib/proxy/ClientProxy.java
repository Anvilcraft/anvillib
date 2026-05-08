package net.anvilcraft.anvillib.proxy;

import dev.tilera.capes.Capes;
import net.anvilcraft.anvillib.resources.SettingsLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IReloadableResourceManager;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        super.init();
        Capes.initCapes();
        ((IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager()).registerReloadListener(new SettingsLoader());
    }
}
