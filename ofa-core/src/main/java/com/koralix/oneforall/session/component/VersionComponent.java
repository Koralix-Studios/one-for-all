package com.koralix.oneforall.session.component;

import com.koralix.oneforall.OneForAll;
import com.koralix.oneforall.session.SessionComponent;
import com.koralix.oneforall.session.SessionComponentType;
import net.fabricmc.loader.api.SemanticVersion;
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

    /**
     * Checks if the current version is compatible with the OneForAll version.
     * In order to be considered compatible, the current version must comply with the following conditions:
     * - The major version must be equal to the OneForAll major version.
     * - The minor version must be greater than or equal to the OneForAll minor version.
     *
     * @return true if the current version is compatible, false otherwise.
     */
    public boolean isCompatible() {
        if (!(OneForAll.version() instanceof SemanticVersion currentSemVer)) return false;
        if (!(this.version instanceof SemanticVersion componentSemVer)) return false;
        if (currentSemVer.getVersionComponentCount() < 2 || componentSemVer.getVersionComponentCount() < 2) return false;
        return currentSemVer.getVersionComponent(0) == componentSemVer.getVersionComponent(0) &&
               currentSemVer.getVersionComponent(1) <= componentSemVer.getVersionComponent(1);
    }

    @Override
    public String toString() {
        return "VersionComponent{" +
                "version='" + version + '\'' +
                '}';
    }
}
