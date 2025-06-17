package com.koralix.oneforall.base;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.base.command.*;
import com.koralix.oneforall.base.settings.CommandSettings;
import com.koralix.oneforall.base.settings.Features;
import com.koralix.oneforall.base.settings.PlayerSettings;
import com.koralix.oneforall.base.settings.ServerSettings;
import com.koralix.oneforall.config.loader.ConfigLoader;
import com.koralix.oneforall.config.loader.Storages;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class Initializer implements OneForAll {
    @Override
    public void onInitialize() {
        OneForAll.logger().info("Base mod initialized successfully.");

        ConfigLoader loader = Storages.SERVER.create(OneForAll.id());
        ServerSettings.register().save(loader);
        CommandSettings.register().save(loader);
        PlayerSettings.register().save(loader);
        Features.register();

        CommandRegistrationCallback.EVENT.register(Base2BaseCommand::register);
        CommandRegistrationCallback.EVENT.register(BatchCommand::register);
        CommandRegistrationCallback.EVENT.register(EnderchestCommand::register);
        CommandRegistrationCallback.EVENT.register(SignalCommand::register);
        CommandRegistrationCallback.EVENT.register(TestComputableAstCommand::register);
    }
}
