package com.koralix.oneforall.base.parser;

public record Token(TokenType type, String content, int loc) {
    public boolean isBinop() {
        return switch (type) {
            case Multiplication, Addition, Division, Substraction -> true;
            default -> false;
        };
    }

    public Pair<Integer, Integer> bindingPower() {
        return switch (type) {
            case Multiplication, Division -> new Pair<>(40, 41);
            case Addition, Substraction -> new Pair<>(30, 31);
            case OpenParen, CloseParen -> new Pair<>(20, 21);
            default -> new Pair<>(0, 1);
        };
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", content='" + content + '\'' +
                ", loc=" + loc +
                '}';
    }
}
