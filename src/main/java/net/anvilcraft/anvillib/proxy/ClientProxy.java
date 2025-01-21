package net.anvilcraft.anvillib.proxy;

import dev.tilera.capes.Capes;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        super.init();
        Capes.initCapes();
    }

    
}
