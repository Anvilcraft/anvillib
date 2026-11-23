package net.anvilcraft.anvillib;

import java.io.File;

import cpw.mods.fml.relauncher.CoreModManager;
import net.anvilcraft.alec.jalec.factories.AlecCriticalRuntimeErrorExceptionFactory;
import net.anvilcraft.anvillib.earlyload.EarlyLoadEvent;
import net.anvilcraft.anvillib.earlyload.IEarlyLoadService;
import net.anvilcraft.anvillib.earlyload.RegisterTransformersEvent;
import net.anvilcraft.anvillib.event.Bus;
import net.anvilcraft.anvillib.event.IEventHandler;
import net.anvilcraft.anvillib.util.JarUtils;

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
                File coreModFile = JarUtils.getAnvillibJar();
                CoreModManager.getReparseableCoremods().add(coreModFile.getName());
                JarUtils.injectCoremod(coreModFile, "net.anvilcraft.anvillib.AnvilCore", true);
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
