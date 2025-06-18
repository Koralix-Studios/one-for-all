package com.koralix.oneforall.base.parser.ast;

import com.koralix.oneforall.base.parser.computable.ScoreNode;
import org.jetbrains.annotations.NotNull;

public interface Expression {
    @NotNull ScoreNode toScoreNode(@NotNull ScoreNode parent);
}
