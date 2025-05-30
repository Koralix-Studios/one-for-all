package com.koralix.oneforall;

import com.koralix.oneforall.util.Functions;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public interface OneForAll {
    String MOD_ID = /*$ mod.id*/ "oneforall";
    String MOD_NAME = /*$ mod.name*/ "One For All";
    String MOD_DESCRIPTION = /*$ mod.description*/ "One Mod to rule them all, One Mod to find them, One Mod to bring them all and in the darkness bind them.";
    String MOD_VERSION = /*$ mod.version*/ "0.1.0";

    Logger LOGGER = LoggerFactory.getLogger(OneForAll.MOD_NAME);
    Version VERSION = Functions.tryCatch(Version::parse).apply(MOD_VERSION);
    Random RANDOM = new Random();
    InternalData INTERNAL_DATA = new InternalData();

    @Contract(value = "_ -> new", pure = true)
    static @NotNull Identifier id(@NotNull String path) {
        StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        ModMetadata metadata = walker.walk(stream -> stream
                .skip(1)
                .map(StackWalker.StackFrame::getDeclaringClass)
                .map(clazz -> clazz.getProtectionDomain().getCodeSource().getLocation().toString())
                .filter(INTERNAL_DATA::containsMod)
                .findFirst()
                .map(INTERNAL_DATA::getMetadata)
                .orElseThrow(() -> new IllegalCallerException("Cannot determine mod ID from stack trace.")));
        return Identifier.of(metadata.getId(), path);
    }

    void onInitialize();
}
