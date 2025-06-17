package com.koralix.oneforall.base.parser.computable;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class SubscriptorComputableNode extends ComputableNode {
    private final Consumer<SubscriptorComputableNode> subscribe;
    private final Map<UUID, BigDecimal> playerCache;

    public SubscriptorComputableNode(Consumer<SubscriptorComputableNode> subscribe) {
        this.subscribe = subscribe;
        this.playerCache = new HashMap<>();
    }

    @Override
    public BigDecimal execute(@NotNull UUID uuid) {
        return this.playerCache.getOrDefault(uuid, this.cached == null ? BigDecimal.ZERO : this.cached);
    }

    @Override
    public void subscribe() {
        this.subscribe.accept(this);
    }

    @Override
    public void unsubscribe() {
        ComputeUnit.unregister(this);
    }

    public void set(BigDecimal value) {
        this.reset();
        this.cached = value;
    }

    public void set(BigDecimal value, UUID uuid) {
        this.reset();
        this.playerCache.put(uuid, value);
    }

    @Override
    public void reset() {
        super.reset();

        this.playerCache.clear();
    }
}
