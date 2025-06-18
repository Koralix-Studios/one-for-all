package com.koralix.oneforall.base.parser.computable;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stat.StatHandler;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.function.Supplier;

public class TickScoreNode extends ChainScoreNode implements ServerTickEvents.EndTick {
    private final Supplier<Long> tickSupplier;

    public TickScoreNode(@NotNull ScoreNode parent, @NotNull Supplier<Long> tickSupplier) {
        super(parent, false);
        this.tickSupplier = tickSupplier;
        ServerTickEvents.END_SERVER_TICK.register(this);
    }

    @Override
    public @NotNull BigDecimal compute(@NotNull StatHandler handler) {
        return BigDecimal.valueOf(tickSupplier.get());
    }

    @Override
    public void attach() {

    }

    @Override
    public void detach() {

    }

    @Override
    public void onEndTick(MinecraftServer server) {
        reset(null);
    }
}
