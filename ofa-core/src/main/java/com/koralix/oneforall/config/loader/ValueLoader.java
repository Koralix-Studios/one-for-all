package com.koralix.oneforall.config.loader;

import com.koralix.oneforall.config.ConfigValue;

public class ValueLoader {
    public record ConfigDataEntry<V>(ConfigValue<V> configValue, V value) {}
}
