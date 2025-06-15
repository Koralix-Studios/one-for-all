package com.koralix.oneforall.base.command;

import com.google.common.collect.Lists;
import com.koralix.oneforall.base.settings.CommandSettings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BatchCommand {
    public static void register(
            @NotNull CommandDispatcher<ServerCommandSource> dispatcher,
            @NotNull CommandRegistryAccess registryAccess,
            @NotNull CommandManager.RegistrationEnvironment environment
    ) {
        LiteralCommandNode<ServerCommandSource> root = dispatcher.register(literal("batch").requires(source -> CommandSettings.COMMAND_BATCH.value()));

        LiteralArgumentBuilder<ServerCommandSource> command = literal("batch")
                .then(argument("size", IntegerArgumentType.integer(1))
                        .fork(root, context -> {
                            List<ServerCommandSource> list = Lists.newArrayList();

                            for (int i = 0; i < IntegerArgumentType.getInteger(context, "size"); i++) {
                                list.add(context.getSource());
                            }

                            return list;
                        })
                ).then(literal("run").redirect(dispatcher.getRoot()));

        dispatcher.register(command);
    }
}
