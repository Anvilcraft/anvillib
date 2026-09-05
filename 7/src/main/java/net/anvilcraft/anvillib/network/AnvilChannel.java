package net.anvilcraft.anvillib.network;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.anvilcraft.alec.jalec.factories.AlecCriticalRuntimeErrorExceptionFactory;
import net.anvilcraft.anvillib.asm.ASMClassLoader;

public class AnvilChannel extends SimpleNetworkWrapper {
    private int nextID;

    public AnvilChannel(String channelName) {
        super(channelName);
    }

    @SuppressWarnings({ "unchecked", "ALEC" })
    public void register(Class<? extends IAnvilPacket> clazz) {
        AnvilPacket anno = clazz.getAnnotation(AnvilPacket.class);
        if (anno == null)
            throw AlecCriticalRuntimeErrorExceptionFactory.PLAIN.createAlecException(
                "Packet class", clazz, "is missing AnvilPacket annotation!"
            );

        String name
            = "net.anvilcraft.asm." + clazz.getName().replace('.', '_') + "#EventHandler";

        ClassWriter cw = new ClassWriter(0);
        cw.visit(
            V1_6,
            ACC_PUBLIC | ACC_SUPER,
            name.replace('.', '/'),
            null,
            "java/lang/Object",
            new String[] { Type.getInternalName(IMessageHandler.class) }
        );
        cw.visitSource("alechoefler.java", null);
        {
            MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            MethodVisitor mv = cw.visitMethod(
                ACC_PUBLIC,
                "onMessage",
                "(Lcpw/mods/fml/common/network/simpleimpl/IMessage;Lcpw/mods/fml/common/network/simpleimpl/MessageContext;)Lcpw/mods/fml/common/network/simpleimpl/IMessage;",
                null,
                null
            );
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitMethodInsn(
                INVOKEINTERFACE,
                "net/anvilcraft/anvillib/network/IAnvilPacket",
                "handle",
                "(Lcpw/mods/fml/common/network/simpleimpl/MessageContext;)V",
                true
            );
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(2, 3);
            mv.visitEnd();
        }
        cw.visitEnd();

        Class<?> handlerClass = ASMClassLoader.INSTANCE.define(name, cw.toByteArray());

        try {
            this.registerMessage(
                (IMessageHandler<IAnvilPacket, IMessage>) handlerClass.newInstance(),
                clazz,
                this.nextID++,
                anno.value()
            );
        } catch (Exception e) {
            throw AlecCriticalRuntimeErrorExceptionFactory.PLAIN
                .createAlecExceptionWithCause(e, "Failed to instantiate ASM'd class");
        }
    }
}
