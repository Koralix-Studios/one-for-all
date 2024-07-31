package com.koralix.oneforall.settings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation to mark a class as a settings registry.
 * A settings registry is a class that contains config values.
 * The config values are stored in the settings registry.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SettingsRegistry {
    String id();
}
