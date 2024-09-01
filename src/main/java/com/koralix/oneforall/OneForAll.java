package com.koralix.oneforall;

import com.koralix.oneforall.commands.Commands;
import com.koralix.oneforall.platform.ModMetadata;
import com.koralix.oneforall.platform.Platform;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class OneForAll {
    public static final String MOD_ID = "oneforall";

    private final Logger logger = LoggerFactory.getLogger("OneForAll");
    private final Platform platform;
    private final ModMetadata metadata;

    public static OneForAll getInstance() {
        return Initializer.instance;
    }

    public OneForAll(Platform platform) {
        this.platform = platform;
        this.metadata = platform.getMetadata(MOD_ID);
    }

    public final void initialize() {
        CommandRegistrationCallback.EVENT.register(Commands::register);

        onInitialize();
    }

    abstract void onInitialize();

    public Logger getLogger() {
        return logger;
    }

    public Platform getPlatform() {
        return platform;
    }

    public ModMetadata getMetadata() {
        return metadata;
    }
}
