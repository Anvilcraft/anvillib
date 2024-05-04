package net.anvilcraft.anvillib.network;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import net.anvilcraft.alec.jalec.AlecLogger;
import net.anvilcraft.anvillib.usercache.UserCache;

@AnvilPacket(Side.CLIENT)
public class PacketUpdateUserCache implements IAnvilPacket {
    public Map<UUID, String> entries;

    public PacketUpdateUserCache(Map<UUID, String> entries) {
        this.entries = entries;
    }

    public PacketUpdateUserCache() {}

    @Override
    public void toBytes(ByteBuf buf) {
        for (Entry<UUID, String> ent : this.entries.entrySet()) {
            buf.writeLong(ent.getKey().getMostSignificantBits());
            buf.writeLong(ent.getKey().getLeastSignificantBits());

            buf.writeInt(ent.getValue().length());
            buf.writeBytes(ent.getValue().getBytes());
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entries = new HashMap<>();

        while (buf.readableBytes() > 0) {
            UUID id = new UUID(buf.readLong(), buf.readLong());

            byte[] nameBytes = new byte[buf.readInt()];
            buf.readBytes(nameBytes);

            String name = new String(nameBytes);

            this.entries.put(id, name);
        }
    }

    @Override
    public void handle(MessageContext ctx) {
        UserCache.INSTANCE.users.putAll(this.entries);
        AlecLogger.PLAIN.alec("AAAAALEEEEEC");
    }
}
