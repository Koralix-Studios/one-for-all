package com.koralix.oneforall.base.parser.ast;

import com.koralix.oneforall.base.parser.computable.ComputableNode;
import com.koralix.oneforall.base.parser.computable.ComputeUnit;
import com.koralix.oneforall.base.parser.computable.MacroComputableNode;
import com.koralix.oneforall.base.parser.computable.MacroRootComputableNode;

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
    public ComputableNode toComputable() {
        MacroRootComputableNode macroRoot = ComputeUnit.macro(macroIdentifier).orElseThrow(() -> new IllegalArgumentException("Unrecognized macro identifier."));

        return new MacroComputableNode(macroRoot);
    }
}
