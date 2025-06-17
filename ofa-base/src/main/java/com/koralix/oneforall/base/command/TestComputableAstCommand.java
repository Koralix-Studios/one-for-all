package com.koralix.oneforall.base.command;

import com.koralix.oneforall.base.parser.Lexer;
import com.koralix.oneforall.base.parser.ParseException;
import com.koralix.oneforall.base.parser.Parser;
import com.koralix.oneforall.base.parser.computable.ComputableNode;
import com.koralix.oneforall.base.settings.CommandSettings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TestComputableAstCommand {
    public static void register(
            @NotNull CommandDispatcher<ServerCommandSource> dispatcher,
            @NotNull CommandRegistryAccess registryAccess,
            @NotNull CommandManager.RegistrationEnvironment environment
    ) {
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("statscore").executes(TestComputableAstCommand::execute);

        dispatcher.register(literalArgumentBuilder);
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        try {
            ComputableNode ast = new Parser(new Lexer("#mined/#tick")).parse().toComputable();
            ComputableNode ast2 = new Parser(new Lexer("#mined")).parse().toComputable();
            ComputableNode ast3 = new Parser(new Lexer("#tick")).parse().toComputable();

            UUID uuid = context.getSource().getPlayerOrThrow().getUuid();

            ast.subscribe();

            BigDecimal result = ast.execute(uuid);
            BigDecimal result2 = ast2.execute(uuid);
            BigDecimal result3 = ast3.execute(uuid);

            ast.unsubscribe();

            context.getSource().sendFeedback(() -> Text.of(String.format("#mined/#tick: %s\n#mined: %s\n#tick: %s", result, result2, result3)), false);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return 0;
    }

}
