package com.koralix.oneforall.config.loader;

import com.mojang.serialization.Codec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PersistentStateConfigLoader extends ConfigLoader {
    private final RegistryKey<World> worldKey;
    private final PersistentStateType<ConfigStorageState> type;
    private @Nullable ConfigStorageState state;

    public PersistentStateConfigLoader(String name, RegistryKey<World> worldKey) {
        this.worldKey = worldKey;
        this.type = new PersistentStateType<>(
                name,
                ConfigStorageState::new,
                ConfigStorageState.CODEC,
                null
        );
    }

    public void server(@Nullable MinecraftServer server) {
        this.state = null;
        if (server == null) return;

        ServerWorld world = server.getWorld(worldKey);
        if (world == null) {
            throw new IllegalStateException("World with key " + worldKey + " not found in server");
        }
        this.state = world.getPersistentStateManager().getOrCreate(this.type);
    }

    @Override
    protected @Nullable ConfigStorage loadStorage() {
        if (this.state == null) {
            throw new IllegalStateException("Persistent state not initialized. Call server() with a valid MinecraftServer instance.");
        }
        return this.state.storage;
    }

    @Override
    protected void saveStorage(@NotNull ConfigStorage storage) {
        if (this.state == null) {
            throw new IllegalStateException("Persistent state not initialized. Call server() with a valid MinecraftServer instance.");
        }
        this.state.storage = storage;
        this.state.markDirty();
    }

    @Override
    public String toString() {
        return "PersistentStateConfigLoader{" +
                "type=" + type.toString() +
                '}';
    }

    private static class ConfigStorageState extends PersistentState {
        public static final Codec<ConfigStorageState> CODEC = ConfigStorage.CODEC.xmap(
                ConfigStorageState::new,
                state -> state.storage
        );
        private ConfigStorage storage;

        public ConfigStorageState(ConfigStorage storage) {
            this.storage = storage;
        }

        public ConfigStorageState() {
            this.storage = ConfigStorage.create();
        }
    }
}
