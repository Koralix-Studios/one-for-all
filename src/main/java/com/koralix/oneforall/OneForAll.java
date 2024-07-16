package com.koralix.oneforall;

import com.koralix.oneforall.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OneForAll {
    private final Logger logger = LoggerFactory.getLogger("OneForAll");
    private final Platform platform;

    public OneForAll(Platform platform) {
        this.platform = platform;
    }

    public void onInitialize() {
        logger.info("Initializing OneForAll...");
    }

    public void onInitializeClient() {
        logger.info("Initializing OneForAll client...");
    }

    public void onInitializeServer() {
        logger.info("Initializing OneForAll server...");
    }
}
