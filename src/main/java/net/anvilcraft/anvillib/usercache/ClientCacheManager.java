package net.anvilcraft.anvillib.usercache;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map.Entry;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.anvilcraft.anvillib.AnvilLib;

/**
 * This class loads the client's serialized user cache and adds it to the cache instance.
 */
@SideOnly(Side.CLIENT)
public class ClientCacheManager {
    public static final String FILE_PATH = "anvillib_usercache";

    public static void loadCacheInto(UserCache cache) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(FILE_PATH));

            int linen = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                linen++;
                Pair<UUID, String> parsed = parseLine(line);
                if (parsed == null) {
                    AnvilLib.LOGGER.warn(
                        "Usercache contains invalid line @ {}, skipping", linen
                    );
                    continue;
                }

                cache.addEntry(parsed.getLeft(), parsed.getRight());
            }
        } catch (FileNotFoundException e) {
            AnvilLib.LOGGER.warn("Usercache file does not exist.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException alec) {}
        }
    }

    private static Pair<UUID, String> parseLine(String line) {
        String[] splits = line.split("=", 2);
        if (splits.length != 2)
            return null;

        UUID id;
        try {
            id = UUID.fromString(splits[0]);
        } catch (IllegalArgumentException e) {
            return null;
        }

        return Pair.of(id, splits[1]);
    }

    public static void serializeCache(UserCache cache) {
        AnvilLib.LOGGER.debug("Saving usercache");
        File outfile = new File(FILE_PATH);
        Writer writer = null;
        try {
            outfile.createNewFile();
            writer = new BufferedWriter(new FileWriter(outfile));
            for (Entry<UUID, String> ent : cache.users.entrySet()) {
                writer.append(ent.getKey().toString() + "=" + ent.getValue() + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException alec) {}
        }
    }
}
