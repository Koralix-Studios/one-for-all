package com.koralix.oneforall.client;

import com.koralix.oneforall.client.entry.OneForAllClient;
import com.koralix.oneforall.client.session.LoginManager;
import com.koralix.oneforall.client.settings.ClientSettings;
import com.koralix.oneforall.entry.OneForAll;

public class CoreClientInit implements OneForAllClient {
    @Override
    public void onInitializeClient() {
        OneForAll.load(ClientSettings.class);

        LoginManager.init();
    }
}
