package com.koralix.oneforall;

import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OFA {
    private final ModMetadata metadata;
    private final Logger logger;

    private OFA(ModMetadata metadata, Logger logger) {
        this.metadata = metadata;
        this.logger = logger;
    }

    @Contract(pure = true)
    public static @NotNull OFA of(@NotNull ModMetadata metadata) {
        return new OFA(
                metadata,
                LoggerFactory.getLogger(metadata.getId())
        );
    }

    public @NotNull ModMetadata metadata() {
        return metadata;
    }

    public @NotNull Logger logger() {
        return logger;
    }

    public @NotNull String id() {
        return metadata.getId();
    }

    public @NotNull String name() {
        return metadata.getName();
    }

    public @NotNull String description() {
        return metadata.getDescription();
    }

    public @NotNull Version version() {
        return metadata.getVersion();
    }

    @Contract("_ -> new")
    public @NotNull Identifier id(@NotNull String path) {
        return Identifier.of(id(), path);
    }
}
