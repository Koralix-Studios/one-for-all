package com.koralix.oneforall;

import com.koralix.oneforall.network.ServerLoginManager;
import net.fabricmc.api.DedicatedServerModInitializer;

public class ServerInitializer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        OneForAll.LOGGER.info("Initializing OneForAll server...");

        ServerLoginManager.init();
    }
}
