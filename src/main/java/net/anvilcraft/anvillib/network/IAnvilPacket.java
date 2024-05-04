package net.anvilcraft.anvillib.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

public interface IAnvilPacket extends IMessage {
    public void handle(MessageContext ctx);
}
