package net.anvilcraft.anvillib.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarFile;

import cpw.mods.fml.relauncher.CoreModManager;
import net.anvilcraft.anvillib.earlyload.AddTweakerEvent;
import net.anvilcraft.anvillib.event.Bus;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class JarUtils {

    private static Class<?> pluginWrapperClass = null;
    private static Field sortIndexField = null;
    private static Method loadCoreMod = null;
    private static Method handleCascadingTweak = null;

    static {
        try {
            pluginWrapperClass = Class.forName("cpw.mods.fml.relauncher.CoreModManager$FMLPluginWrapper");
            sortIndexField = pluginWrapperClass.getDeclaredField("sortIndex");
            sortIndexField.setAccessible(true);
            loadCoreMod = CoreModManager.class.getDeclaredMethod("loadCoreMod", LaunchClassLoader.class, String.class, File.class);
            loadCoreMod.setAccessible(true);
            handleCascadingTweak = CoreModManager.class.getDeclaredMethod("handleCascadingTweak", File.class, JarFile.class, String.class, LaunchClassLoader.class, Integer.class);
            handleCascadingTweak.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void injectTweaker(File file, JarFile jar, int order, String tweakClass) throws ReflectiveOperationException, IllegalArgumentException {
        handleCascadingTweak.invoke(null, file, jar, tweakClass, Launch.classLoader, order);
    }

    public static void injectCoremod(File file, String coremodClass, boolean force) throws ReflectiveOperationException, IllegalArgumentException {
        String fileName = file.getName();
        if (force) {
            CoreModManager.getLoadedCoremods().remove(fileName);
        }
        if (CoreModManager.getLoadedCoremods().contains(fileName)) return;

        Object ret = loadCoreMod.invoke(null, Launch.classLoader, coremodClass, file);

        if (pluginWrapperClass.isInstance(ret)) {
            Integer sortIndex = (Integer) sortIndexField.get(ret);
            Bus.MAIN.fire(new AddTweakerEvent((ITweaker) ret, sortIndex));
        }
    }

    public static File getAnvillibJar() throws IOException, URISyntaxException {
        JarURLConnection connection = (JarURLConnection) JarUtils.class.getProtectionDomain().getCodeSource().getLocation().openConnection();
        URL jarFile = connection.getJarFileURL();
        return new File(jarFile.toURI());
    }
    
}