package com.koralix.oneforall.settings;

import com.koralix.oneforall.settings.registry.ConfigValueEntry;
import com.koralix.oneforall.utils.IntoText;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.Codec;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;
import java.util.function.Consumer;

public interface ConfigValue<T> extends IntoText {
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

    /**
     * The command adapter of this config value.
     * This adapter is used to create the command for this config value.
     *
     * @return the command adapter of this config value
     */
    ConfigValueAdapter.Command<T, ?> command();

    /**
     * Check if the given command source has permission to access this config value.
     *
     * @param source the command source to check
     * @return true if the command source has permission, otherwise false
     */
    boolean hasPermission(CommandSource source);

    @Override
    default Text toText() {
        Identifier id = entry().key().asIdentifier();
        return Text.translatable("settings." + id.getNamespace() + "." + id.getPath());
    }

    static <T> SingletonConfigValue.Builder<T> singleton(T nominalValue, Codec<T> codec, ConfigValueAdapter.Command<T, ?> command) {
        return new SingletonConfigValue.Builder<>(nominalValue, codec, command);
    }

    static <T> PlayerConfigValue.Builder<T> player(T nominalValue, Codec<T> codec, ConfigValueAdapter.Command<T, ?> command) {
        return new PlayerConfigValue.Builder<>(nominalValue, codec, command);
    }
}
