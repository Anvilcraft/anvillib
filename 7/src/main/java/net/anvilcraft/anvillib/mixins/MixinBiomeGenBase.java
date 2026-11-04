package net.anvilcraft.anvillib.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.anvilcraft.anvillib.world.BiomeConflictManager;
import net.minecraft.world.biome.BiomeGenBase;

@Mixin(BiomeGenBase.class)
public class MixinBiomeGenBase {

    @Inject(method = "<init>(IZ)V", at = @At("TAIL"), remap = false)
    public void constructorHead(int id, boolean register, CallbackInfo ci) {
       if (register) {
          BiomeConflictManager.hook((BiomeGenBase)(Object)this, id);
       }
    }
    
}
