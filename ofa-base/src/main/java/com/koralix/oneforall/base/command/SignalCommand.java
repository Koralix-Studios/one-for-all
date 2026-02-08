package com.koralix.oneforall.base.command;

import com.koralix.oneforall.base.settings.CommandSettings;
import com.koralix.oneforall.base.utils.SignalBlock;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class SignalCommand {
    private static final DynamicCommandExceptionType EXCEPTION = new DynamicCommandExceptionType(e -> Text.of(((IllegalArgumentException) e).getMessage()));

    public static void register(
            @NotNull CommandDispatcher<ServerCommandSource> dispatcher,
            @NotNull CommandRegistryAccess registryAccess,
            @NotNull CommandManager.RegistrationEnvironment environment
    ) {
        LiteralArgumentBuilder<ServerCommandSource> command = literal("signal")
                .requires(source -> CommandSettings.COMMAND_SIGNAL.get() && source.hasPermissionLevel(2));

        RequiredArgumentBuilder<ServerCommandSource, Integer> ss = argument("ss", IntegerArgumentType.integer(0));

        ss.executes(context -> execute(context, SignalBlock.AUTO));

        for (SignalBlock signalBlock : SignalBlock.values()) {
            if (signalBlock == SignalBlock.AUTO) continue;

            ss.then(literal(signalBlock.toString().toLowerCase()).executes(context -> execute(context, signalBlock)));
        }

        command.then(ss);

        dispatcher.register(command);
    }

    private static int execute(@NotNull CommandContext<ServerCommandSource> context, @NotNull SignalBlock signalBlock) throws CommandSyntaxException {
        try {
            int signalStrength = IntegerArgumentType.getInteger(context, "ss");
            ItemStack itemStack = signalBlock.getItemStack(signalStrength);
            context.getSource().getPlayerOrThrow().giveItemStack(itemStack);
            return 1;
        } catch (IllegalArgumentException e) {
            throw EXCEPTION.create(e);
        }
    }
}
