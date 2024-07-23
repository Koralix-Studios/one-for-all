package com.koralix.oneforall;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.lang.reflect.InvocationTargetException;

public class Initializer implements ModInitializer {
    public static final OneForAll instance = createInstance();

    private static OneForAll createInstance() {
        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
            return ServerInitializer.instance();
        }

        try {
            return (OneForAll) Class.forName("com.koralix.oneforall.ClientInitializer")
                    .getDeclaredMethod("instance")
                    .invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onInitialize() {
        instance.onInitialize();
    }
}
