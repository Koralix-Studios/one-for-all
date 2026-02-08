package com.koralix.oneforall.session;

import org.jetbrains.annotations.NotNull;

public interface SessionHolder {
    @NotNull Session get();
}
