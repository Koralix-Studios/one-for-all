package com.koralix.oneforall.settings.registry;

public record ConfigValueEnvironment(byte flags) {
    public static final ConfigValueEnvironment CLIENT = new ConfigValueEnvironment((byte) 0b00);
    public static final ConfigValueEnvironment SERVER = new ConfigValueEnvironment((byte) 0b10);
    public static final ConfigValueEnvironment INTEGRATED = new ConfigValueEnvironment((byte) 0b10);
    public static final ConfigValueEnvironment DEDICATED = new ConfigValueEnvironment((byte) 0b11);

    public boolean server() {
        return (flags & SERVER.flags) == SERVER.flags;
    }
}
