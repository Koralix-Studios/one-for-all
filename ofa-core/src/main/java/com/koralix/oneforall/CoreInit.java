package com.koralix.oneforall;

import com.koralix.oneforall.command.OfaCommand;
import com.koralix.oneforall.entry.OneForAll;
import com.koralix.oneforall.session.LoginManager;
import com.koralix.oneforall.settings.ServerSettings;
import com.koralix.oneforall.settings.PlayerSettings;
import com.koralix.oneforall.util.wrap.OnceCell;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.Version;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public class CoreInit implements OneForAll {
    public static final OnceCell<OFA> OFA = new OnceCell<>();

    @Override
    public void setup(OFA ofa) {
        OFA.set(ofa);
        logger().info("{} initialized", id());
    }

    @Override
    public void onInitialize() {
        OneForAll.load(ServerSettings.class);
        OneForAll.load(PlayerSettings.class);

        LoginManager.init();

        CommandRegistrationCallback.EVENT.register(OfaCommand::register);
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
