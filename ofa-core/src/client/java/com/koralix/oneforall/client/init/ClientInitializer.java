package com.koralix.oneforall.client.init;

import com.koralix.oneforall.client.entry.OneForAllClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        for (OneForAllClient ofa : FabricLoader.getInstance().getEntrypoints("ofa:client", OneForAllClient.class)) {
            ofa.onInitializeClient();
        }
    }
}
