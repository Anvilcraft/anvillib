package net.anvilcraft.anvillib.mixin;

import java.util.List;
import java.util.Set;

import net.anvilcraft.anvillib.Compat;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class AnvilLibMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {

    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.equals("net.anvilcraft.anvillib.mixin.accessor.AnimatedGeoModelAccessor")) {
            return Compat.hasGeckolib();
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(
        String targetClassName,
        org.objectweb.asm.tree.ClassNode targetClass,
        String mixinClassName,
        IMixinInfo mixinInfo
    ) {

    }

    @Override
    public void postApply(
        String targetClassName,
        org.objectweb.asm.tree.ClassNode targetClass,
        String mixinClassName,
        IMixinInfo mixinInfo
    ) {

    }

}
