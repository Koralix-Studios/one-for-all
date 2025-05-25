package com.koralix.oneforall.config.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.util.Identifier;

public record ConfigKey(Identifier registryId, Identifier configId) {
    public static final Codec<ConfigKey> STRING_CODEC = Codec.STRING.comapFlatMap(
            s -> {
                int i = s.indexOf('/');
                if (i <= 0) return DataResult.error(() -> "Invalid ConfigKey format: " + s);

                String registry = s.substring(0, i);
                String config = s.substring(i + 1);

                int j = registry.indexOf(':');
                if (j <= 0 || j == registry.length() - 1) return DataResult.error(() -> "Invalid ConfigKey format: " + s);

                int k = config.indexOf(':');
                if (k <= 0 || k == config.length() - 1) return DataResult.error(() -> "Invalid ConfigKey format: " + s);

                Identifier registryId = Identifier.tryParse(registry);
                if (registryId == null) return DataResult.error(() -> "Invalid registry identifier: " + registry);
                Identifier configId = Identifier.tryParse(config);
                if (configId == null) return DataResult.error(() -> "Invalid config identifier: " + config);

                return DataResult.success(new ConfigKey(registryId, configId));
            },
            configKey -> configKey.registryId + "/" + configKey.configId
    );
}
