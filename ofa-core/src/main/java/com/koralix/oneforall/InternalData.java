package com.koralix.oneforall;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

import static com.koralix.oneforall.OneForAll.INTERNAL_DATA;

public class InternalData {
    private static final String MOD_ID = /*$ mod.id*/ "oneforall-core";
    private static final Pattern DEV_MOD_URL_PATTERN = Pattern.compile("classes/[^/]+/[^/]+/$");

    private Map<String, OFA> ofaIdMap = new HashMap<>();
    private Map<String, OFA> ofaUrlMap = new HashMap<>();
    private OFA core;

    InternalData() {
        this.core = this.register(
                this.getClass().getProtectionDomain().getCodeSource().getLocation().toString(),
                OFA.of(FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata())
        );
    }

    @Contract("_, _ -> param2")
    private @NotNull OFA register(@NotNull String url, @NotNull OFA ofa) {
        this.ofaIdMap.put(ofa.id(), ofa);
        this.ofaUrlMap.put(url, ofa);
        return ofa;
    }

    @Contract("_, _ -> new")
    @Nullable OFA register(@NotNull String url, @NotNull ModMetadata metadata) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            url = DEV_MOD_URL_PATTERN.matcher(url).replaceAll("");
        }
        if (this.ofaUrlMap.containsKey(url)) {
            core.logger().warn("Duplicate OneForAll entrypoint found for URL: {}", url);
            return null;
        }
        return this.register(url, OFA.of(metadata));
    }

    void freeze() {
        this.ofaIdMap = Map.copyOf(this.ofaIdMap);
        this.ofaUrlMap = Map.copyOf(this.ofaUrlMap);
    }

    public boolean containsId(@NotNull String id) {
        return this.ofaIdMap.containsKey(id);
    }

    public boolean containsUrl(@NotNull String url) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            url = DEV_MOD_URL_PATTERN.matcher(url).replaceAll("");
        }
        return this.ofaUrlMap.containsKey(url);
    }

    public @Nullable OFA ofId(@NotNull String id) {
        return this.ofaIdMap.get(id);
    }

    public @Nullable OFA ofUrl(@NotNull String url) {
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            url = DEV_MOD_URL_PATTERN.matcher(url).replaceAll("");
        }
        return this.ofaUrlMap.get(url);
    }

    OFA instance() {
        StackWalker walker = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
        return walker.walk(stream -> stream
                .skip(2)
                .map(StackWalker.StackFrame::getDeclaringClass)
                .map(clazz -> clazz.getProtectionDomain().getCodeSource().getLocation().toString())
                .filter(INTERNAL_DATA::containsUrl)
                .findFirst()
                .map(INTERNAL_DATA::ofUrl)
                .orElseGet(() -> walker.walk(stream1 -> stream1
                        .skip(2)
                        .map(StackWalker.StackFrame::getMethodName)
                        .filter(methodName -> methodName.indexOf('$') != -1)
                        .flatMap(methodName -> {
                            List<String> parts = Arrays.asList(methodName.split("\\$"));
                            Collections.reverse(parts);
                            return parts.stream();
                        })
                        .filter(INTERNAL_DATA::containsId)
                        .findFirst()
                        .map(INTERNAL_DATA::ofId)
                        .orElseThrow(() -> new IllegalCallerException("Cannot determine mod ID from stack trace."))
                )));
    }
}
