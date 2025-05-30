package com.koralix.oneforall;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class PreLaunchInitializer implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        for (EntrypointContainer<OneForAll> container : FabricLoader.getInstance().getEntrypointContainers("ofa", OneForAll.class)) {
            OneForAll ofa = container.getEntrypoint();
            String url = ofa.getClass().getProtectionDomain().getCodeSource().getLocation().toString();
            OneForAll.INTERNAL_DATA.register(url, container.getProvider().getMetadata());
        }

        OneForAll.INTERNAL_DATA.freeze();

        OneForAll.LOGGER.info("OneForAll pre-launch initialization complete.");

        for (OneForAllPreLaunch ofa : FabricLoader.getInstance().getEntrypoints("ofa-preLaunch", OneForAllPreLaunch.class)) {
            ofa.onPreLaunch();
        }
    }
}
