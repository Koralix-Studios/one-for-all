package com.koralix.oneforall.base.mixin.oplevel;

import com.koralix.oneforall.base.settings.CommandSettings;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.OperatorEntry;
import net.minecraft.server.OperatorList;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.dedicated.command.OpCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Collection;

@Mixin(OpCommand.class)
public class OpCommandMixin {
    @Unique
    private static final DynamicCommandExceptionType INSUFFICIENT_LEVEL_EXCEPTION = new DynamicCommandExceptionType(o -> Text.translatable("commands.op.failed.level", o));

    @Shadow @Final private static SimpleCommandExceptionType ALREADY_OPPED_EXCEPTION;

    @ModifyExpressionValue(
            method = "register",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/brigadier/builder/RequiredArgumentBuilder;executes(Lcom/mojang/brigadier/Command;)Lcom/mojang/brigadier/builder/ArgumentBuilder;"
            ),
            remap = false
    )
    private static ArgumentBuilder<ServerCommandSource, ?> expandWithLevel(ArgumentBuilder<ServerCommandSource, ?> original) {
        return original.then(
                CommandManager.argument("level", IntegerArgumentType.integer(1, 4))
                        .requires(context -> CommandSettings.COMMAND_OP_LEVEL.get())
                        .executes(OpCommandMixin::setOpLevel)
        );
    }

    @Unique
    private static int setOpLevel(@NotNull CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Collection<GameProfile> targets = GameProfileArgumentType.getProfileArgument(context, "targets");
        int level = IntegerArgumentType.getInteger(context, "level");

        PlayerManager playerManager = source.getServer().getPlayerManager();
        OperatorList oplist = playerManager.getOpList();
        int i = 0;

        ServerPlayerEntity player = source.getPlayer();
        if (player != null) {
            OperatorEntry entry = oplist.get(player.getGameProfile());
            if (entry == null || entry.getPermissionLevel() < level) {
                throw INSUFFICIENT_LEVEL_EXCEPTION.create(level);
            }
        }

        for (GameProfile gameProfile : targets) {
            if (playerManager.isOperator(gameProfile)) {
                if (oplist.get(gameProfile).getPermissionLevel() != level) {
                    oplist.remove(gameProfile);
                    oplist.add(new OperatorEntry(gameProfile, level, oplist.canBypassPlayerLimit(gameProfile)));
                    ++i;
                    source.sendFeedback(() -> Text.translatable("commands.op.success.level", gameProfile.getName(), level), true);
                }
            } else {
                oplist.add(new OperatorEntry(gameProfile, level, oplist.canBypassPlayerLimit(gameProfile)));
                ++i;
                source.sendFeedback(/* method_52012 */ () -> Text.translatable("commands.op.success.level", gameProfile.getName(), level), true);
            }
        }

        if (i == 0) {
            throw ALREADY_OPPED_EXCEPTION.create();
        } else {
            return i;
        }
    }
}
