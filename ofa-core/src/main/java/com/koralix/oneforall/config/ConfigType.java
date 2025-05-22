package com.koralix.oneforall.config;

import com.koralix.oneforall.OneForAll;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

public record ConfigType<V, C extends ConfigValue<V>>(MapCodec<C> codec) {
    public static final Registry<ConfigType<?, ?>> REGISTRY = new SimpleRegistry<>(
            RegistryKey.ofRegistry(OneForAll.id("config_type")),
            Lifecycle.stable()
    );
}
