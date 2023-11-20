package net.anvilcraft.anvillib.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;

import net.anvilcraft.anvillib.cosmetics.ClientEventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(at = @At("RETURN"), method = "<init>")
	public void init(RunArgs args, CallbackInfo info) {
		ClientEventHandler.registerRemoteCosmetics(args.directories.assetDir);
	}
}
