package com.koralix.oneforall.settings;

import net.minecraft.text.Text;

import java.util.Optional;

public interface ConfigValueView<T, C extends ConfigValue<T>> extends ConfigValue<T> {
    /**
     * The config id of this config id view.
     * This id is used to determine the config id of the config id view.
     *
     * @return the config id of this config id view
     */
    C configValue();

    /**
     * The default id of this config id.
     * This id is used to determine the default id of the config id.
     *
     * @return the default id of this config id
     */
    T defaultValue();

    /**
     * The default id of this config id.
     * This id is used to determine the default id of the config id.
     *
     * @param value the default id of this config id
     * @return the default id of this config id
     */
    Optional<Text> defaultValue(T value);

    /**
     * The id of this config id.
     * This id is used to determine the id of the config id.
     *
     * @return the id of this config id
     */
    T value();

    /**
     * The id of this config id.
     * This id is used to determine the id of the config id.
     *
     * @param value the id of this config id
     * @return the id of this config id
     */
    Optional<Text> value(T value);
}
