package com.koralix.oneforall.lang;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koralix.oneforall.OneForAll;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public enum Language {
    ENGLISH("en_us", Locale.ENGLISH);

    private static final Map<String, Language> LANGUAGES;
    public static final Codec<Language> CODEC = Codec.STRING.comapFlatMap(
            s -> {
                Language language = fromCode(s);
                return language == null
                        ? DataResult.error(() -> "Unknown language code: " + s)
                        : DataResult.success(language);
            },
            Language::toString
    );
    public static final PacketCodec<ByteBuf, Language> PACKET_CODEC = PacketCodecs.STRING.xmap(
            s -> {
                Language language = fromCode(s);
                if (language == null) throw new DecoderException("Unknown language code: " + s);
                return language;
            },
            Language::toString
    );

    static {
        Map<String, Language> languages = new HashMap<>();
        for (Language language : values()) {
            languages.put(language.code, language);
        }
        LANGUAGES = Map.copyOf(languages);
    }

    private final String code;
    private final Locale locale;
    private final Map<String, String> translations;

    Language(String code, Locale locale) {
        this.code = code;
        this.locale = locale;

        OneForAll.logger().info("Loading translations from {}", code);

        Map<String, String> translationMap = new HashMap<>();

        Gson GSON = new Gson();
        OneForAll.INTERNAL_DATA.extensions().forEach(ofa -> load(GSON, ofa.id(), code, translationMap));

        this.translations = Map.copyOf(translationMap);
    }

    private static void load(Gson GSON, String modId, String code, Map<String, String> translationMap) {
        String languageFile = "/assets/" + modId + "/lang/" + code + ".json";
        try (InputStream inputStream = Language.class.getResourceAsStream(languageFile)) {
            JsonObject jsonObject = GSON.fromJson(new InputStreamReader(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8), JsonObject.class);

            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                translationMap.put(entry.getKey(), entry.getValue().getAsString());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to load translations from " + code, e);
        }
    }

    public String code() {
        return this.code;
    }

    public Locale locale() {
        return this.locale;
    }

    public boolean hasTranslation(String key) {
        return this.translations.containsKey(key);
    }

    public @NotNull Optional<String> translate(String key) {
        return Optional.ofNullable(this.translations.get(key));
    }

    @Override
    public String toString() {
        return code;
    }

    public static Language fromCode(String s) {
        return LANGUAGES.get(s);
    }
}
