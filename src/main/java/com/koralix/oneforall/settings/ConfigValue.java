package com.koralix.oneforall.settings;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public interface ConfigValue<T> {
    @Contract(value = "_, _ -> new", pure = true)
    static <T> @NotNull ConfigValueBuilder<T> of(@NotNull T value, @NotNull Codec<T> codec) {
        return new ConfigValueBuilder<>(value, codec);
    }

    @Contract(value = "_, _ -> new", pure = true)
    static <T> @NotNull ConfigValueBuilder<T> ofNull(@NotNull Class<T> clazz, @NotNull Codec<T> codec) {
        return new ConfigValueBuilder<>(clazz, codec);
    }

    /**
     * Get the registry of the config value
     *
     * @return the registry of the config value
     */
    SettingsRegistry registry();

    /**
     * Get the identifier of the config value
     *
     * @return the identifier of the config value
     */
    SettingEntry<T> entry();

    /**
     * Test if the user satisfies the permission predicate
     *
     * @return whether the user satisfies the permission predicate
     */
    boolean permission(ServerCommandSource source);

    /**
     * Reset the config value to the default value
     *
     * @return if the config value was reset successfully
     */
    default void reset() {
        value(defaultValue());
    }

    /**
     * Restore the config value to standard settings
     *
     * @return if the config value was restored successfully
     */
    default void restore() {
        defaultValue(nominalValue());
        reset();
    }

    /**
     * Get the nominal value of the config value
     *
     * @return the nominal value
     */
    T nominalValue();

    /**
     * Get the default value of the config value
     *
     * @return the default value
     */
    T defaultValue();

    /**
     * Set the default value of the config value
     *
     * @param value the new default value
     * @return if the default value was set successfully
     */
    Text defaultValue(T value);

    /**
     * Get the current value of the config value
     *
     * @return the current value
     */
    T value();


    /**
     * Set the current value of the config value
     *
     * @param value the new value
     * @return if the value was set successfully
     */
    Text value(T value);

    /**
     * Get the class of the config value
     *
     * @return the class of the config value
     */
    Class<T> clazz();

    default <V> void serialize(DynamicOps<V> ops, Consumer<V> consumer) {
        Codec<T> defaultCodec = codec().fieldOf("default").codec();
        Codec<T> valueCodec = codec().fieldOf("value").codec();
        Codec<Pair<T, T>> codec = Codec.pair(defaultCodec, valueCodec);

        codec.encodeStart(ops, Pair.of(defaultValue(), value())).result().ifPresentOrElse(
                consumer,
                () -> {
                    throw new IllegalStateException("Failed to serialize config value");
                }
        );
    }

    default <V> void deserialize(DynamicOps<V> ops, V input) {
        Codec<T> defaultCodec = codec().fieldOf("default").codec();
        Codec<T> valueCodec = codec().fieldOf("value").codec();
        Codec<Pair<T, T>> codec = Codec.pair(defaultCodec, valueCodec);
        DataResult<Pair<T, T>> result = codec.parse(ops, input);
        result.result().ifPresentOrElse(
                pair -> {
                    defaultValue(pair.getFirst());
                    value(pair.getSecond());
                },
                () -> {
                    throw new IllegalStateException("Failed to deserialize config value");
                }
        );
    }

    Codec<T> codec();
}
