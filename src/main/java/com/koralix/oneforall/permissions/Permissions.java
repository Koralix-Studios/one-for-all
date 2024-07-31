package com.koralix.oneforall.permissions;

import net.minecraft.server.command.ServerCommandSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Predicate;

public class Permissions {
    private static final Map<UUID, PermissionSet> permissions = new HashMap<>();

    public static Predicate<ServerCommandSource> hasPermission(String permission) {
        return commandSource -> commandSource.hasPermissionLevel(4)
                || (commandSource.isExecutedByPlayer()
                && commandSource.getPlayer() != null
                && permissions.get(commandSource.getPlayer().getUuid()).contains(permission));
    }
}
