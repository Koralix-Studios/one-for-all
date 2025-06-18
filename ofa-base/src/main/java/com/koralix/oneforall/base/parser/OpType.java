package com.koralix.oneforall.base.parser;

public enum OpType {
    Multiplication,
    Division,
    Addition,
    Substraction;

    public static OpType fromTokenType(TokenType tokenType) {
        return switch (tokenType) {
            case Multiplication -> Multiplication;
            case Division -> Division;
            case Addition -> Addition;
            case Substraction -> Substraction;
            default -> throw new IllegalArgumentException("No OpType for tokenType: " + tokenType);
        };
    }
}
