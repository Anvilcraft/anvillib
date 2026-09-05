package net.anvilcraft.anvillib.asm;

/**
 * A simple wrapper around the current ClassLoader to allow defining classes manually.
 */
public class ASMClassLoader extends ClassLoader {
    public static final ASMClassLoader INSTANCE = new ASMClassLoader();

    private ASMClassLoader() {
        super(ASMClassLoader.class.getClassLoader());
    }

    public Class<?> define(String name, byte[] code) {
        return super.defineClass(name, code, 0, code.length);
    }
}
