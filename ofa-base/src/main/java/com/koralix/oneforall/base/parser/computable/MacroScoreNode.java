package com.koralix.oneforall.base.parser.computable;

import net.minecraft.stat.StatHandler;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public class MacroScoreNode extends ChainScoreNode {
    private final MacroRootScoreNode macro;

    public MacroScoreNode(@NotNull ScoreNode parent, @NotNull MacroRootScoreNode macro) {
        super(parent);
        this.macro = macro;
    }

    @Override
    protected @NotNull BigDecimal compute(@NotNull StatHandler handler) {
        return this.macro.execute(handler);
    }

    @Override
    public void attach() {
        macro.attach();
        macro.attach(this);
    }

    @Override
    public void detach() {
        macro.detach();
        macro.detach(this);
    }
}
