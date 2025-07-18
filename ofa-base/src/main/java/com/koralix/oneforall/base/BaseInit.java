package com.koralix.oneforall.base;

import com.koralix.oneforall.OFA;
import com.koralix.oneforall.base.command.*;
import com.koralix.oneforall.base.settings.CommandSettings;
import com.koralix.oneforall.base.settings.PlayerSettings;
import com.koralix.oneforall.base.settings.ServerSettings;
import com.koralix.oneforall.entry.OneForAll;
import com.koralix.oneforall.util.wrap.OnceCell;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.Version;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class BaseInit implements OneForAll {
    public static final OnceCell<OFA> OFA = new OnceCell<>();

    @Override
    public void setup(OFA ofa) {
        OFA.set(ofa);
        logger().info("{} initialized", id());
    }

    @Override
    public void onInitialize() {
        OneForAll.load(ServerSettings.class);
        OneForAll.load(CommandSettings.class);
        OneForAll.load(PlayerSettings.class);

        CommandRegistrationCallback.EVENT.register(Base2BaseCommand::register);
        CommandRegistrationCallback.EVENT.register(BatchCommand::register);
        CommandRegistrationCallback.EVENT.register(EnderchestCommand::register);
        CommandRegistrationCallback.EVENT.register(SignalCommand::register);
        CommandRegistrationCallback.EVENT.register(StatScoreCommand::register);
    }

    @Contract(pure = true)
    public static @NotNull Logger logger() {
        return OFA.get().logger();
    }

    @Contract(value = "_ -> new", pure = true)
    public static @NotNull Identifier id(String path) {
        return OFA.get().id(path);
    }

    @Contract(pure = true)
    public static @NotNull String id() {
        return OFA.get().id();
    }

    @Contract(pure = true)
    public static @NotNull Version version() {
        return OFA.get().version();
    }
}
