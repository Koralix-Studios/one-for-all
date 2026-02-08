package com.koralix.oneforall.error;

import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record ExceptionReport(
        Text title,
        Text description,
        Text details
) {
    @Contract("-> new")
    public static @NotNull ReportBuilder builder() {
        return new ReportBuilder();
    }

    @Contract("_ -> new")
    public @NotNull ReportedException exception(@NotNull String message) {
        return new ReportedException(this, message);
    }

    @Contract("_ -> new")
    public @NotNull ReportedException exception(@NotNull Throwable cause) {
        return new ReportedException(this, cause);
    }

    public static final class ReportBuilder {
        private Text title;
        private Text description;
        private Text details;

        private ReportBuilder() {
            // Private constructor to enforce usage of the builder pattern
        }

        public ReportBuilder title(Text title) {
            this.title = title;
            return this;
        }

        public ReportBuilder description(Text description) {
            this.description = description;
            return this;
        }

        public ReportBuilder details(Text details) {
            this.details = details;
            return this;
        }

        @Contract(" -> new")
        public @NotNull ExceptionReport build() {
            return new ExceptionReport(title, description, details);
        }
    }
}
