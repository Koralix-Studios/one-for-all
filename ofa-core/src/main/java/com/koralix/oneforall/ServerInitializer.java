package com.koralix.oneforall;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ServerInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        for (OneForAllDedicated ofa : FabricLoader.getInstance().getEntrypoints("ofa-server", OneForAllDedicated.class)) {
            ofa.onInitializeDedicated();
        }
    }
}
