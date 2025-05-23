package com.koralix.oneforall.base.client;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.client.OneForAllClient;

public class ClientInitializer implements OneForAllClient {
    @Override
    public void onInitializeClient() {
        OneForAll.LOGGER.info("Client initialized successfully.");
    }
}
