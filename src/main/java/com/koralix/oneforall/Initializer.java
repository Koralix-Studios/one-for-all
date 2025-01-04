package com.koralix.oneforall;

import com.koralix.oneforall.commands.Commands;
import com.koralix.oneforall.settings.PlayerSettings;
import com.koralix.oneforall.settings.ServerSettings;
import com.koralix.oneforall.settings.SettingsManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.random.RandomGenerator;

public class Initializer implements ModInitializer {
    public static final String MOD_ID = /*$ mod_id*/ "oneforall";
    public static final String MOD_VERSION = /*$ mod_version*/ "0.1.0";
    public static final String MOD_NAME = /*$ mod_name*/ "One For All";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static RandomGenerator rng() {
        return RandomGenerator.getDefault();
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing OneForAll...");

        CommandRegistrationCallback.EVENT.register(Commands::register);

        SettingsManager.register(ServerSettings.class);
        SettingsManager.register(PlayerSettings.class);
    }
}
