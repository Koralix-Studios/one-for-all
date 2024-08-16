package com.koralix.oneforall.network;

public class ClientSession {
    private String language;
    private String modVersion;

    public String language() {
        return language;
    }

    public void language(String language) {
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
