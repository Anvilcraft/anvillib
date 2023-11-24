package net.anvilcraft.anvillib.cosmetics.remote.model;

import java.util.List;
import java.util.UUID;

import com.google.gson.annotations.Expose;

public class PlayerData {
    @Expose
    public UUID uuid;
    @Expose
    public List<String> cosmetics;
    @Expose
    public String cape;
}
