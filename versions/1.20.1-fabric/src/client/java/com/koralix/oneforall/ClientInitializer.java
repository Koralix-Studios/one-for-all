package com.koralix.oneforall;

import com.koralix.oneforall.platform.FabricPlatform;
import net.fabricmc.api.ClientModInitializer;

public class ClientInitializer implements ClientModInitializer {
    private final ClientOneForAll instance = (ClientOneForAll) Initializer.instance;

    public static OneForAll instance() {
        return new ClientOneForAll(new FabricPlatform());
    }

    @Override
    public void onInitializeClient() {
        instance.onInitializeClient();
    }
}
