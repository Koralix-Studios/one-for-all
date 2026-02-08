package com.koralix.oneforall.lang;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koralix.oneforall.CoreInit;
import com.koralix.oneforall.config.ConfigCommandAdapter;
import com.koralix.oneforall.entry.OneForAll;
import com.koralix.oneforall.init.Initializer;
import com.koralix.oneforall.session.Session;
import com.koralix.oneforall.session.component.GameProfileComponent;
import com.koralix.oneforall.session.component.LangComponent;
import com.koralix.oneforall.session.component.PlayerComponent;
import com.koralix.oneforall.settings.PlayerSettings;
import com.koralix.oneforall.settings.ServerSettings;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.IntFunction;

public enum Language implements StringIdentifiable {
    ENGLISH("en_us", Locale.ENGLISH),
    SPANISH("es_es", Locale.forLanguageTag("es-ES"));

    public static final @NotNull Codec<Language> CODEC = StringIdentifiable.createCodec(Language::values);
    public static final ConfigCommandAdapter<Language> COMMAND_ADAPTER = ConfigCommandAdapter.ofEnum(Language.class);
    private static final Map<String, Language> LANGUAGES;
    private static final IntFunction<Language> BY_ID = ValueLists.createIndexToValueFunction(
            Language::ordinal, values(), ValueLists.OutOfBoundsHandling.WRAP
    );
    public static final PacketCodec<ByteBuf, Language> PACKET_CODEC = PacketCodecs.indexed(BY_ID, Language::ordinal);

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

        CoreInit.logger().info("Loading translations from {}", code);

        Map<String, String> translationMap = new HashMap<>();

        Gson GSON = new Gson();
        Initializer.get().getExtensionManager().forEach(ofa -> load(GSON, ofa.id(), code, translationMap));

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

    public static @NotNull Language of(@NotNull Session session) {
        LangComponent langComponent = session.get(LangComponent.TYPE);
        if (langComponent != null) return langComponent.language();
        GameProfileComponent profileComponent = session.get(GameProfileComponent.TYPE);
        return profileComponent == null
                ? ServerSettings.DEFAULT_LANGUAGE.get()
                : session.has(PlayerComponent.TYPE)
                ? PlayerSettings.DEFAULT_LANGUAGE
                .get(session.get(PlayerComponent.TYPE).player())
                .orElse(ServerSettings.DEFAULT_LANGUAGE.get())
                : ServerSettings.DEFAULT_LANGUAGE.get();
    }

    public static Language fromCode(String s) {
        return LANGUAGES.get(s);
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

    @Override
    public String asString() {
        return code;
    }
}
