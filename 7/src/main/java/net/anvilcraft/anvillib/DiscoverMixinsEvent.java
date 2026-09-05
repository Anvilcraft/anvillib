package net.anvilcraft.anvillib;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DiscoverMixinsEvent {

    private List<String> mixinConfigs = new ArrayList<>();

    public void addMixinConfig(String mixinConfig) {
        mixinConfigs.add(mixinConfig);
    }

    public Stream<String> getMixinConfigs() {
        return mixinConfigs.stream();
    }
    
}
