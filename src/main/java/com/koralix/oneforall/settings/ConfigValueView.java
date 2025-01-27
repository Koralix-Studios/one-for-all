package com.koralix.oneforall.settings;

import net.minecraft.text.Text;

import java.util.Optional;

public interface ConfigValueView<T> {
    /**
     * The default value of this config value view.
     * This value is used when the config value is not present in the config file.
     *
     * @return the default value of this config value view
     */
    T defaultValue();

    /**
     * The default value of this config value view.
     * This value is used when the config value is not present in the config file.
     *
     * @param value the default value of this config value view
     * @return the default value of this config value view
     */
    Optional<Text> defaultValue(T value);

    /**
     * The value of this config value view.
     * This value is used to determine the value of the config value view.
     *
     * @return the value of this config value view
     */
    T value();

    /**
     * The value of this config value view.
     * This value is used to determine the value of the config value view.
     *
     * @param value the value of this config value view
     * @return the value of this config value view
     */
    Optional<Text> value(T value);
}
