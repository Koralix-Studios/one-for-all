package com.koralix.oneforall.init;

import com.koralix.oneforall.OFA;
import com.koralix.oneforall.entry.OneForAll;
import com.koralix.oneforall.extension.ExtensionManager;
import com.koralix.oneforall.util.wrap.OnceCell;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Initializer implements ModInitializer, PreLaunchEntrypoint {
    private static final OnceCell<Initializer> INSTANCE = new OnceCell<>();
    private static final String MOD_ID = /*$ mod.id*/ "oneforall-core";
    private ExtensionManager extensionManager;
    private boolean initialized = false;
    private @Nullable MinecraftServer server = null;

    public static Initializer get() {
        return INSTANCE.get();
    }

    public static @NotNull Optional<Initializer> optional() {
        return INSTANCE.optional();
    }

    @Override
    public void onPreLaunch() {
        OneForAll.LOGGER.info("Setting up OneForAll...");
        INSTANCE.set(this);

        loadExtensions();

        initialized = true;
    }

    @Override
    public void onInitialize() {
        for (OneForAll ofa : FabricLoader.getInstance().getEntrypoints("ofa", OneForAll.class)) {
            ofa.onInitialize();
        }
    }

    private void loadExtensions() {
        OneForAll.LOGGER.debug("Loading extensions for OneForAll...");

        ExtensionManager.Builder builder = new ExtensionManager.Builder();

        List<Pair<OFA, OneForAll>> entrypoints = new ArrayList<>();
        boolean found = false;

        for (EntrypointContainer<OneForAll> container : FabricLoader.getInstance().getEntrypointContainers("ofa", OneForAll.class)) {
            OFA ofa = OFA.of(container.getProvider().getMetadata());
            builder.register(ofa);
            if (found) {
                container.getEntrypoint().setup(ofa);
                continue;
            }
            if (MOD_ID.equals(ofa.id())) {
                found = true;
                container.getEntrypoint().setup(ofa);
                for (Pair<OFA, OneForAll> entry : entrypoints) {
                    entry.getRight().setup(entry.getLeft());
                }
            } else {
                entrypoints.add(new Pair<>(ofa, container.getEntrypoint()));
            }
        }
        extensionManager = builder.build();

        StringBuilder extensionsList = new StringBuilder();
        extensionManager.forEach(ofa -> {
            extensionsList.append("\n\t- ").append(ofa.id());
        });

        OneForAll.LOGGER.info("OneForAll initialized with {} extensions:{}", extensionManager.count(), extensionsList);
    }

    private void assertInitialized() {
        if (!initialized) throw new IllegalStateException("Initializer has not been initialized yet.");
    }

    public @NotNull ExtensionManager getExtensionManager() {
        assertInitialized();
        return extensionManager;
    }

    public @NotNull Optional<MinecraftServer> server() {
        return Optional.ofNullable(server);
    }

    public void server(@Nullable MinecraftServer server) {
        this.server = server;
    }
}
