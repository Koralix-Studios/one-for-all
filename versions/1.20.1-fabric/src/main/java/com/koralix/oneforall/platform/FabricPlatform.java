package com.koralix.oneforall.platform;

import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatform implements Platform {
    @Override
    public ModMetadata getMetadata(String modId) {
        return FabricLoader.getInstance().getModContainer(modId)
                .map(container -> new ModMetadata(
                        container.getMetadata().getId(),
                        container.getMetadata().getName(),
                        container.getMetadata().getVersion().getFriendlyString(),
                        container.getMetadata().getDescription()
                ))
                .orElse(null);
    }
}
