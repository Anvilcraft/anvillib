package net.anvilcraft.anvillib.event;

import java.util.Map;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.player.PlayerEntity;

public record
AddEntityRenderLayersEvent(Map<String, EntityRenderer<? extends PlayerEntity>> skinMap) {}
