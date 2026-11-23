package net.anvilcraft.anvillib.earlyload;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

import net.anvilcraft.alec.jalec.factories.AlecCriticalRuntimeErrorExceptionFactory;
import net.anvilcraft.anvillib.event.Bus;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class EarlyLoadTweaker implements ITweaker {

    private boolean hasLoadedClassloader = false;
    private List<String> postponedTweakClassed = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public EarlyLoadTweaker() {
        Optional<LaunchClassLoader> classLoader = Optional.ofNullable(Launch.classLoader);
        classLoader.ifPresent(lcl -> {
            lcl.addClassLoaderExclusion("net.anvilcraft.anvillib.earlyload.");
            lcl.addClassLoaderExclusion("net.anvilcraft.anvillib.event.Bus");
            lcl.addClassLoaderExclusion("net.anvilcraft.anvillib.event.IEventHandler");
            lcl.addClassLoaderExclusion("net.anvilcraft.anvillib.event.IEventBusRegisterable");
        });

        ServiceLoader<IEarlyLoadService> services = ServiceLoader.load(IEarlyLoadService.class);
        for (IEarlyLoadService service : services) {
            Bus.MAIN.register(service);
        }

        // I'm going first
        List<ITweaker> canonicalTweakers = (List<ITweaker>) Launch.blackboard.get("Tweaks");
        List<ITweaker> bootstrappedTweakers = new ArrayList<>(canonicalTweakers);
        Launch.blackboard.put("net.anvilcraft.BootstrappedTweakers", bootstrappedTweakers);
        canonicalTweakers.clear();
        bootstrappedTweakers.remove(this);
        List<String> tweakClasses = (List<String>) Launch.blackboard.get("TweakClasses");
        postponedTweakClassed.addAll(tweakClasses);
        for (int i = 0; i < tweakClasses.size(); i++) {
            tweakClasses.set(i, "net.anvilcraft.anvillib.earlyload.EarlyLoadTweaker");
        }

        Bus.MAIN.register(AddTweakerEvent.class, e -> bootstrappedTweakers.add(e.getTweaker()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        List<String> tweakClasses = (List<String>) Launch.blackboard.get("TweakClasses");
        tweakClasses.add("net.anvilcraft.anvillib.earlyload.PostponedTweakerLoader");
        tweakClasses.addAll(postponedTweakClassed);
        boolean isClient = args.contains("--uuid");
        Runnable gameStopper = () -> {
            try {
                Class<?> shutdownClass = Class.forName("java.lang.Shutdown");
                Method exitMethod = shutdownClass.getDeclaredMethod("exit", int.class);
                exitMethod.setAccessible(true);
                exitMethod.invoke(null, 0);
            } catch (Throwable e) {
                throw AlecCriticalRuntimeErrorExceptionFactory.PLAIN.createAlecExceptionWithCause(e, new Object[0]);
            }
        };
        EarlyLoadEvent event = new EarlyLoadEvent(gameDir, assetsDir, args, isClient, gameStopper);
        Bus.MAIN.fire(event);
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        if (hasLoadedClassloader) return;
        hasLoadedClassloader = true;
        RegisterTransformersEvent event = new RegisterTransformersEvent();
        Bus.MAIN.fire(event);
        event.getTransformerClasses().distinct().forEach(classLoader::registerTransformer);
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
