package net.anvilcraft.anvillib.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.anvilcraft.anvillib.cosmetics.CosmeticsManager;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerEntityMixin {
    private static ResourceLocation ELYTRA = ResourceLocation.withDefaultNamespace("textures/entity/elytra.png");

    @Inject(method = "getSkin", at = @At("RETURN"), cancellable = true)
    private void getSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        AbstractClientPlayer self = (AbstractClientPlayer) (Object) this;
        PlayerSkin original = cir.getReturnValue();
        cir.setReturnValue(new PlayerSkin(
                original.texture(),
                original.textureUrl(),
                CosmeticsManager.getCape(self.getUUID()),
                ELYTRA,
                original.model(),
                original.secure()
        ));
    }
}
