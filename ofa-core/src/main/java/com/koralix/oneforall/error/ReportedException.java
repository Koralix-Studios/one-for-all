package com.koralix.oneforall.error;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.text.MutableText;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ReportedException extends RuntimeException {
    private static final DynamicCommandExceptionType COMMAND_EXCEPTION_TYPE = new DynamicCommandExceptionType(o -> {
        ExceptionReport report = (ExceptionReport) o;
        return (report.title() instanceof MutableText ? (MutableText) report.title() : report.title().copy())
                .append("\n")
                .append(report.description())
                .append("\n")
                .append(report.details());
    });
    private final ExceptionReport report;

    ReportedException(@NotNull ExceptionReport report, @NotNull String message) {
        super(message);
        this.report = report;
    }

    ReportedException(@NotNull ExceptionReport report, @NotNull Throwable cause) {
        super(cause);
        this.report = report;
    }

    @Contract("-> new")
    public @NotNull CommandSyntaxException command() {
        return COMMAND_EXCEPTION_TYPE.create(this.report);
    }

    @Contract("_ -> new")
    public @NotNull CommandSyntaxException command(@NotNull ImmutableStringReader reader) {
        return COMMAND_EXCEPTION_TYPE.createWithContext(reader, this.report);
    }
}
