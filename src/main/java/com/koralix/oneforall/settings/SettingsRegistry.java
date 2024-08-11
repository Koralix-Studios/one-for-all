package com.koralix.oneforall.settings;

import com.koralix.oneforall.OneForAll;
import net.minecraft.util.Identifier;

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
    String namespace() default OneForAll.MOD_ID;
    Env env() default Env.SERVER;

    enum Env {
        /**
         * The settings registry is for the client
         */
        CLIENT,
        /**
         * The settings registry is for any server
         */
        SERVER,
        /**
         * The settings registry is for an integrated server
         */
        INTEGRATED,
        /**
         * The settings registry is for a dedicated server
         */
        DEDICATED
    }
}
