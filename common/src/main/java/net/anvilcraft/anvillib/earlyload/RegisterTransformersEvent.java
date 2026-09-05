package net.anvilcraft.anvillib.earlyload;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class RegisterTransformersEvent {
    private List<String> transformerClasses = new ArrayList<>();

    public void registerTransformer(String className) {
        transformerClasses.add(className);
    }

    public Stream<String> getTransformerClasses() {
        return transformerClasses.stream();
    }
}
