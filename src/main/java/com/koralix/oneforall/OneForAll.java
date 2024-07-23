package com.koralix.oneforall;

import com.koralix.oneforall.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OneForAll {
    private final Logger logger = LoggerFactory.getLogger("OneForAll");
    private final Platform platform;

    public static OneForAll getInstance() {
        return Initializer.instance;
    }

    public OneForAll(Platform platform) {
        this.platform = platform;
    }

    abstract void onInitialize();

    public Platform getPlatform() {
        return platform;
    }

    public Logger getLogger() {
        return logger;
    }
}
