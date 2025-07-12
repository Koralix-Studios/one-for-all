package com.koralix.oneforall.base.command;

import com.koralix.oneforall.base.settings.CommandSettings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class EnderchestCommand {
    public static void register(
            @NotNull CommandDispatcher<ServerCommandSource> dispatcher,
            @NotNull CommandRegistryAccess registryAccess,
            @NotNull CommandManager.RegistrationEnvironment environment
    ) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("enderchest")
                .requires(source -> CommandSettings.COMMAND_ENDERCHEST.get() && source.hasPermissionLevel(3))
                .then(argument("player", EntityArgumentType.player())
                        .executes(context -> open(context.getSource(), EntityArgumentType.getPlayer(context, "player")))
                );

        dispatcher.register(literalArgumentBuilder);
    }

    private static int open(@NotNull ServerCommandSource source, @NotNull ServerPlayerEntity player) throws CommandSyntaxException {
        source.getPlayerOrThrow().openHandledScreen(new SimpleNamedScreenHandlerFactory(
                (i, playerInventory, playerEntity) -> GenericContainerScreenHandler.createGeneric9x3(
                        i,
                        playerInventory,
                        player.getEnderChestInventory()
                ),
                player.getDisplayName()
        ));

        return 1;
    }
}
