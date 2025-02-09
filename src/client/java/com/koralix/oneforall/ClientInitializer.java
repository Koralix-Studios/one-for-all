package com.koralix.oneforall;

import com.koralix.oneforall.commands.ClientCommands;
import com.koralix.oneforall.network.ClientLoginManager;
import com.koralix.oneforall.settings.ClientSettings;
import com.koralix.oneforall.settings.SettingsManager;
import com.koralix.oneforall.settings.registry.ConfigValueEnvironment;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class ClientInitializer implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        OneForAll.LOGGER.info("Initializing OneForAll client...");

        ClientCommandRegistrationCallback.EVENT.register(ClientCommands::register);

        SettingsManager.register(OneForAll.id("client"), ClientSettings.class, ConfigValueEnvironment.CLIENT);
        SettingsManager.load(ConfigValueEnvironment.CLIENT);

        ClientLoginManager.init();
    }
}
