package com.koralix.oneforall.settings.registry;

import com.koralix.oneforall.OneForAll;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public record ConfigValueEnvironment(byte flags) {
    public static final ConfigValueEnvironment CLIENT = new ConfigValueEnvironment((byte) 0b00);
    public static final ConfigValueEnvironment SERVER = new ConfigValueEnvironment((byte) 0b10);
    public static final ConfigValueEnvironment INTEGRATED = new ConfigValueEnvironment((byte) 0b10);
    public static final ConfigValueEnvironment DEDICATED = new ConfigValueEnvironment((byte) 0b11);

    public boolean server() {
        return (flags & SERVER.flags) == SERVER.flags;
    }

    public File file(boolean create) {
        Path path;
        if (server()) {
            path = OneForAll.SERVER.getSavePath(WorldSavePath.ROOT);
        } else {
            path = FabricLoader.getInstance().getConfigDir();
        }
        path = path.resolve("oneforall").resolve("settings.nbt");

        File file = path.toFile();
        if (create && !file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return file;
    }
}
