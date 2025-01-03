package com.koralix.oneforall.network;

import com.koralix.oneforall.lang.Language;

public class ClientSession {
    private Language language;
    private String modVersion;

    public Language language() {
        return language;
    }

    public void language(Language language) {
        this.language = language;
    }

    public String modVersion() {
        return modVersion;
    }

    public void modVersion(String modVersion) {
        this.modVersion = modVersion;
    }

    @Override
    public String toString() {
        return "ClientSession{" +
                "language='" + language + '\'' +
                ", modVersion='" + modVersion + '\'' +
                '}';
    }
}
