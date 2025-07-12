package com.koralix.oneforall.base.client;

import com.koralix.oneforall.base.BaseInit;
import com.koralix.oneforall.base.client.settings.ClientSettings;
import com.koralix.oneforall.client.entry.OneForAllClient;
import com.koralix.oneforall.entry.OneForAll;

public class BaseClientInit implements OneForAllClient {
    @Override
    public void onInitializeClient() {
        BaseInit.logger().info("Client initialized successfully.");

        OneForAll.load(ClientSettings.class);
    }
}
