package com.koralix.oneforall.config.loader;

import com.koralix.oneforall.OneForAll;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Storages {
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir();
    private static final List<ConfigLoader> DIRECT_LOADERS = new ArrayList<>();
    @Environment(EnvType.CLIENT)
    public static final ConfigLoaderProvider<FileConfigLoader> CLIENT = direct(
            "client",
            (name, modId) -> new FileConfigLoader(CONFIG_PATH.resolve(modId).resolve(name + ".nbt"))
    );
    private static final List<ConfigLoader> DEFERRED_LOADERS = new ArrayList<>();
    public static final ConfigLoaderProvider<PersistentStateConfigLoader> SERVER = deferred(
            "server",
            (name, modId) -> new PersistentStateConfigLoader(modId + "-" + name, World.OVERWORLD)
    );
    
    private Storages() {
        // Prevent instantiation
    }

    @Contract(value = "_, _ -> new", pure = true)
    private static <C extends ConfigLoader> @NotNull ConfigLoaderProvider<C> direct(
            @NotNull String name,
            @NotNull ConfigLoaderProvider.ConfigLoaderFactory<C> factory
    ) {
        return new ConfigLoaderProvider<>(name, factory, DIRECT_LOADERS::add);
    }

    @Contract(value = "_, _ -> new", pure = true)
    private static <C extends ConfigLoader> @NotNull ConfigLoaderProvider<C> deferred(
            @NotNull String name,
            @NotNull ConfigLoaderProvider.ConfigLoaderFactory<C> factory
    ) {
        return new ConfigLoaderProvider<>(name, factory, DEFERRED_LOADERS::add);
    }

    public static void loadDirect() {
        for (ConfigLoader loader : DIRECT_LOADERS) {
            try {
                loader.load();
            } catch (IOException e) {
                OneForAll.logger().error(
                        "Failed to load direct config loader: {}",
                        loader,
                        e
                );
            }
        }
    }

    public static void loadDeferred(@NotNull MinecraftServer server) {
        for (ConfigLoader loader : DEFERRED_LOADERS) {
            if (loader instanceof PersistentStateConfigLoader persistent) persistent.server(server);
            try {
                loader.load();
            } catch (IOException e) {
                OneForAll.logger().error(
                        "Failed to load deferred config loader: {}",
                        loader,
                        e
                );
            }
        }
    }
}
