package net.anvilcraft.anvillib.util;

public class AnvilUtil {
    public static <T extends Enum<T>> T enumFromInt(Class<T> clazz, int n) {
        T[] values = clazz.getEnumConstants();
        if (n < 0 || n >= values.length)
            return null;
        return values[n];
    }
}
