package net.anvilcraft.anvillib.earlyload;

import java.io.File;
import java.lang.reflect.Method;
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

        classLoader.ifPresent(this::injectIntoClassLoader);
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
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
