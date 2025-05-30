package com.koralix.oneforall;

import com.koralix.oneforall.session.LoginManager;
import com.koralix.oneforall.settings.ServerSettings;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Initializer implements ModInitializer {
    @Override
    public void onInitialize() {
        OneForAll.LOGGER.info("Initializing {} v{}", OneForAll.MOD_ID, OneForAll.MOD_VERSION);

        ServerSettings.register();

        for (OneForAll ofa : FabricLoader.getInstance().getEntrypoints("ofa", OneForAll.class)) {
            ofa.onInitialize();
        }

        LoginManager.init();
    }
}
