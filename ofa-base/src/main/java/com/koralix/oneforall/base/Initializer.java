package com.koralix.oneforall.base;

import com.koralix.oneforall.OneForAll;

public class Initializer implements OneForAll {
    @Override
    public void onInitialize() {
        OneForAll.LOGGER.info("Base mod initialized successfully.");
    }
}
