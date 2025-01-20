package com.koralix.oneforall.mixin.client.commands;

import com.koralix.oneforall.commands.CommonCommandSource;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(FabricClientCommandSource.class)
public class FabricClientCommandSourceMixin implements CommonCommandSource {
    @Unique
    private final FabricClientCommandSource self = (FabricClientCommandSource) this;

    @Override
    public Optional<PlayerEntity> player() {
        return Optional.ofNullable(self.getPlayer());
    }
}
