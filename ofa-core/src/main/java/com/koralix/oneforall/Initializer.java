package com.koralix.oneforall;

import com.koralix.oneforall.command.OfaCommand;
import com.koralix.oneforall.config.loader.ConfigLoader;
import com.koralix.oneforall.config.loader.Storages;
import com.koralix.oneforall.session.LoginManager;
import com.koralix.oneforall.settings.PlayerSettings;
import com.koralix.oneforall.settings.ServerSettings;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;

public class Initializer implements ModInitializer {
    @Override
    public void onInitialize() {
        OneForAll.logger().info("Initializing {} v{}", OneForAll.id(), OneForAll.version());

        ConfigLoader loader = Storages.SERVER.create(OneForAll.id());
        ServerSettings.register().save(loader);
        PlayerSettings.register().save(loader);

        for (OneForAll ofa : FabricLoader.getInstance().getEntrypoints("ofa", OneForAll.class)) {
            ofa.onInitialize();
        }

        LoginManager.init();

        CommandRegistrationCallback.EVENT.register(OfaCommand::register);

        ServerLifecycleEvents.SERVER_STARTED.register(Storages::loadDeferred);
    }
}
