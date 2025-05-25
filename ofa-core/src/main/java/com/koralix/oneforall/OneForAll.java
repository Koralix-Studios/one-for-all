package com.koralix.oneforall;

import com.koralix.oneforall.util.Functions;
import net.fabricmc.loader.api.Version;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface OneForAll {
    String MOD_ID = /*$ mod.id*/ "one-for-all";
    String MOD_NAME = /*$ mod.name*/ "One For All";
    String MOD_DESCRIPTION = /*$ mod.description*/ "One Mod to rule them all, One Mod to find them, One Mod to bring them all and in the darkness bind them.";
    String MOD_VERSION = /*$ mod.version*/ "0.1.0";

    Logger LOGGER = LoggerFactory.getLogger(OneForAll.MOD_NAME);
    Version VERSION = Functions.tryCatch(Version::parse).apply(MOD_VERSION);

    @Contract(value = "_ -> new", pure = true)
    static @NotNull Identifier id(@NotNull String path) {
        return Identifier.of(MOD_ID, path);
    }

    void onInitialize();
}
