package net.anvilcraft.anvillib.asm;

import java.util.ArrayList;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassVisitorFileResourcePack extends ClassVisitor {
    public ClassVisitorFileResourcePack(ClassWriter writer) {
        super(Opcodes.ASM5, writer);
    }

    public static String transformPathCallback(String path) {
        ArrayList<String> segments = new ArrayList<>();
        for (String sub : path.split("/")) {
            if (sub.equals("..") && segments.size() > 0)
                segments.remove(segments.size() - 1);
            else
                segments.add(sub);
        }

        return String.join("/", segments);
    }

    @Override
    public MethodVisitor visitMethod(
        int access, String name, String desc, String signature, String[] exceptions
    ) {
        MethodVisitor vis = super.visitMethod(access, name, desc, signature, exceptions);

        if (name.equals("a") || name.equals("getInputStreamByName") || name.equals("b")
            || name.equals("hasResourceName")) {
            return new MethodVisitorTransformPath(vis);
        }

        return vis;
    }

    public static class MethodVisitorTransformPath extends MethodVisitor {
        public MethodVisitorTransformPath(MethodVisitor mv) {
            super(Opcodes.ASM5, mv);
        }

        @Override
        public void visitCode() {
            super.visitCode();
            super.visitVarInsn(Opcodes.ALOAD, 1);
            super.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "net/anvilcraft/anvillib/asm/ClassVisitorFileResourcePack",
                "transformPathCallback",
                "(Ljava/lang/String;)Ljava/lang/String;",
                false
            );
            super.visitVarInsn(Opcodes.ASTORE, 1);
        }
    }
}
