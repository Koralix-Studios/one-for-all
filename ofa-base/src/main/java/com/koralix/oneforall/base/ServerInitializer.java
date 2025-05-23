package com.koralix.oneforall.base;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.OneForAllDedicated;

public class ServerInitializer implements OneForAllDedicated {
    @Override
    public void onInitializeDedicated() {
        OneForAll.LOGGER.info("Base mod initialized successfully.");
    }
}
