package com.koralix.oneforall.base.parser.ast;

import com.koralix.oneforall.base.parser.computable.ComputableNode;
import com.koralix.oneforall.base.parser.computable.ComputeUnit;
import com.koralix.oneforall.base.parser.computable.SubscriptorComputableNode;
import net.minecraft.registry.Registries;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class IdentifierExpr implements Expression {
    private String identifier;

    public IdentifierExpr(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    private static <T> Optional<ComputableNode> toStatComputable(StatType<T> type, Identifier identifier) {
        return type.getRegistry().getOptionalValue(identifier).map(key ->
            new SubscriptorComputableNode(n -> ComputeUnit.registerOnStat(type.getOrCreateStat(key), n))
        );
    }

    @Override
    public ComputableNode toComputable() {
        int i = identifier.indexOf(':');

        Optional<StatType<?>> opt = Registries.STAT_TYPE
            .getOptionalValue(Identifier.splitOn(identifier.substring(0, i), '.'));

        return opt
                .flatMap(type -> toStatComputable(type, Identifier.splitOn(identifier.substring(i + 1), '.')))
                .orElseThrow(() -> new RuntimeException("Couldn't transform to computable."));
    }
}
