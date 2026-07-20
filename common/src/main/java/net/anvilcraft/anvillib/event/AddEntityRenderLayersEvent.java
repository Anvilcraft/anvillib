package net.anvilcraft.anvillib.event;

import java.util.Map;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.world.entity.player.Player;

public record
AddEntityRenderLayersEvent(Map<PlayerSkin.Model, EntityRenderer<? extends Player>> skinMap) {}
