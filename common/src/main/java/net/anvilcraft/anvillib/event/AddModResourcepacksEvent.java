package net.anvilcraft.anvillib.event;

import net.minecraft.server.packs.PackType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fired by AnvilLib when mod-bundled datapacks should be registered.
 *
 * <p>Register a listener via {@code Bus.MAIN.register(AddModDatapacksEvent.class, event -> ...)}
 * and call {@link #add} to include a datapack from your mod's JAR.
 *
 * <p>The pack must be located at {@code resourcepacks/<packId>/} within the mod's JAR,
 * containing a valid {@code pack.mcmeta} and a {@code data/} directory.
 */
public class AddModResourcepacksEvent {
    private final List<Entry> entries = new ArrayList<>();
    private final PackType type;

    public AddModResourcepacksEvent(PackType type) {
        this.type = type;
    }

    public PackType getType() {
        return type;
    }

    public void add(String modId, String packId) {
        entries.add(new Entry(modId, packId));
    }

    public List<Entry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public record Entry(String modId, String packId) {}
}
