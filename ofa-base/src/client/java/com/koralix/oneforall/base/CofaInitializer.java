package com.koralix.oneforall.base;

import com.koralix.oneforall.OneForAll;

public class CofaInitializer implements net.fabricmc.api.ClientModInitializer  {
    @Override
    public void onInitializeClient() {
        OneForAll.LOGGER.info("Client initialized successfully.");
    }
}
