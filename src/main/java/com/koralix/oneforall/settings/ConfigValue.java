package com.koralix.oneforall.settings;

import com.koralix.oneforall.settings.registry.ConfigValueEntry;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Optional;
import java.util.function.Consumer;

public interface ConfigValue<T> {
    /**
     * Retrieves the entry associated with this config value.
     * This entry represents the config value inside a particular ConfigValueRegistry.
     *
     * @return the config value entry
     */
    ConfigValueEntry<T> entry();

    /**
     * The nominal value of this config value.
     * This value is used to determine the default value of the config value.
     *
     * @return the nominal value of this config value
     */
    T nominalValue();

    /**
     * The codec of this config value.
     * This codec is used to serialize and deserialize the config value.
     *
     * @return the codec of this config value
     */
    Codec<T> codec();

    /**
     * Validate the given value.
     * If the value is valid, the value is accepted and the action is performed.
     * If the value is invalid, the value is rejected and the error message is returned.
     *
     * @param value the value to validate
     * @param action the action to perform if the value is valid
     * @return the error message if the value is invalid, otherwise empty
     */
    Optional<Text> validate(T value, Consumer<T> action);

    /**
     * Create a config value view with the given context.
     *
     * @param context the context to create the config value view with
     * @param <S> the type of the command sourcea
     * @return the config value view
     */
    ConfigValueView<T> view(CommandContext<? extends CommandSource> context);

    static <T> SingletonConfigValue.Builder<T> singleton(T nominalValue, Codec<T> codec) {
        return new SingletonConfigValue.Builder<>(nominalValue, codec);
    }

    static <T> PlayerConfigValue.Builder<T> player(T nominalValue, Codec<T> codec) {
        return new PlayerConfigValue.Builder<>(nominalValue, codec);
    }
}
