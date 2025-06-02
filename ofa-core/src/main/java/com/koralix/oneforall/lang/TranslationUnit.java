package com.koralix.oneforall.lang;

import com.koralix.oneforall.settings.ServerSettings;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ListIterator;

public class TranslationUnit {
    private static final ThreadLocal<@Nullable Language> LANGUAGE = ThreadLocal.withInitial(() -> null);

    public static void prepare(@NotNull Language language) {
        LANGUAGE.set(language);
    }

    public static void reset() {
        LANGUAGE.remove();
    }

    public static Text adapt(Text text) {
        Language language = LANGUAGE.get();
        if (language == null) return text;

        if (!text.getSiblings().isEmpty()) {
            ListIterator<Text> iterator = text.getSiblings().listIterator();

            while (iterator.hasNext()) {
                iterator.set(adapt(iterator.next()));
            }
        }

        if (!(text.getContent() instanceof TranslatableTextContent translatable)) return text;

        String key = translatable.getKey();
        String translation = language.translate(key)
                .or(() -> ServerSettings.DEFAULT_LANGUAGE.value().translate(key))
                .orElse(null);

        if (translation == null) return text;

        Object[] args = translatable.getArgs();

        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Text arg) {
                Text adapted = adapt(arg);
                if (adapted.getContent() instanceof TranslatableTextContent argContent) {
                    args[i] = argContent.getFallback() == null ? adapted : argContent.getFallback();
                }
            }
        }

        translation = String.format(language.locale(), translation, args);

        MutableText result = Text.translatableWithFallback(key, translation, translatable.getArgs());

        for (Text sibling : text.getSiblings()) {
            result.append(sibling);
        }

        return result;
    }
}
