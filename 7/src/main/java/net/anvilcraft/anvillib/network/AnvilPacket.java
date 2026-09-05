package net.anvilcraft.anvillib.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cpw.mods.fml.relauncher.Side;

/**
 * This annotation must be present on every AnvilLib packet to declare metadata about it.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AnvilPacket {
    /**
     * The side the packet is sent to.
     */
    public Side value();
}
