package com.koralix.oneforall.settings;

import com.koralix.oneforall.serde.Deserialize;
import com.koralix.oneforall.serde.Serde;
import com.koralix.oneforall.serde.Serialize;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface ConfigValue<T> extends Serialize, Deserialize {
    @Contract(value = "_ -> new", pure = true)
    static <T> @NotNull ConfigValueBuilder<T> of(@NotNull T value) {
        return new ConfigValueBuilder<>(value);
    }

    @Contract(value = "_ -> new", pure = true)
    static <T> @NotNull ConfigValueBuilder<T> ofNull(@NotNull Class<T> clazz) {
        return new ConfigValueBuilder<>(clazz);
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

    @Override
    default void serialize(PacketByteBuf buf) {
        Serde.serialize(buf, defaultValue());
        Serde.serialize(buf, value());
    }

    @Override
    default void deserialize(PacketByteBuf buf) {
        defaultValue(Serde.deserialize(buf, clazz()));
        value(Serde.deserialize(buf, clazz()));
    }
}
