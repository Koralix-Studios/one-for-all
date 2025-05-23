package com.koralix.oneforall.client;

import com.koralix.oneforall.OneForAll;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OneForAll.LOGGER.info("Initializing {}-client v{}", OneForAll.MOD_ID, OneForAll.MOD_VERSION);

        for (OneForAllClient ofa : FabricLoader.getInstance().getEntrypoints("ofa-client", OneForAllClient.class)) {
            ofa.onInitializeClient();
        }
    }
}
