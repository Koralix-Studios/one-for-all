package com.koralix.oneforall.base;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.base.settings.PlayerSettings;
import com.koralix.oneforall.base.settings.ServerSettings;

public class Initializer implements OneForAll {
    @Override
    public void onInitialize() {
        OneForAll.LOGGER.info("Base mod initialized successfully.");

        ServerSettings.register();
        PlayerSettings.register();
    }
}
