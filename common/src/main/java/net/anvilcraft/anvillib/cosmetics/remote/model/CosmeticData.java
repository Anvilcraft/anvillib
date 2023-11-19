package net.anvilcraft.anvillib.cosmetics.remote.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CosmeticData {
    
    @Expose
    public String id;
    @Expose
    @SerializedName("model_url")
    public String modelUrl;
    @Expose
    @SerializedName("animation_data")
    public AnimationData animationData;
    @Expose
    @SerializedName("texture_data")
    public TextureData textureData;

}
