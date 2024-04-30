package net.anvilcraft.anvillib.proxy;

import net.anvilcraft.anvillib.usercache.ClientCacheManager;
import net.anvilcraft.anvillib.usercache.UserCache;

public class ClientProxy extends CommonProxy {
    @Override
    public void loadUserCache(UserCache cache) {
        ClientCacheManager.loadCacheInto(cache);
    }

    @Override
    public void saveUserCache(UserCache cache) {
        ClientCacheManager.serializeCache(cache);
    }
}
