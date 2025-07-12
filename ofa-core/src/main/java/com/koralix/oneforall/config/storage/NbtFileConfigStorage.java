package com.koralix.oneforall.config.storage;

import com.koralix.oneforall.config.ConfigRegistry;
import com.koralix.oneforall.config.backend.ConfigBackend;
import com.koralix.oneforall.config.backend.ConfigBundle;
import com.koralix.oneforall.util.NbtIoExt;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtSizeTracker;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NbtFileConfigStorage<K> implements ConfigStorage<K, NbtElement> {
    private final Path path;

    public NbtFileConfigStorage(Path path) {
        this.path = path;
    }

    @Override
    public void save(@NotNull K backend) throws IOException {
        if (!Files.exists(path.getParent())) Files.createDirectories(path.getParent());
        ConfigBundle<K> bundle = ConfigBackend.cast(backend).bundle();
        NbtElement nbt = bundle.serialize(NbtOps.INSTANCE);
        NbtIoExt.writeCompressed(nbt, path);
    }

    @Override
    public @NotNull ConfigBundle<K> load(@NotNull K backend, @NotNull ConfigRegistry<K> registry) throws IOException {
        if (!Files.exists(path)) return new ConfigBundle<>(registry);
        NbtElement nbt = NbtIoExt.readCompressed(path, NbtSizeTracker.ofUnlimitedBytes());
        return ConfigBundle.deserialize(NbtOps.INSTANCE, nbt, registry);
    }
}
