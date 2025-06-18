package com.koralix.oneforall.base.parser.computable;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class MacroComputableNode extends ComputableNode {
    private final MacroRootComputableNode ref;

    public MacroComputableNode(MacroRootComputableNode ref) {
        this.ref = ref;
    }

    @Override
    public BigDecimal execute(@NotNull UUID uuid) {
        return ref.execute(uuid);
    }

    @Override
    public void subscribe() {
        ref.subscribe();
    }

    @Override
    public void unsubscribe() {
        ref.unsubscribe();
    }
}
