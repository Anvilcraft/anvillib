package net.anvilcraft.anvillib.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import net.minecraft.launchwrapper.IClassTransformer;

public class ASMTransformer implements IClassTransformer {
    
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.client.resources.FileResourcePack")) {
            ClassReader reader = new ClassReader(basicClass);
            ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_FRAMES);
            reader.accept(new ClassVisitorFileResourcePack(writer), 0);
            return writer.toByteArray();
        }

        return basicClass;
    }

}
