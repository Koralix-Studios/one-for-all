package com.koralix.oneforall.base.parser.ast;

import com.koralix.oneforall.base.parser.computable.ComputeUnit;
import com.koralix.oneforall.base.parser.computable.MacroRootScoreNode;
import com.koralix.oneforall.base.parser.computable.MacroScoreNode;
import com.koralix.oneforall.base.parser.computable.ScoreNode;
import org.jetbrains.annotations.NotNull;

public class MacroExpr implements Expression {
    private String macroIdentifier;

    public MacroExpr(String macroIdentifier) {
        this.macroIdentifier = macroIdentifier;
    }

    public String getMacroIdentifier() {
        return macroIdentifier;
    }

    public void setMacroIdentifier(String macroIdentifier) {
        this.macroIdentifier = macroIdentifier;
    }

    @Override
    public @NotNull ScoreNode toScoreNode(@NotNull ScoreNode parent) {
        MacroRootScoreNode macroRoot = ComputeUnit.macro(macroIdentifier).orElseThrow(() -> new IllegalArgumentException("Unrecognized macro identifier."));

        return new MacroScoreNode(parent, macroRoot);
    }
}
