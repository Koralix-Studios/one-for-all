package com.koralix.oneforall.base.parser.computable;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

public class FuncCallComputableNode extends ComputableNode {
    private final Method method;
    private final ComputableNode[] params;

    public FuncCallComputableNode(String functionName, ComputableNode... params) {
        this.params = params;

        for (ComputableNode node : params) {
            node.parent(this);
        }

        @SuppressWarnings("unchecked")
        Class<BigDecimal>[] args = new Class[params.length];
        Arrays.fill(args, BigDecimal.class);

        try {
            this.method = FuncCallComputableNode.class.getDeclaredMethod(functionName, args);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BigDecimal execute(@NotNull UUID uuid) {
        if (this.cached != null) return this.cached;

        BigDecimal[] a = Arrays.stream(this.params).map(e -> e.execute(uuid)).toArray(BigDecimal[]::new);

        try {
            return this.cached = (BigDecimal) this.method.invoke(null, (Object[]) a);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe() {
        for (ComputableNode param : params) {
            param.subscribe();
        }
    }

    @Override
    public void unsubscribe() {
        for (ComputableNode param : params) {
            param.unsubscribe();
        }
    }


    private static BigDecimal log(BigDecimal base, BigDecimal number) {
        return BigDecimal.valueOf(Math.log(number.doubleValue())/Math.log(base.doubleValue()));
    }
}
