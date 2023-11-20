package net.anvilcraft.anvillib.cosmetics;

import java.util.UUID;
import java.util.function.Consumer;

import net.minecraft.util.Identifier;

public interface ICosmeticProvider {
    boolean requestsRefresh();

    void addCosmetics(UUID player, Consumer<ICosmetic> cosmeticAdder);

    default Identifier getCape(UUID player) {
        return null;
    }
}
