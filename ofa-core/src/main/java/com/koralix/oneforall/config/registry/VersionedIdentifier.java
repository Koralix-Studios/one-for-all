package com.koralix.oneforall.config.registry;

import net.fabricmc.loader.api.Version;
import net.minecraft.util.Identifier;

public record VersionedIdentifier(Version version, Identifier identifier) {
}
