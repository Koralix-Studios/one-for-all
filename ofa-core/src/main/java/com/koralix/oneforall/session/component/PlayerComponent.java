package com.koralix.oneforall.session.component;

import com.koralix.oneforall.session.SessionComponent;
import com.koralix.oneforall.session.SessionComponentType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.NotNull;

public record PlayerComponent(ServerPlayerEntity player) implements SessionComponent<PlayerComponent> {
    public static final Type TYPE = new Type();

    @Override
    public @NotNull SessionComponentType<PlayerComponent> type() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "PlayerComponent{" +
                "player=" + player +
                '}';
    }

    public static final class Type implements SessionComponentType<PlayerComponent> {
    }
}
