package com.koralix.oneforall;

import com.koralix.oneforall.platform.Platform;

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
    }

    public void onInitializeServer() {
        getLogger().info("Initializing OneForAll server...");
    }
}
