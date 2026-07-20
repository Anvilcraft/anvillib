package net.anvilcraft.anvillib.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.anvilcraft.anvillib.cosmetics.ClientEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    @Inject(at = @At("RETURN"), method = "<init>")
    public void init(GameConfig args, CallbackInfo info) {
        ClientEventHandler.registerRemoteCosmetics(args.location.assetDirectory);
    }
}
