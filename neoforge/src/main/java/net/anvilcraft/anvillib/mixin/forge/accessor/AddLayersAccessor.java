package net.anvilcraft.anvillib.mixin.forge.accessor;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.event.EntityRenderersEvent.AddLayers;

@Mixin(AddLayers.class)
public interface AddLayersAccessor {
    @Accessor(remap = false)
    public Map<String, EntityRenderer<? extends PlayerEntity>> getSkinMap();
}
