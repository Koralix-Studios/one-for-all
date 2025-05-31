package com.koralix.oneforall;

import com.koralix.oneforall.command.OfaCommand;
import com.koralix.oneforall.config.loader.Storages;
import com.koralix.oneforall.session.LoginManager;
import com.koralix.oneforall.settings.ServerSettings;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

public class Initializer implements ModInitializer {
    @Override
    public void onInitialize() {
        OneForAll.logger().info("Initializing {} v{}", OneForAll.id(), OneForAll.version());

        ServerSettings.register().save(Storages.SERVER);

        for (OneForAll ofa : FabricLoader.getInstance().getEntrypoints("ofa", OneForAll.class)) {
            ofa.onInitialize();
        }

        LoginManager.init();

        CommandRegistrationCallback.EVENT.register(OfaCommand::register);
    }
}
