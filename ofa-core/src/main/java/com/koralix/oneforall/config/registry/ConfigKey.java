package com.koralix.oneforall.config.registry;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;

public record ConfigKey(Identifier registryId, Identifier configId) {
    public static final Codec<ConfigKey> CODEC = Codec.pair(Identifier.CODEC, Identifier.CODEC).xmap(
            list -> new ConfigKey(list.getFirst(), list.getSecond()),
            key -> Pair.of(key.registryId, key.configId)
    );
}
