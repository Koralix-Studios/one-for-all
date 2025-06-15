package com.koralix.oneforall.base.client;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.base.client.settings.ClientSettings;
import com.koralix.oneforall.client.OneForAllClient;
import com.koralix.oneforall.config.loader.ConfigLoader;
import com.koralix.oneforall.config.loader.Storages;

public class ClientInitializer implements OneForAllClient {
    @Override
    public void onInitializeClient() {
        OneForAll.logger().info("Client initialized successfully.");

        ConfigLoader loader = Storages.CLIENT.create(OneForAll.id());
        ClientSettings.register().save(loader);
    }
}
