package com.koralix.oneforall;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OneForAll implements ModInitializer {
    public static final String MOD_ID = /*$ mod.id*/ "one-for-all";
    public static final String MOD_NAME = /*$ mod.name*/ "One For All";
    public static final String MOD_DESCRIPTION = /*$ mod.description*/ "One Mod to rule them all, One Mod to find them, One Mod to bring them all and in the darkness bind them.";
    public static final String MOD_VERSION = /*$ mod.version*/ "0.1.0";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing {} v{}", MOD_ID, MOD_VERSION);
    }
}
