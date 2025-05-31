package com.koralix.oneforall;

import com.koralix.oneforall.util.Functions;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record OFA(
        ModMetadata metadata,
        Logger logger
) {
    @Contract(pure = true)
    public static @NotNull OFA of(@NotNull ModMetadata metadata) {
        return new OFA(
                metadata,
                LoggerFactory.getLogger(metadata.getId())
        );
    }

    public String id() {
        return metadata.getId();
    }

    public String name() {
        return metadata.getName();
    }

    public String description() {
        return metadata.getDescription();
    }

    public Version version() {
        return metadata.getVersion();
    }

    @Contract("_ -> new")
    public @NotNull Identifier id(@NotNull String path) {
        return Identifier.of(id(), path);
    }

    public Logger logger() {
        return logger;
    }
}
