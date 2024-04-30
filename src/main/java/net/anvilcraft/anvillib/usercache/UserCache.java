package net.anvilcraft.anvillib.usercache;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

/**
 * A singleton data structure that serves as a lookup table from user UUIDs to their usernames.
 * It is serialized on the client and in worlds and is synched to the client upon joining a server.
 */
public class UserCache {
    public static final UserCache INSTANCE = new UserCache();
    public Map<UUID, String> users = new HashMap<>();

    /**
     * Add a new entry to the usercache.
     *
     * @param id The UUID of the player.
     * @param name The name of the player.
     */
    public void addEntry(UUID id, String name) {
        this.users.put(id, name);
    }

    /**
     * Add a player to the usercache.
     *
     * @param player Player Entity to add.
     */
    public void addEntry(EntityPlayer player) {
        this.addEntry(player.getUniqueID(), player.getCommandSenderName());
    }

    /**
     * Gets a username given the user's UUID.
     *
     * @param id The UUID of the player.
     *
     * @return The player's Username or null if it is not known.
     */
    public String getCached(UUID id) {
        return this.users.get(id);
    }
}
