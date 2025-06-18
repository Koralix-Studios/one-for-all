package com.koralix.oneforall.base.parser.computable;

import com.koralix.oneforall.base.parser.Expr2Node;
import net.minecraft.stat.StatHandler;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.function.Function;

public class UnaryOpScoreNode extends ChainScoreNode {
    private final ScoreNode child;
    private final Function<BigDecimal, BigDecimal> operator;

    public UnaryOpScoreNode(
            @NotNull ScoreNode parent,
            @NotNull Expr2Node child,
            @NotNull Function<BigDecimal, BigDecimal> operator
    ) {
        super(parent, child);
        this.child = child.get(this);
        this.operator = operator;
    }

    @Override
    public @NotNull BigDecimal compute(@NotNull StatHandler handler) {
        return operator.apply(child.execute(handler));
    }

    @Override
    public void attach() {
        child.attach();
    }

    @Override
    public void detach() {
        child.detach();
    }
}
