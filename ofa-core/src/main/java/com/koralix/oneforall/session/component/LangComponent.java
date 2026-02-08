package com.koralix.oneforall.session.component;

import com.koralix.oneforall.lang.Language;
import com.koralix.oneforall.session.SessionComponent;
import com.koralix.oneforall.session.SessionComponentType;
import org.jetbrains.annotations.NotNull;

public record LangComponent(Language language) implements SessionComponent<LangComponent> {
    public static final Type TYPE = new Type();

    @Override
    public @NotNull SessionComponentType<LangComponent> type() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "LangComponent{" +
                "language='" + language + '\'' +
                '}';
    }

    public static final class Type implements SessionComponentType<LangComponent> {
    }
}
