package com.koralix.oneforall.base.parser;

public enum TokenType {
    Multiplication,
    Addition,
    Division,
    Substraction,
    OpenParen,
    CloseParen,
    Comma,

    Macro,
    Integer,
    Double,
    Identifier,
    Name;

    @Override
    public String toString() {
        return switch (this) {
            case Multiplication -> "Multiplication";
            case Addition -> "Addition";
            case Division -> "Division";
            case Substraction -> "Substraction";
            case OpenParen -> "OpenParen";
            case CloseParen -> "CloseParen";
            case Comma -> "Comma";
            case Macro -> "Macro";
            case Integer -> "Integer";
            case Double -> "Double";
            case Identifier -> "Identifier";
            case Name -> "Name";
        };
    }
}
