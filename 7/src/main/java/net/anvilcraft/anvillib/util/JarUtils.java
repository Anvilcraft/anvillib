package net.anvilcraft.anvillib.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import cpw.mods.fml.common.asm.transformers.ModAccessTransformer;
import cpw.mods.fml.relauncher.CoreModManager;
import net.anvilcraft.anvillib.api.types.Result;
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

    public static void addJar(File file) throws IOException {
        try(JarFile jar = new JarFile(file)) {
            Manifest manifest = jar.getManifest();
            if (manifest != null) {
                ModAccessTransformer.addJar(jar);
                
                Attributes attributes = manifest.getMainAttributes();

                Optional<String> tweakClass = Optional.ofNullable(attributes.getValue("TweakClass"));
                Optional<Integer> tweakOrder = Result
                    .ok(attributes.getValue("TweakOrder"))
                    .map(Integer::parseInt)
                    .toOptional();
                Optional<String> coreMod = Optional.ofNullable(attributes.getValue("FMLCorePlugin"));
                Optional<Boolean> containsMod = Result
                    .ok(attributes.getValue("FMLCorePluginContainsFMLMod"))
                    .map(Boolean::parseBoolean)
                    .toOptional();

                if (tweakClass.isPresent()) {
                    injectTweaker(file, jar, tweakOrder.orElse(0), tweakClass.get());
                    CoreModManager.getLoadedCoremods().add(file.getName());
                } else if (coreMod.isPresent()) {
                    injectCoremod(file, coreMod.get(), false);

                    if (containsMod.isPresent()) {
                        CoreModManager.getReparseableCoremods().add(file.getName());
                    } else {
                        CoreModManager.getLoadedCoremods().add(file.getName());
                    }
                }

            }
        } catch (Exception e) {
            throw new IOException(e);
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