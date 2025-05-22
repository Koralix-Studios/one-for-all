package com.koralix.oneforall;

import net.fabricmc.api.ModInitializer;

public class Initializer implements ModInitializer {
    @Override
    public void onInitialize() {
        OneForAll.LOGGER.info("Initializing {} v{}", OneForAll.MOD_ID, OneForAll.MOD_VERSION);
    }
}
