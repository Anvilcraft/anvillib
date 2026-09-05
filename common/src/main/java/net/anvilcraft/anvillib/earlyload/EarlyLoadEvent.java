package net.anvilcraft.anvillib.earlyload;

import java.io.File;
import java.util.List;

public class EarlyLoadEvent {
    
    public final File gameDir;
    public final File assetDir;
    public final List<String> args;
    public final boolean isClient;
    public final Runnable stopGame;

    public EarlyLoadEvent(File gameDir, File assetDir, List<String> args, boolean isClient, Runnable stopGame) {
        this.gameDir = gameDir;
        this.assetDir = assetDir;
        this.args = args;
        this.isClient = isClient;
        this.stopGame = stopGame;
    }

}
