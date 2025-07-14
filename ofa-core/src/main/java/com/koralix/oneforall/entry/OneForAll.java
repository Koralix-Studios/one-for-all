package com.koralix.oneforall.entry;

import com.koralix.oneforall.OFA;
import com.koralix.oneforall.init.Initializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Random;

public interface OneForAll {
    String MOD_ID = /*$ mod.id*/ "oneforall";
    String MOD_NAME = /*$ mod.name*/ "One For All";
    String MOD_DESCRIPTION = /*$ mod.description*/ "One Mod to rule them all, One Mod to find them, One Mod to bring them all and in the darkness bind them.";
    String MOD_VERSION = /*$ mod.version*/ "0.1.0";

    Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    Random RANDOM = new Random();

    void setup(OFA ofa);

    void onInitialize();

    static void load(@NotNull Class<?> clazz) {
        try {
            Class.forName(clazz.getName(), true, clazz.getClassLoader());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load class: " + clazz.getName(), e);
        }
    }

    @Contract("_ -> new")
    static @NotNull Identifier id(@NotNull String path) {
        return Identifier.of(MOD_ID, path);
    }

    static @NotNull Optional<MinecraftServer> server() {
        return Initializer.optional().flatMap(Initializer::server);
    }
}
