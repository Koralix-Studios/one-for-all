package com.koralix.oneforall.session.component;

import com.koralix.oneforall.session.SessionComponent;
import com.koralix.oneforall.session.SessionComponentType;
import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.NotNull;

public record GameProfileComponent(GameProfile profile) implements SessionComponent<GameProfileComponent> {
    public static final Type TYPE = new GameProfileComponent.Type();
    public static final class Type implements SessionComponentType<GameProfileComponent> {}

    @Override
    public @NotNull SessionComponentType<GameProfileComponent> type() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "LangComponent{" +
                "profile='" + profile.toString() + '\'' +
                '}';
    }
}
