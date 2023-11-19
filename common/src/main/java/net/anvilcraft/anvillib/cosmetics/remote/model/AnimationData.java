package net.anvilcraft.anvillib.cosmetics.remote.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AnimationData {
    
    @Expose
    public String url;
    @Expose
    @SerializedName("idle_animation")
    public String idleAnimation;

}
