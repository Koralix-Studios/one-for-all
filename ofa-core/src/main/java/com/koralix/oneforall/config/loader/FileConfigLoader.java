package com.koralix.oneforall.config.loader;

import com.koralix.oneforall.OneForAll;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class FileConfigLoader extends ConfigLoader {
    private final @NotNull Path path;

    public FileConfigLoader(@NotNull Path path) {
        this.path = path;
    }

    @Override
    protected @Nullable ConfigStorage loadStorage() throws IOException {
        NbtElement nbt = NbtIo.read(this.path);
        if (nbt == null) return null;

        DataResult<ConfigStorage> result = ConfigStorage.CODEC.parse(NbtOps.INSTANCE, nbt);
        Optional<ConfigStorage> opt = result.result();
        Optional<DataResult.Error<ConfigStorage>> error = result.error();
        if (error.isPresent()) {
            OneForAll.logger().warn("Skipping some configs: {}", error.get().message());
            opt = error.get().partialValue();
        }
        return opt.orElseThrow(() -> error
                .map(configStorageError -> new NbtException("Failed to parse config storage from NBT: " + configStorageError.message()))
                .orElseGet(() -> new NbtException("Unexpected error while parsing config storage from NBT"))
        );
    }

    @Override
    protected void saveStorage(@NotNull ConfigStorage storage) throws IOException {
        DataResult<NbtCompound> nbt = ConfigStorage.CODEC.encodeStart(NbtOps.INSTANCE, storage)
                .flatMap(nbt1 -> nbt1 instanceof NbtCompound compound
                        ? DataResult.success(compound)
                        : DataResult.error(() -> "Expected NbtCompound, got " + nbt1));
        if (nbt.error().isPresent()) {
            throw new IOException("Failed to encode config storage to NBT: " + nbt.error().get().message());
        }
        this.path.getParent().toFile().mkdirs();
        NbtIo.write(nbt.result().orElseThrow(), this.path);
    }

    @Override
    public String toString() {
        return "FileConfigLoader{" +
                "path=" + path +
                '}';
    }
}
