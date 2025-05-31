package com.koralix.oneforall;

import net.fabricmc.loader.api.Version;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Random;

public interface OneForAll {
    Random RANDOM = new Random();
    InternalData INTERNAL_DATA = new InternalData();

    @Contract(pure = true)
    static @NotNull OFA instance() {
        return INTERNAL_DATA.instance();
    }

    @Contract(pure = true)
    static @NotNull String id() {
        return INTERNAL_DATA.instance().id();
    }

    @Contract(pure = true)
    static @NotNull String name() {
        return INTERNAL_DATA.instance().name();
    }

    @Contract(pure = true)
    static @NotNull String description() {
        return INTERNAL_DATA.instance().description();
    }

    @Contract(pure = true)
    static @NotNull Version version() {
        return INTERNAL_DATA.instance().version();
    }

    @Contract(value = "_ -> new", pure = true)
    static @NotNull Identifier id(@NotNull String path) {
        return INTERNAL_DATA.instance().id(path);
    }

    @Contract(pure = true)
    static @NotNull Logger logger() {
        return INTERNAL_DATA.instance().logger();
    }

    void onInitialize();
}
