package net.anvilcraft.anvillib.world;

import net.minecraft.world.biome.BiomeGenBase;

public class BiomeConflictManager {

    public static BiomeGenBase[] biomeCache = new BiomeGenBase[256];

    public static void hook(BiomeGenBase biome, int id) throws IllegalStateException {
        if (biomeCache.length > id) {
            if (biomeCache[id] != null && biomeCache[id] != biome) {
                StringBuilder builder = new StringBuilder();
                builder.append("Biome ID conflict: " + id + " : " + biome.biomeName + " : " + biomeCache[id].biomeName);
                builder.append("\nFree Biome IDs:");
                for (int i = 0; i < biomeCache.length; i++) {
                    if (biomeCache[i] == null) {
                        builder.append("\n");
                        builder.append(i);
                    }
                }
                throw new IllegalArgumentException(builder.toString());
            } else {
                biomeCache[id] = biome;
            }
        }
    }

}
