package com.koralix.oneforall.base.parser.ast;

import com.koralix.oneforall.base.parser.computable.ScoreNode;
import com.koralix.oneforall.base.parser.computable.StatScoreNode;
import net.minecraft.registry.Registries;
import net.minecraft.stat.StatType;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

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

    private static <T> Optional<ScoreNode> toStatScoreNode(@NotNull ScoreNode parent, StatType<T> type, Identifier identifier) {
        return type.getRegistry().getOptionalValue(identifier).map(key ->
            new StatScoreNode(parent, type.getOrCreateStat(key))
        );
    }

    @Override
    public @NotNull ScoreNode toScoreNode(@NotNull ScoreNode parent) {
        int i = identifier.indexOf(':');

        Optional<StatType<?>> opt = Registries.STAT_TYPE
            .getOptionalValue(Identifier.splitOn(identifier.substring(0, i), '.'));

        return opt
                .flatMap(type -> toStatScoreNode(parent, type, Identifier.splitOn(identifier.substring(i + 1), '.')))
                .orElseThrow(() -> new RuntimeException("Couldn't transform to computable."));
    }
}
