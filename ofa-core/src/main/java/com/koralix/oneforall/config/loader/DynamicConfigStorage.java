package com.koralix.oneforall.config.loader;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.config.ConfigValue;
import com.koralix.oneforall.config.registry.ConfigKey;
import com.koralix.oneforall.config.registry.ConfigRegistry;
import com.mojang.serialization.DataResult;
import net.fabricmc.loader.api.Version;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class DynamicConfigStorage extends ConfigStorage {
    private final Set<ConfigKey> addedKeys = new HashSet<>();
    private @Nullable Path path;

    private DynamicConfigStorage(Version version, Map<ConfigKey, Object> entries) {
        super(version, entries);
    }

    public void path(@NotNull Path path) throws IOException {
        if (this.path != null) {
            throw new IllegalStateException("Path is already set for this DynamicConfigStorage.");
        }
        if (!this.entries.isEmpty()) {
            this.flush();
        }
        this.path = path;
        NbtCompound nbt = NbtIo.read(path);
        if (nbt == null) return;
        DataResult<ConfigStorage> result = ConfigStorage.CODEC.parse(NbtOps.INSTANCE, nbt);
        if (result.error().isPresent()) {
            throw new IOException("Failed to load config storage from " + path + ": " + result.error().get().message());
        }
        result.result().orElseThrow().entries.forEach((key, o) -> {
            ConfigValue<?, ?, ?> configValue = ConfigRegistry.get(key).orElseThrow();
            load(configValue, o);
            this.add(configValue);
        });
    }

    @SuppressWarnings("unchecked")
    private <S> void load(@NotNull ConfigValue<?, ?, S> configValue, @NotNull Object value) {
        S saveData = (S) value;
        configValue.loadData(saveData);
    }

    public void flush() throws IOException {
        this.save();
        this.path = null;
        this.entries.clear();
        this.addedKeys.clear();
    }

    private void save() throws IOException {
        if (this.path == null) {
            throw new IllegalStateException("Path is not set for this DynamicConfigStorage.");
        }
        DataResult<NbtCompound> nbt = ConfigStorage.CODEC.encodeStart(NbtOps.INSTANCE, this)
                .flatMap(nbt1 -> nbt1 instanceof NbtCompound compound
                        ? DataResult.success(compound)
                        : DataResult.error(() -> "Expected NbtCompound, got " + nbt1));
        if (nbt.error().isPresent()) {
            throw new IOException("Failed to encode config storage to NBT: " + nbt.error().get().message());
        }
        this.path.getParent().toFile().mkdirs();
        NbtIo.write(nbt.result().orElseThrow(), this.path);
    }

    public <S> void add(@NotNull ConfigValue<?, ?, S> configValue) {
        ConfigKey key = configValue.key();
        this.set(configValue);

        if (this.addedKeys.contains(key)) return;

        configValue.onChange(s -> {
            if (!this.addedKeys.contains(key)) return false;
            this.set(configValue, s);
            return true;
        });

        this.addedKeys.add(key);
    }

    private <S> void set(@NotNull ConfigValue<?, ?, S> configValue, @Nullable S value) {
        ConfigKey key = configValue.key();
        if (value == null) this.entries.remove(key);
        else this.entries.put(key, value);
        try {
            if (this.path != null) this.save();
        } catch (Exception e) {
            OneForAll.logger().error("Failed to save config value for {} on {}: {}", key, this.path, e.getMessage(), e);
        }
    }

    private <S> void set(@NotNull ConfigValue<?, ?, S> configValue) {
        this.set(configValue, configValue.saveData().orElse(null));
    }

    @Contract("_ -> new")
    public static @NotNull DynamicConfigStorage create(@NotNull Collection<ConfigValue<?, ?, ?>> configs) {
        DynamicConfigStorage storage = new DynamicConfigStorage(OneForAll.version(), new HashMap<>());

        for (ConfigValue<?, ?, ?> config : configs) {
            storage.add(config);
        }

        return storage;
    }

    @Contract("_ -> new")
    public static @NotNull DynamicConfigStorage create(ConfigValue<?, ?, ?>... configs) {
        return create(List.of(configs));
    }
}
