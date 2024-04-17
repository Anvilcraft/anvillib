package dev.tilera.capes;

import com.jadarstudios.developercapes.cape.ICape;
import com.jadarstudios.developercapes.user.User;
import com.jadarstudios.developercapes.user.UserManager;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;

public class RenderEventHandler {
    @SubscribeEvent
    public void renderPlayer(RenderPlayerEvent.Specials.Pre event) {
        AbstractClientPlayer player = (AbstractClientPlayer) event.entityPlayer;

        UserManager manager = UserManager.getInstance();
        User user = manager.getUser(player.getUniqueID().toString());
        if (user == null) {
            player.func_152121_a(MinecraftProfileTexture.Type.CAPE, null);
            return;
        }

        ICape cape = user.capes.get(0);
        if (cape == null) {
            player.func_152121_a(MinecraftProfileTexture.Type.CAPE, null);
            return;
        }

        cape.loadTexture(player);
    }
}
