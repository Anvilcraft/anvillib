package dev.tilera.capes;

import java.io.InputStream;
import java.net.URL;

import com.jadarstudios.developercapes.DevCapes;

import org.apache.commons.io.IOUtils;

import net.minecraftforge.common.MinecraftForge;

public class Capes {
    
    private static boolean capesInitialized = false;

    public static void initCapes() {
        if (!capesInitialized) {
            capesInitialized = true;
            try {
                URL index = new URL("https://capes.tilera.xyz/devcapes/index");
                InputStream stream = DevCapes.getInstance().getStreamForURL(index);
                String capeConfig = IOUtils.toString(stream);
                stream.close();
                DevCapes.getInstance().registerConfig(
                    capeConfig
                );
                MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
            } catch (Exception e) {
                System.out.print("Cant load capes\n" + e);
            }
        }
    }

}
