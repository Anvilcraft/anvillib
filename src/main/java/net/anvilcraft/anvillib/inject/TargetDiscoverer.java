package net.anvilcraft.anvillib.inject;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.stream.Stream;

import cpw.mods.fml.common.discovery.ASMDataTable;
import net.anvilcraft.anvillib.api.inject.Implementation;
import net.anvilcraft.anvillib.api.inject.Inject;

public class TargetDiscoverer {

    ASMDataTable asmData;

    public TargetDiscoverer(ASMDataTable asmData) {
        this.asmData = asmData;
    }

    public Stream<ASMDataTable.ASMData> getASMData(Class<?> annotation) {
        return this.asmData.getAll(annotation.getCanonicalName()).stream();
    }

    public Stream<InjectionTarget> getInjectionTargets() {
        return this.getASMData(Inject.class)
            .map(this::map)
            .filter(Objects::nonNull);
    }

    public Stream<Class<?>> getSingletonImplementations() {
        return this.getASMData(Implementation.class)
            .map(asm -> asm.getClassName())
            .<Class<?>>map(cn -> {
                try {
                    return Class.forName(cn);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            })
            .filter(Objects::nonNull);
    }

    public InjectionTarget map(ASMDataTable.ASMData asm) {
        try {
            Class<?> clazz = Class.forName(asm.getClassName());
            Field field = clazz.getDeclaredField(asm.getObjectName());
            Inject inj = field.getDeclaredAnnotation(Inject.class);
            Type type = inj.value();
            return new InjectionTarget(type, clazz, field, inj.preferred());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
}