package com.koralix.oneforall.base.parser.computable;

import com.koralix.oneforall.base.parser.Expr2Node;
import net.minecraft.stat.StatHandler;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;

public class FuncCallScoreNode extends ChainScoreNode {
    private final Method method;
    private final ScoreNode[] params;

    public FuncCallScoreNode(@NotNull ScoreNode parent, @NotNull String functionName, @NotNull Expr2Node @NotNull... params) {
        super(parent, params);
        this.params = Arrays.stream(params)
                .map(e -> e.get(this))
                .toArray(ScoreNode[]::new);

        @SuppressWarnings("unchecked")
        Class<BigDecimal>[] args = new Class[params.length];
        Arrays.fill(args, BigDecimal.class);

        try {
            this.method = FuncCallScoreNode.class.getDeclaredMethod(functionName, args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected @NotNull BigDecimal compute(@NotNull StatHandler handler) {
        BigDecimal[] a = Arrays.stream(this.params).map(e -> e.execute(handler)).toArray(BigDecimal[]::new);

        try {
            return (BigDecimal) this.method.invoke(null, (Object[]) a);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Contract("_, _ -> new")
    private static @NotNull BigDecimal log(@NotNull BigDecimal base, @NotNull BigDecimal number) {
        return BigDecimal.valueOf(Math.log(number.doubleValue())/Math.log(base.doubleValue()));
    }

    @Override
    public void attach() {
        for (ScoreNode param : this.params) {
            param.attach();
        }
    }

    @Override
    public void detach() {
        for (ScoreNode param : this.params) {
            param.detach();
        }
    }
}
