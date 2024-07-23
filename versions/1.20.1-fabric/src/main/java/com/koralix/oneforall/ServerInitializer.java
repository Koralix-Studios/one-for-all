package com.koralix.oneforall;

import com.koralix.oneforall.platform.FabricPlatform;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ServerInitializer implements DedicatedServerModInitializer {
    private final ServerOneForAll instance = (ServerOneForAll) Initializer.instance;

    public static OneForAll instance() {
        return new ServerOneForAll(new FabricPlatform());
    }

    @Override
    public void onInitializeServer() {
        instance.onInitializeServer();
    }
}
