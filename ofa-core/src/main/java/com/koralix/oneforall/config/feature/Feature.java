package com.koralix.oneforall.config.feature;

import com.koralix.oneforall.config.ConfigActor;
import com.koralix.oneforall.config.ConfigResult;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.text.Text;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Feature<T> {
    @NotNull T get(UUID uuid);
    @NotNull ConfigResult<T> get(@NotNull ConfigActor actor, @NotNull UUID uuid);
    @NotNull ConfigResult<T> get(@NotNull ConfigActor actor);
    void set(@NotNull UUID uuid, @NotNull T value);
    @NotNull ConfigResult<T> set(@NotNull ConfigActor actor, @NotNull UUID uuid, @NotNull T value);
    @NotNull ConfigResult<T> set(@NotNull ConfigActor actor, @NotNull T value);
    <S> void command(@NotNull ArgumentBuilder<S, ?> parent, @NotNull TriConsumer<S, Text, Boolean> feedback);
}
