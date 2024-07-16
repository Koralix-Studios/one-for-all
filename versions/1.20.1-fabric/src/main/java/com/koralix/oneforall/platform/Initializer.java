package com.koralix.oneforall.platform;

import com.koralix.oneforall.OneForAll;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Initializer implements ModInitializer, ClientModInitializer, DedicatedServerModInitializer {
    private final Logger logger = LoggerFactory.getLogger("OneForAll");
    private final OneForAll instance = new OneForAll(Platform.FABRIC);

    @Override
    public void onInitialize() {
        logger.info("Initializing OneForAll...");
        instance.onInitialize();
        logger.info("OneForAll initialized!");
    }

    @Override
    public void onInitializeClient() {
        logger.info("Initializing OneForAll client...");
        instance.onInitializeClient();
        logger.info("OneForAll client initialized!");
    }

    @Override
    public void onInitializeServer() {
        logger.info("Initializing OneForAll server...");
        instance.onInitializeServer();
        logger.info("OneForAll server initialized!");
    }
}
