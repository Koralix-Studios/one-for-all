package com.koralix.oneforall.base.command;

import com.koralix.oneforall.base.BaseInit;
import com.koralix.oneforall.base.settings.CommandSettings;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class Base2BaseCommand {
    private static final SimpleCommandExceptionType PARSE_EXCEPTION = new SimpleCommandExceptionType(
            Text.stringifiedTranslatable("command." + BaseInit.id() + ".base2base.parse_error")
    );
    private static final SimpleCommandExceptionType BIG_PART_EXCEPTION = new SimpleCommandExceptionType(
            Text.stringifiedTranslatable("command." + BaseInit.id() + ".base2base.big_part_error")
    );

    private static final Collection<String> BINARY = List.of(
            "0", "1"
    );
    private static final Collection<String> OCTAL = List.of(
            "0", "1", "2", "3", "4", "5", "6", "7"
    );
    private static final Collection<String> DECIMAL = List.of(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    );
    private static final Collection<String> HEXADECIMAL = List.of(
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"
    );

    public static void register(
            @NotNull CommandDispatcher<ServerCommandSource> dispatcher,
            @NotNull CommandRegistryAccess registryAccess,
            @NotNull CommandManager.RegistrationEnvironment environment
    ) {
        LiteralArgumentBuilder<ServerCommandSource> command = literal("base2base")
                .requires(source -> CommandSettings.COMMAND_BASE2BASE.get())
                .then(argument("from", IntegerArgumentType.integer(1))
                        .then(argument("to", IntegerArgumentType.integer(1))
                                .then(argument("value", StringArgumentType.greedyString())
                                        .suggests((context, builder) -> CommandSource.suggestMatching(suggest(context, builder), builder))
                                        .executes(Base2BaseCommand::execute)
                                )
                        )
                );

        dispatcher.register(command);
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        String input = StringArgumentType.getString(context, "value");
        int fromBase = IntegerArgumentType.getInteger(context, "from");

        BigInteger value = parse(input, fromBase);

        int toBase = IntegerArgumentType.getInteger(context, "to");
        StringBuilder str;
        if (toBase == 1) {
            str = new StringBuilder();
            for (BigInteger i = BigInteger.ZERO; i.compareTo(value) < 0; i = i.add(BigInteger.ONE)) {
                str.append("1");
            }
        } else if (toBase >= 2 && toBase <= 36) {
            str = new StringBuilder(value.toString(toBase));
        } else {
            str = new StringBuilder();
            BigInteger bigBase = BigInteger.valueOf(toBase);
            BigInteger remainder;
            BigInteger quotient = value;
            while (quotient.compareTo(BigInteger.ZERO) > 0) {
                BigInteger[] ints = quotient.divideAndRemainder(bigBase);
                quotient = ints[0];
                remainder = ints[1];
                str.insert(0, remainder.toString() + (str.isEmpty() ? "" : ","));
            }
        }

        ServerCommandSource source = context.getSource();
        source.sendFeedback(
                () -> Text.stringifiedTranslatable("command." + BaseInit.id() + ".base2base.result",
                        fromBase,
                        input,
                        toBase,
                        str.toString()
                ), false
        );

        if (value.bitLength() > 32) return Integer.MAX_VALUE;
        return value.intValue();
    }

    private static @NotNull BigInteger parse(@NotNull String value, int base) throws CommandSyntaxException {
        BigInteger bigBase = BigInteger.valueOf(base);
        String[] parts = value.split(",");

        int acc = parts.length - 1;
        BigInteger result = BigInteger.ZERO;
        for (String part : parts) {
            if (part.isEmpty()) throw PARSE_EXCEPTION.create();
            BigInteger parsedPart = parsePart(part, base > 36 ? 10 : base);

            if (parts.length > 1 && parsedPart.compareTo(bigBase) >= 0) {
                throw BIG_PART_EXCEPTION.create();
            }

            result = result.add(parsedPart.multiply(bigBase.pow(acc--)));
        }
        return result;
    }

    @Contract("_, _ -> new")
    private static @NotNull BigInteger parsePart(String part, int base) throws CommandSyntaxException {
        if (base == 1) return BigInteger.valueOf(part.length());
        try {
            return new BigInteger(part, base);
        } catch (NumberFormatException e) {
            throw PARSE_EXCEPTION.create();
        }
    }

    private static Collection<String> suggest(@NotNull CommandContext<ServerCommandSource> context, @NotNull SuggestionsBuilder builder) throws CommandSyntaxException {
        int fromBase = IntegerArgumentType.getInteger(context, "from");
        BigInteger bigBase = BigInteger.valueOf(fromBase);

        Collection<String> chars = switch (fromBase) {
            case 1, 2 -> BINARY;
            case 8 -> OCTAL;
            case 16 -> HEXADECIMAL;
            default -> DECIMAL;
        };

        String input = builder.getRemaining();
        if (input == null || input.isEmpty()) return chars;
        if (input.endsWith(",")) return extend(input, chars);
        return extend(input, merge(chars, ","))
                .stream()
                .filter(s -> {
                    if (s.endsWith(",")) return true;
                    if (!s.contains(",")) return true;
                    try {
                        return parsePart(s.substring(s.lastIndexOf(",") + 1), fromBase > 36 ? 10 : fromBase).compareTo(bigBase) < 0;
                    } catch (CommandSyntaxException e) {
                        return false;
                    }
                })
                .toList();
    }

    private static Collection<String> extend(@NotNull String input, @NotNull Collection<String> extensions) {
        if (input.isEmpty()) return extensions;
        return extensions.stream()
                .map(ext -> input + ext)
                .toList();
    }

    private static @NotNull Collection<String> merge(@NotNull Collection<String> collection, String... extra) {
        List<String> merged = new ArrayList<>(collection);
        Collections.addAll(merged, extra);
        return merged;
    }
}
