package com.koralix.oneforall.base.parser.computable;

import com.koralix.oneforall.base.parser.Expr2Node;
import net.minecraft.stat.StatHandler;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.function.BiFunction;

public class BinOpScoreNode extends ChainScoreNode {
    private final ScoreNode left;
    private final ScoreNode right;
    private final BiFunction<BigDecimal, BigDecimal, BigDecimal> operator;

    public BinOpScoreNode(
            @NotNull ScoreNode parent,
            @NotNull Expr2Node left,
            @NotNull Expr2Node right,
            @NotNull BiFunction<BigDecimal, BigDecimal, BigDecimal> operator
    ) {
        super(parent, left, right);
        this.left = left.get(this);
        this.right = right.get(this);
        this.operator = operator;
    }

    @Override
    public @NotNull BigDecimal compute(@NotNull StatHandler handler) {
        return operator.apply(left.execute(handler), right.execute(handler));
    }

    @Override
    public void attach() {
        left.attach();
        right.attach();
    }

    @Override
    public void detach() {
        left.detach();
        right.detach();
    }
}
