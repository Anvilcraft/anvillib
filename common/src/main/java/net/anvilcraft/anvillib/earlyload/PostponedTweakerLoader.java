package net.anvilcraft.anvillib.earlyload;

import java.io.File;
import java.util.List;

import net.anvilcraft.anvillib.event.Bus;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class PostponedTweakerLoader implements ITweaker {

    @SuppressWarnings("unchecked")
    public PostponedTweakerLoader() {
        List<ITweaker> canonicalTweakers = (List<ITweaker>) Launch.blackboard.get("Tweaks");
        List<ITweaker> bootstrappedTweakers = (List<ITweaker>) Launch.blackboard.get("net.anvilcraft.BootstrappedTweakers");
        canonicalTweakers.addAll(bootstrappedTweakers);
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        Bus.MAIN.register(AddTweakerEvent.class, e -> {
            e.getTweaker().acceptOptions(args, gameDir, assetsDir, profile);
            e.getTweaker().injectIntoClassLoader(Launch.classLoader);
        });
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        
    }

    @Override
    public String getLaunchTarget() {
        return null;
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
    
}
