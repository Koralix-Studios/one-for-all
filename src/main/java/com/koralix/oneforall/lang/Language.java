package com.koralix.oneforall.lang;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koralix.oneforall.OneForAll;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static net.minecraft.datafixer.fix.BlockEntitySignTextStrictJsonFix.GSON;

public enum Language {
    SPANISH("es_es"),
    GALICIAN("gl_es");

    private static final Map<String, Language> LANGUAGES;

    static {
        Map<String, Language> languages = new HashMap<>();
        for (Language language : values()) {
            languages.put(language.code, language);
        }
        LANGUAGES = Map.copyOf(languages);
    }

    private final String code;
    private final Map<String, String> translations;

    Language(String code) throws IllegalArgumentException {
        this.code = code;

        OneForAll.getInstance().getLogger().info("Loading translations from {}", code);

        Map<String, String> translationMap = new HashMap<>();
        String languageFile = "/assets/oneforall/lang/" + code + ".json";

        try (InputStream inputStream = TranslationUnit.class.getResourceAsStream(languageFile)) {
            JsonObject jsonObject = GSON.fromJson(new InputStreamReader(inputStream, StandardCharsets.UTF_8), JsonObject.class);

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                translationMap.put(entry.getKey(), entry.getValue().getAsString());
            }

            this.translations = Map.copyOf(translationMap);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to load translations from " + code, e);
        }
    }

    public String getCode() {
        return code;
    }

    public boolean containsKey(String code) {
        return translations.containsKey(code);
    }

    public Optional<String> translate(String code) {
        return Optional.ofNullable(translations.get(code));
    }

    @Override
    public String toString() {
        return code;
    }

    public static Language fromCode(String s) {
        return LANGUAGES.get(s);
    }
}
