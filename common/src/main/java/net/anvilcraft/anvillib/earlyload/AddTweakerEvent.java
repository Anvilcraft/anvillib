package net.anvilcraft.anvillib.earlyload;

import net.minecraft.launchwrapper.ITweaker;

public class AddTweakerEvent {

    private ITweaker tweaker;
    private int priority;

    public AddTweakerEvent(ITweaker tweaker, int priority) {
        this.tweaker = tweaker;
        this.priority = priority;
    }

    public ITweaker getTweaker() {
        return tweaker;
    }

    public int getPriority() {
        return priority;
    }

}
