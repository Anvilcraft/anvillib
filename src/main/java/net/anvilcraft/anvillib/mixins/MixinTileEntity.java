package net.anvilcraft.anvillib.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.anvilcraft.anvillib.event.TileLoadedEvent;
import net.anvilcraft.anvillib.event.TileUnloadedEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;

@Mixin(TileEntity.class)
public class MixinTileEntity {

    public boolean anvillibTileLoaded = false;
    
    @Inject(method = "updateEntity()V", at = @At("HEAD"), remap = false)
    private void onUpdate(CallbackInfo ci) {
        TileEntity self = (TileEntity)(Object)this;
        if (!this.anvillibTileLoaded && !self.getWorldObj().isRemote) {
            this.anvillibTileLoaded = true;
            MinecraftForge.EVENT_BUS.post(new TileLoadedEvent(self));
        }
    }

    @Inject(method = "invalidate()V", at = @At("HEAD"), remap = false)
    private void onInvalidate(CallbackInfo ci) {
        TileEntity self = (TileEntity)(Object)this;
        if (!self.getWorldObj().isRemote) {
            this.anvillibTileLoaded = false;
            MinecraftForge.EVENT_BUS.post(new TileUnloadedEvent(self));
        }
    }

    @Inject(method = "onChunkUnload()V", at = @At("HEAD"), remap = false)
    private void onOnChunkUnload(CallbackInfo ci) {
        TileEntity self = (TileEntity)(Object)this;
        if (!self.getWorldObj().isRemote) {
            this.anvillibTileLoaded = false;
            MinecraftForge.EVENT_BUS.post(new TileUnloadedEvent(self));
        }
    }

}
