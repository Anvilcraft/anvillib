package net.anvilcraft.anvillib.client;

import org.joml.Vector3f;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

public class RecoloredFluidExtension implements IClientFluidTypeExtensions {

    ResourceLocation stillTexture;
    ResourceLocation flowingTexture;
    int tintColor = 0xFFFFFFFF;
    
    public RecoloredFluidExtension(ResourceLocation stillTexture, ResourceLocation flowingTexture, int tintColor) {
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.tintColor = tintColor;
    }

    public RecoloredFluidExtension(ResourceLocation stillTexture, int tintColor) {
        this.stillTexture = stillTexture;
        this.flowingTexture = stillTexture;
        this.tintColor = tintColor;
    }

    public RecoloredFluidExtension(ResourceLocation stillTexture, ResourceLocation flowingTexture) {
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
    }

    public RecoloredFluidExtension(ResourceLocation stillTexture) {
        this.stillTexture = stillTexture;
        this.flowingTexture = stillTexture;
    }

    @Override
    public ResourceLocation getFlowingTexture() {
        return this.flowingTexture;
    }

    @Override
    public ResourceLocation getStillTexture() {
        return this.stillTexture;
    }

    @Override
    public int getTintColor() {
        return this.tintColor;
    }

    @Override
    public Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
        float r = ((getTintColor() >> 16) & 0xFF) / 255f;
        float g = ((getTintColor() >> 8) & 0xFF) / 255f;
        float b = (getTintColor() & 0xFF) / 255f;
        return new Vector3f(r, g, b);
    }
    
}
