package com.koralix.oneforall.settings;

public interface MonoConfigValue<T, C extends MonoConfigValue<T, C>> extends ConfigValue<T>, ConfigValueView<T, C> {

}
