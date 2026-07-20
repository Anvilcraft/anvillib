package net.anvilcraft.anvillib.cosmetics;

import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.resources.ResourceLocation;

public interface ICosmeticProvider {
    boolean requestsRefresh();

    void addCosmetics(UUID player, Consumer<ICosmetic> cosmeticAdder);

    default ResourceLocation getCape(UUID player) {
        return null;
    }
}
