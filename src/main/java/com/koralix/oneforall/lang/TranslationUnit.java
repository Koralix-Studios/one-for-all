package com.koralix.oneforall.lang;

import com.koralix.oneforall.network.ClientSession;
import com.koralix.oneforall.network.ServerLoginManager;
import com.koralix.oneforall.settings.ServerSettings;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import java.util.ListIterator;
import java.util.Optional;
import java.util.UUID;

public class TranslationUnit {
    private final static Language DEFAULT_LANGUAGE = Language.ENGLISH;
    private static Optional<UUID> TARGET_PLAYER = Optional.empty();

    public static String translate(Language lang, String code) {
        return lang.translate(code)
                .or(() -> ServerSettings.DEFAULT_LANGUAGE.value().translate(code))
                .or(() -> DEFAULT_LANGUAGE.translate(code))
                .orElse(code);
    }

    public static void prepare(UUID uuid) {
        TARGET_PLAYER = Optional.of(uuid);
    }

    public static Text adaptText(Text text) {
        Text result = text;

        ClientSession session = null;
        if (TARGET_PLAYER.isPresent()) {
            session = ServerLoginManager.SESSIONS.get(TARGET_PLAYER.get());
        }

        if (session != null && session.modVersion() != null) { // TODO: Version dependent translations
            return result;
        }

        if (!text.getSiblings().isEmpty()) {
            ListIterator<Text> iterator = text.getSiblings().listIterator();

            while (iterator.hasNext()) {
                iterator.set(adaptText(iterator.next()));
            }
        }

        if (text.getContent() instanceof TranslatableTextContent textContent) {
            String key = textContent.getKey();
            Language userLanguage = session == null
                    ? ServerSettings.DEFAULT_LANGUAGE.value()
                    : (session.language() != null ? session.language() : ServerSettings.DEFAULT_LANGUAGE.value());
            String translation = translate(userLanguage, key);
            MutableText content = Text.translatableWithFallback(key, translation);
            for (Text sibling: text.getSiblings()) {
                content.append(sibling);
            }
            result = content;
        }

        return result;
    }

}
