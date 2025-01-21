package net.anvilcraft.anvillib;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import io.github.tox1cozz.mixinbooterlegacy.IEarlyMixinLoader;

public class AnvilCore implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        List<String> mixins = new ArrayList<>();
        mixins.add("anvillib.mixins.json");
        return mixins;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"net.anvilcraft.anvillib.asm.ASMTransformer"};
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
