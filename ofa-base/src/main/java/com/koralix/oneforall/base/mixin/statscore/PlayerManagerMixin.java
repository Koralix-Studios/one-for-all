package com.koralix.oneforall.base.mixin.statscore;

import com.koralix.oneforall.base.duck.OpenUserCache;
import com.koralix.oneforall.base.duck.StatHandlerGetter;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.path.PathUtil;
import net.minecraft.world.PlayerSaveHandler;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@Mixin(PlayerManager.class)
@Implements(@Interface(iface = StatHandlerGetter.class, prefix = "StatHandlerGetter$"))
public abstract class PlayerManagerMixin implements StatHandlerGetter {
    @Shadow @Final private Map<UUID, ServerStatHandler> statisticsMap;

    @Shadow @Final private MinecraftServer server;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void init(
            MinecraftServer server,
            CombinedDynamicRegistries<ServerDynamicRegistryType> registryManager,
            PlayerSaveHandler saveHandler,
            int maxPlayers,
            CallbackInfo ci
    ) {
        ((OpenUserCache) server.getUserCache()).profiles()
                .map(profile -> Map.entry(
                        profile.getId(),
                        this.createStatHandler(profile)
                ))
                .forEach(entry -> this.statisticsMap.put(entry.getKey(), entry.getValue()));
    }

    public ServerStatHandler createStatHandler(GameProfile profile) {
        UUID uUID = profile.getId();
        ServerStatHandler serverStatHandler = this.statisticsMap.get(uUID);
        if (serverStatHandler == null) {
            File file = this.server.getSavePath(WorldSavePath.STATS).toFile();
            File file2 = new File(file, uUID + ".json");
            if (!file2.exists()) {
                File file3 = new File(file, profile.getName() + ".json");
                Path path = file3.toPath();
                if (PathUtil.isNormal(path) && PathUtil.isAllowedName(path) && path.startsWith(file.getPath()) && file3.isFile()) {
                    file3.renameTo(file2);
                }
            }

            serverStatHandler = new ServerStatHandler(this.server, file2);
            this.statisticsMap.put(uUID, serverStatHandler);
        }

        return serverStatHandler;
    }

    @WrapWithCondition(
            method = "remove",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 1
            )
    )
    private boolean skipStatHandlerRemove(Map<UUID, ServerStatHandler> instance, Object o) {
        return false;
    }

    @Unique
    public ServerStatHandler StatHandlerGetter$get(PlayerEntity player) {
        return this.statisticsMap.get(player.getUuid());
    }

    @Unique
    public ServerStatHandler StatHandlerGetter$get(UUID uuid) {
        return this.statisticsMap.get(uuid);
    }
}
