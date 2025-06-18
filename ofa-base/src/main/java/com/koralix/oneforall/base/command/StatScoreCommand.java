package com.koralix.oneforall.base.command;

import com.koralix.oneforall.base.parser.ParseException;
import com.koralix.oneforall.base.parser.computable.ComputeUnit;
import com.koralix.oneforall.base.settings.CommandSettings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class StatScoreCommand {
    public static void register(
            @NotNull CommandDispatcher<ServerCommandSource> dispatcher,
            @NotNull CommandRegistryAccess registryAccess,
            @NotNull CommandManager.RegistrationEnvironment environment
    ) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("statscore")
                .requires(source -> CommandSettings.COMMAND_STATSCORE.value())
                .executes(StatScoreCommand::remove)
                .then(argument("title", TextArgumentType.text(registryAccess))
                        .then(argument("input", StringArgumentType.greedyString())
                                .executes(StatScoreCommand::execute)
                        )
                );

        dispatcher.register(literalArgumentBuilder);
    }

    private static int remove(CommandContext<ServerCommandSource> context) {
        ComputeUnit.disposeScoreboard();
        return 0;
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            ScoreboardObjective objective = ComputeUnit.scoreboard(TextArgumentType.getTextArgument(context, "title"), StringArgumentType.getString(context, "input"));
            objective.getScoreboard().setObjectiveSlot(ScoreboardDisplaySlot.SIDEBAR, objective);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

}
