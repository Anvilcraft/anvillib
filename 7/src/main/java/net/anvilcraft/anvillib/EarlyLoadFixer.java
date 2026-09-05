package net.anvilcraft.anvillib;

import java.io.File;
import java.lang.reflect.Method;

import cpw.mods.fml.relauncher.CoreModManager;
import net.anvilcraft.alec.jalec.factories.AlecCriticalRuntimeErrorExceptionFactory;
import net.anvilcraft.anvillib.earlyload.EarlyLoadEvent;
import net.anvilcraft.anvillib.earlyload.IEarlyLoadService;
import net.anvilcraft.anvillib.earlyload.RegisterTransformersEvent;
import net.anvilcraft.anvillib.event.Bus;
import net.anvilcraft.anvillib.event.IEventHandler;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class EarlyLoadFixer implements IEarlyLoadService {

    @Override
    public void registerEventHandlers(Bus bus) {
        bus.register(EarlyLoadEvent.class, new EarlyLoadHandler());
        bus.register(RegisterTransformersEvent.class, (ev) -> {
            ev.registerTransformer("net.anvilcraft.anvillib.asm.ASMTransformer");
        });
    }

    static class EarlyLoadHandler implements IEventHandler<EarlyLoadEvent> {

        @Override
        public void accept(EarlyLoadEvent ev) {
            try {
                String uri = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().toString();
                String jarFile = CoreModManager.getLoadedCoremods().stream().filter(jar -> uri.contains(jar)).findFirst().get();
                CoreModManager.getLoadedCoremods().remove(jarFile);
                CoreModManager.getReparseableCoremods().add(jarFile);
                File coreModFile = new File(jarFile);
                Method loadCoreMod = CoreModManager.class.getDeclaredMethod("loadCoreMod", LaunchClassLoader.class, String.class, File.class);
                loadCoreMod.setAccessible(true);
                loadCoreMod.invoke(null, Launch.classLoader, "net.anvilcraft.anvillib.AnvilCore", coreModFile);
            } catch (Exception e) {
                throw AlecCriticalRuntimeErrorExceptionFactory.PLAIN.createAlecExceptionWithCause(e, new Object[0]);
            }
        }

        @Override
        public int order() {
            return Integer.MAX_VALUE;
        }

    }
    
}
