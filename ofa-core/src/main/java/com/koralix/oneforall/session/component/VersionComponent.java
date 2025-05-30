package com.koralix.oneforall.session.component;

import com.koralix.oneforall.session.SessionComponent;
import com.koralix.oneforall.session.SessionComponentType;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import org.jetbrains.annotations.NotNull;

public record VersionComponent(Version version) implements SessionComponent<VersionComponent> {
    public static final Type TYPE = new Type();
    public static final class Type implements SessionComponentType<VersionComponent> {}

    public VersionComponent(String version) throws VersionParsingException {
        this(Version.parse(version));
    }

    @Override
    public @NotNull SessionComponentType<VersionComponent> type() {
        return TYPE;
    }

    @Override
    public String toString() {
        return "VersionComponent{" +
                "version='" + version + '\'' +
                '}';
    }
}
