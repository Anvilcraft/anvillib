package net.anvilcraft.anvillib.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.anvilcraft.anvillib.event.AddEntityRenderLayersEvent;
import net.anvilcraft.anvillib.event.Bus;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourceManager;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin {
    @Shadow
    private Map<String, EntityRenderer<? extends PlayerEntity>> modelRenderers;

    @Inject(method = "reload", at = @At("TAIL"))
    public void onReload(ResourceManager alec, CallbackInfo ci) {
        Bus.MAIN.fire(new AddEntityRenderLayersEvent(this.modelRenderers));
    }
}
