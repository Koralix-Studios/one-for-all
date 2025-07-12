package com.koralix.oneforall.entry;

import com.koralix.oneforall.OFA;
import com.koralix.oneforall.init.Initializer;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.Random;

public interface OneForAll {
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

    static @NotNull Optional<MinecraftServer> server() {
        return Initializer.optional().flatMap(Initializer::server);
    }
}
