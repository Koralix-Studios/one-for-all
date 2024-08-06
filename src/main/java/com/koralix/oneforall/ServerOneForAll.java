package com.koralix.oneforall;

import com.koralix.oneforall.network.ServerLoginManager;
import com.koralix.oneforall.platform.Platform;
import com.koralix.oneforall.settings.ServerSettings;
import com.koralix.oneforall.settings.SettingsManager;

public class ServerOneForAll extends OneForAll {

    public ServerOneForAll(Platform platform) {
        super(platform);
    }

    public static ServerOneForAll getInstance() {
        return (ServerOneForAll) OneForAll.getInstance();
    }

    @Override
    public void onInitialize() {
        getLogger().info("Initializing OneForAll...");

        SettingsManager.register(ServerSettings.class);
    }

    public void onInitializeServer() {
        getLogger().info("Initializing OneForAll server...");

        ServerLoginManager.init();
    }
}
