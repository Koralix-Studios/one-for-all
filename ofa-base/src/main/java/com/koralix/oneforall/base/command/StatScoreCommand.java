package com.koralix.oneforall.base.command;

import com.koralix.oneforall.base.settings.CommandSettings;
import com.koralix.oneforall.base.statscore.StatNode;
import com.koralix.oneforall.base.statscore.StatScore;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class StatScoreCommand {
    public static void register(
            @NotNull CommandDispatcher<ServerCommandSource> dispatcher,
            @NotNull CommandRegistryAccess registryAccess,
            @NotNull CommandManager.RegistrationEnvironment environment
    ) {
        LiteralArgumentBuilder<ServerCommandSource> root = literal("statscore")
                .requires(source -> CommandSettings.COMMAND_STATSCORE.get())
                .executes(StatScoreCommand::remove);

        root.then(argument("input", StringArgumentType.greedyString())
                .suggests((context, builder) ->
                        CommandSource.suggestMatching(
                                Stream.concat(
                                        Stream.of(
                                                "#mined",
                                                "#pickaxe"
                                        ),
                                        Registries.STAT_TYPE.stream()
                                                .flatMap(statType -> StreamSupport.stream(statType.spliterator(), false))
                                                .map(ScoreboardCriterion::getName)
                                ),
                                builder
                        ))
                .executes(StatScoreCommand::execute)
        );

        dispatcher.register(root);
    }

    private static int remove(CommandContext<ServerCommandSource> context) {
        return StatScore.dispose() ? 1 : 0;
    }

    private static int execute(CommandContext<ServerCommandSource> context) {
        String input = StringArgumentType.getString(context, "input").trim();

        StatNode node = StatScore.parse(input);

        StatScore.create(context.getSource().getServer(), node);

        return 0;
    }

}
