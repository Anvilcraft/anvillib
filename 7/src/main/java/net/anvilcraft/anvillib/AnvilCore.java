package net.anvilcraft.anvillib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import io.github.tox1cozz.mixinbooterlegacy.IEarlyMixinLoader;
import net.anvilcraft.anvillib.event.Bus;

public class AnvilCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        DiscoverMixinsEvent event = new DiscoverMixinsEvent();
        Bus.MAIN.fire(event);
        List<String> mixins = new ArrayList<>();
        event.getMixinConfigs().forEach(mixins::add);
        mixins.add("anvillib.mixins.json");
        return mixins;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return "net.anvilcraft.anvillib.AnvilCore$Container";
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

     public static class Container extends DummyModContainer {

        public Container() {
            super(new ModMetadata());
            ModMetadata meta = getMetadata();
            meta.modId = "anvillib-core";
            meta.name = "AnvilLib Core Mod";
            meta.version = "1.0.0";
        }

    }
    
}
