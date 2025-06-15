package com.koralix.oneforall.client;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.client.session.LoginManager;
import com.koralix.oneforall.client.settings.ClientSettings;
import com.koralix.oneforall.config.loader.ConfigLoader;
import com.koralix.oneforall.config.loader.Storages;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OneForAll.logger().info("Initializing {}-client v{}", OneForAll.id(), OneForAll.version());

        ConfigLoader loader = Storages.CLIENT.create(OneForAll.id());
        ClientSettings.register().save(loader);

        for (OneForAllClient ofa : FabricLoader.getInstance().getEntrypoints("ofa-client", OneForAllClient.class)) {
            ofa.onInitializeClient();
        }

        LoginManager.init();
    }
}
