package net.anvilcraft.anvillib;

public class Compat {

    private static Boolean hasGeckolib = null;

    public static boolean hasGeckolib() {
        if (hasGeckolib == null) {
            try {
                Class.forName("software.bernie.geckolib.GeckoLib");
                hasGeckolib = true;
            } catch (ClassNotFoundException e) {
                hasGeckolib = false;
            }
        }
        return hasGeckolib;
    }

}
