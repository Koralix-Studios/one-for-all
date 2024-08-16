package com.koralix.oneforall.lang;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.network.ClientSession;
import com.koralix.oneforall.network.ServerLoginManager;
import com.koralix.oneforall.settings.ServerSettings;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

import static net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix.GSON;

public class TranslationUnit {
    private final static Map<String, Map<String, String>> TRANSLATIONS = new HashMap<>();
    private final static String DEFAULT_LANGUAGE = "es_es";
    private static Optional<UUID> TARGET_PLAYER = Optional.empty();

    static {
        Stream.of(
                "es_es",
                "gl_es"
        ).forEach(TranslationUnit::load);
    }

    public static void load(String lang) {
        OneForAll.getInstance().getLogger().info("Loading translations from {}", lang);

        Map<String, String> translationMap = new HashMap<>();
        String languageFile = "/assets/lang/" + lang + ".json";

        try (InputStream inputStream = TranslationUnit.class.getResourceAsStream(languageFile)) {
            JsonObject jsonObject = GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                translationMap.put(entry.getKey(), entry.getValue().getAsString());
            }

            TRANSLATIONS.put(lang, translationMap);
        } catch (Exception e) {
            OneForAll.getInstance().getLogger().error("Failed to load translations from {}", lang, e);
        }
    }

    public static String translate(String lang, String code) {
        if (TRANSLATIONS.containsKey(lang) && TRANSLATIONS.get(lang).containsKey(code)) {
            return TRANSLATIONS.get(lang).get(code);
        } else if (TRANSLATIONS.containsKey(ServerSettings.DEFAULT_LANGUAGE.value()) && TRANSLATIONS.get(ServerSettings.DEFAULT_LANGUAGE.value()).containsKey(code)) {
            return TRANSLATIONS.get(ServerSettings.DEFAULT_LANGUAGE.value()).get(code);
        } else {
            return TRANSLATIONS.get(DEFAULT_LANGUAGE).get(code);
        }
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

        if (session == null || session.modVersion() == null) {
            if (!text.getSiblings().isEmpty()) {
                ListIterator<Text> iterator = text.getSiblings().listIterator();

                while (iterator.hasNext()) {
                    iterator.set(adaptText(iterator.next()));
                }
            }

            if (text.getContent() instanceof TranslatableTextContent textContent) {
                if (TRANSLATIONS.get(DEFAULT_LANGUAGE).containsKey(textContent.getKey())) {
                    String userLanguage = session != null
                            ? (session.language() != null ? session.language() : ServerSettings.DEFAULT_LANGUAGE.value())
                            : ServerSettings.DEFAULT_LANGUAGE.value();
                    String translation = translate(userLanguage, textContent.getKey());
                    MutableText content = Text.translatableWithFallback(textContent.getKey(), translation);
                    for (Text sibling: text.getSiblings()) {
                        content.append(sibling);
                    }

                    result = content;
                }
            }
        }

        return result;
    }

    public static boolean hasLanguage(String language) {
        return TRANSLATIONS.containsKey(language);
    }

}
