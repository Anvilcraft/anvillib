package net.anvilcraft.anvillib.cosmetics.remote.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TextureData {
    @Expose
    public String url;
    @Expose
    @SerializedName("total_frames")
    public int frameCount;
}
