package com.koralix.oneforall.platform;

import com.koralix.oneforall.OneForAll;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Initializer implements ModInitializer, ClientModInitializer, DedicatedServerModInitializer {
    private final Logger LOGGER = LoggerFactory.getLogger("OneForAll");
    private final OneForAll instance = new OneForAll();

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing OneForAll...");
        instance.onInitialize();
        LOGGER.info("OneForAll initialized!");
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing OneForAll client...");
        instance.onInitializeClient();
        LOGGER.info("OneForAll client initialized!");
    }

    @Override
    public void onInitializeServer() {
        LOGGER.info("Initializing OneForAll server...");
        instance.onInitializeServer();
        LOGGER.info("OneForAll server initialized!");
    }
}
