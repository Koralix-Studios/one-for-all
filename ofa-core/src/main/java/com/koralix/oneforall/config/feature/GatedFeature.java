package com.koralix.oneforall.config.feature;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.command.OfaCommand;
import com.koralix.oneforall.config.ConfigActor;
import com.koralix.oneforall.config.ConfigResult;
import com.koralix.oneforall.config.MonoConfigValue;
import com.koralix.oneforall.config.PlayerConfigValue;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.text.Text;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.BiFunction;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class GatedFeature<T, G> implements Feature<T> {
    private final @NotNull String id;
    private final @NotNull PlayerConfigValue<T, ?> configValue;
    private final @NotNull MonoConfigValue<G, ?, ?> gateValue;
    private final @NotNull BiFunction<T, G, T> gateFunction;

    public GatedFeature(
            @NotNull String id,
            @NotNull PlayerConfigValue<T, ?> configValue,
            @NotNull MonoConfigValue<G, ?, ?> gateValue,
            @NotNull BiFunction<T, G, T> gateFunction
    ) {
        this.id = id;
        this.configValue = configValue;
        this.gateValue = gateValue;
        this.gateFunction = gateFunction;
    }

    @Override
    public @NotNull T get(@NotNull UUID uuid) {
        return this.gateFunction.apply(this.configValue.value(uuid), this.gateValue.value());
    }

    @Override
    public @NotNull ConfigResult<T> get(@NotNull ConfigActor actor, @NotNull UUID uuid) {
        ConfigResult<T> result = this.configValue.value(actor, uuid);
        ConfigResult<G> gateResult = this.gateValue.value(actor);
        if (result.isError()) return result;
        if (gateResult.isError()) return new ConfigResult.ForbidObserve<>(actor);
        return ConfigResult.ok(this.gateFunction.apply(result.get().orElseThrow(), gateResult.get().orElseThrow()));
    }

    @Override
    public @NotNull ConfigResult<T> get(@NotNull ConfigActor actor) {
        return actor.uuid().map(uuid -> this.get(actor, uuid)).orElseGet(() -> new ConfigResult.RequiredUUID<>(actor));
    }

    @Override
    public void set(@NotNull UUID uuid, @NotNull T value) {
        this.configValue.value(uuid, this.gateFunction.apply(value, this.gateValue.value()));
    }

    @Override
    public @NotNull ConfigResult<T> set(@NotNull ConfigActor actor, @NotNull UUID uuid, @NotNull T value) {
        ConfigResult<G> gateResult = this.gateValue.value(actor);
        if (gateResult.isError()) return new ConfigResult.ForbidChange<>(actor);
        return this.configValue.value(actor, uuid, this.gateFunction.apply(value, gateResult.get().orElseThrow()));
    }

    @Override
    public @NotNull ConfigResult<T> set(@NotNull ConfigActor actor, @NotNull T value) {
        return actor.uuid().map(uuid -> this.set(actor, uuid, value)).orElseGet(() -> new ConfigResult.RequiredUUID<>(actor));
    }

    @Override
    public <S> void command(@NotNull ArgumentBuilder<S, ?> parent, @NotNull TriConsumer<S, Text, Boolean> feedback) {
        LiteralArgumentBuilder<S> gateLiteral = literal("gate");
        this.gateValue.commandAdapter().adapt("gate", gateLiteral, (builder, getter) -> builder.executes(context -> {
            G gate = getter.get(context, "gate");
            ConfigResult<G> gateResult = this.gate((ConfigActor) context.getSource(), gate);
            if (gateResult.isError()) throw OfaCommand.CONFIG_TEST_EXCEPTION.create(gateResult.message());
            feedback.accept(
                    context.getSource(),
                    Text.stringifiedTranslatable("command." + OneForAll.id() + ".feature.gate.set", this.id, gate.toString()),
                    false
            );
            return 1;
        }));
        gateLiteral.executes(context -> {
            ConfigResult<G> gateResult = this.gate((ConfigActor) context.getSource());
            if (gateResult.isError()) throw OfaCommand.CONFIG_TEST_EXCEPTION.create(gateResult.message());
            feedback.accept(
                    context.getSource(),
                    Text.stringifiedTranslatable("command." + OneForAll.id() + ".feature.gate", this.id, gateResult.get().orElseThrow().toString()),
                    false
            );
            return 1;
        });
        parent.then(gateLiteral);

        LiteralArgumentBuilder<S> valueLiteral = literal("value");
        this.configValue.commandAdapter().adapt("value", valueLiteral, (builder, getter) -> builder.executes(context -> {
            T value = getter.get(context, "value");
            ConfigResult<T> valueResult = this.set((ConfigActor) context.getSource(), value);
            if (valueResult.isError()) throw OfaCommand.CONFIG_TEST_EXCEPTION.create(valueResult.message());
            feedback.accept(
                    context.getSource(),
                    Text.stringifiedTranslatable("command." + OneForAll.id() + ".feature.value.set", this.id, value.toString()),
                    false
            );
            return 1;
        }));
        valueLiteral.executes(context -> {
            ConfigResult<T> valueResult = this.get((ConfigActor) context.getSource());
            if (valueResult.isError()) throw OfaCommand.CONFIG_TEST_EXCEPTION.create(valueResult.message());
            feedback.accept(
                    context.getSource(),
                    Text.stringifiedTranslatable("command." + OneForAll.id() + ".feature.value", this.id, valueResult.get().orElseThrow().toString()),
                    false
            );
            return 1;
        });
        parent.then(valueLiteral);
    }

    public @NotNull G gate() {
        return this.gateValue.value();
    }

    public @NotNull ConfigResult<G> gate(@NotNull ConfigActor actor) {
        return this.gateValue.value(actor);
    }

    public void gate(@NotNull G gate) {
        this.gateValue.value(gate);
    }

    public @NotNull ConfigResult<G> gate(@NotNull ConfigActor actor, @NotNull G gate) {
        return this.gateValue.value(actor, gate);
    }
}
