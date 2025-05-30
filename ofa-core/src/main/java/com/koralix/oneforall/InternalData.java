package com.koralix.oneforall;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class InternalData {
    private Map<String, ModMetadata> modMetadataMap = new HashMap<>();
    private static final Pattern DEV_MOD_URL_PATTERN = Pattern.compile("classes/[^/]+/[^/]+/$");

    InternalData() {
        modMetadataMap.put(
                this.getClass().getProtectionDomain().getCodeSource().getLocation().toString(),
                FabricLoader.getInstance().getModContainer(OneForAll.MOD_ID).orElseThrow().getMetadata()
        );
    }

    void register(@NotNull String url, @NotNull ModMetadata metadata) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            url = DEV_MOD_URL_PATTERN.matcher(url).replaceAll("");
        }
        if (this.modMetadataMap.containsKey(url)) {
            OneForAll.LOGGER.warn("Duplicate OneForAll entrypoint found for URL: {}", url);
            return;
        }
        this.modMetadataMap.put(url, metadata);
    }

    void freeze() {
        this.modMetadataMap = Map.copyOf(this.modMetadataMap);
    }

    public boolean containsMod(@NotNull String url) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            url = DEV_MOD_URL_PATTERN.matcher(url).replaceAll("");
        }
        return this.modMetadataMap.containsKey(url);
    }

    public @Nullable ModMetadata getMetadata(@NotNull String url) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            url = DEV_MOD_URL_PATTERN.matcher(url).replaceAll("");
        }
        return this.modMetadataMap.get(url);
    }
}
