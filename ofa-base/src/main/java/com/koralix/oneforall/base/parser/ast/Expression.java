package com.koralix.oneforall.base.parser.ast;

import com.koralix.oneforall.base.parser.computable.ComputableNode;

public interface Expression {
    ComputableNode toComputable();
}
