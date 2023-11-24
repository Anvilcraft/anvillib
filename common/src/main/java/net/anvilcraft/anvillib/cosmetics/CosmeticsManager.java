package net.anvilcraft.anvillib.cosmetics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.Identifier;
import software.bernie.geckolib3.file.AnimationFile;
import software.bernie.geckolib3.geo.render.built.GeoModel;

public class CosmeticsManager {
    private static List<ICosmeticProvider> providers = new ArrayList<>();
    private static Map<UUID, List<ICosmetic>> cosmeticCache = new HashMap<>();
    private static Map<UUID, Identifier> capeCache = new HashMap<>();
    private static Set<UUID> activePlayers = new HashSet<>();
    private static Map<Identifier, GeoModel> cachedModels = new ConcurrentHashMap<>();
    private static Map<Identifier, AnimationFile> cachedAnimations
        = new ConcurrentHashMap<>();

    private static void refresh() {
        boolean doRefresh = false;
        for (ICosmeticProvider provider : providers) {
            doRefresh = doRefresh || provider.requestsRefresh();
        }
        if (!doRefresh)
            return;
        cosmeticCache.clear();
        for (UUID uuid : activePlayers) {
            loadPlayer(uuid);
        }
    }

    private static void loadPlayer(UUID player) {
        if (cosmeticCache.containsKey(player))
            return;
        cosmeticCache.put(player, new ArrayList<>());
        List<ICosmetic> cosmetics = cosmeticCache.get(player);
        for (ICosmeticProvider provider : providers) {
            provider.addCosmetics(player, (cosmetic) -> cosmetics.add(cosmetic));
            if (!capeCache.containsKey(player)) {
                Identifier cape = provider.getCape(player);
                if (cape != null)
                    capeCache.put(player, cape);
            }
        }
    }

    public static void registerProvider(ICosmeticProvider provider) {
        providers.add(provider);
    }

    protected static List<ICosmetic> getCosmeticsForPlayer(UUID uuid) {
        if (!activePlayers.contains(uuid)) {
            activePlayers.add(uuid);
            loadPlayer(uuid);
        }
        refresh();
        return cosmeticCache.get(uuid);
    }

    public static Identifier getCape(UUID player) {
        if (!activePlayers.contains(player)) {
            activePlayers.add(player);
            loadPlayer(player);
        }
        refresh();
        return capeCache.get(player);
    }

    protected static GeoModel getModel(Identifier id) {
        return cachedModels.get(id);
    }

    protected static AnimationFile getAnimations(Identifier id) {
        return cachedAnimations.get(id);
    }

    public static void loadModel(Identifier id, GeoModel model) {
        cachedModels.put(id, model);
    }

    public static void loadAnimations(Identifier id, AnimationFile animations) {
        cachedAnimations.put(id, animations);
    }
}
