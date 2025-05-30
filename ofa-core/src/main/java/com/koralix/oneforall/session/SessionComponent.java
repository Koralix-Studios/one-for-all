package com.koralix.oneforall.session;

import org.jetbrains.annotations.NotNull;

public interface SessionComponent<T extends SessionComponent<T>> {
    @NotNull SessionComponentType<T> type();
}
