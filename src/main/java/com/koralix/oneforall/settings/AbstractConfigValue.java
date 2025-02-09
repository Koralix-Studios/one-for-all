package com.koralix.oneforall.settings;

import com.koralix.oneforall.settings.registry.ConfigValueEntry;
import com.koralix.oneforall.utils.OnceCell;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.fabricmc.loader.api.SemanticVersion;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.ApiStatus;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class AbstractConfigValue<T> implements ConfigValue<T> {
    private final OnceCell<ConfigValueEntry<T>> entry = new OnceCell<>();
    private final T nominalValue;
    private final Codec<T> codec;
    private final ConfigValueAdapter.Command<T, ?> command;
    private final ConfigValidator<T> validator;
    private final Predicate<CommandSource> permission;
    private final Event<ConfigValueChange.OnChange<T>> onChange = EventFactory.createArrayBacked(ConfigValueChange.OnChange.class, listeners -> change -> {
        for (ConfigValueChange.OnChange<T> listener : listeners) {
            listener.onChange(change);
        }
    });
    private final SemanticVersion since;

    public AbstractConfigValue(
            T nominalValue,
            Codec<T> codec,
            ConfigValueAdapter.Command<T, ?> command,
            ConfigValidator<T> validator,
            Predicate<CommandSource> permission,
            SemanticVersion since
    ) {
        this.nominalValue = nominalValue;
        this.codec = codec;
        this.command = command;
        this.validator = validator;
        this.permission = permission;
        this.since = since;
    }

    @Override
    public ConfigValueEntry<T> entry() {
        return entry.get();
    }

    @ApiStatus.Internal
    public void entry(ConfigValueEntry<T> entry) {
        this.entry.set(entry);
    }

    @Override
    public T nominalValue() {
        return nominalValue;
    }

    @Override
    public Codec<T> codec() {
        return codec;
    }

    @Override
    public Optional<Text> validate(T value, Consumer<T> action) {
        Text result = validator.test(value);
        if (result != null) return Optional.of(result);
        action.accept(value);
        return Optional.empty();
    }

    @Override
    public ConfigValueAdapter.Command<T, ?> command() {
        return command;
    }

    @Override
    public boolean hasPermission(CommandSource source) {
        return permission.test(source);
    }

    @Override
    public Event<ConfigValueChange.OnChange<T>> onChange() {
        return onChange;
    }

    @Override
    public SemanticVersion since() {
        return since;
    }

    @Override
    public String toString() {
        return entry().key().toString();
    }
}
