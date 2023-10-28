package net.anvilcraft.anvillib.cosmetics;

import java.util.UUID;
import java.util.function.Consumer;

public interface ICosmeticProvider {
    boolean requestsRefresh();

    void addCosmetics(UUID player, Consumer<ICosmetic> cosmeticAdder);
}
