package com.koralix.oneforall.base.parser;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    private final String code;
    private Token current_token;
    private int loc;

    public Lexer(String code) {
        this.code = code;
        this.loc = 0;
    }

    private Token advanceLexer() {
        skipWhitespaces();

        if (loc >= code.length()) return null;
        char c = code.charAt(loc);

        switch (c) {
            case '*': return new Token(TokenType.Multiplication, "*", loc++);
            case '+': return new Token(TokenType.Addition, "+", loc++);
            case '/': return new Token(TokenType.Division, "/", loc++);
            case '-': return new Token(TokenType.Substraction, "-", loc++);
            case '(': return new Token(TokenType.OpenParen, "(", loc++);
            case ')': return new Token(TokenType.CloseParen, ")", loc++);
            case ',': return new Token(TokenType.Comma, ",", loc++);
            case '#': {
                int token_loc = loc++;
                String identifier = lexName();
                Objects.requireNonNull(identifier);

                loc += identifier.length();

                return new Token(TokenType.Macro, "#" + identifier, token_loc);
            }
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9': {
                int token_loc = loc;
                String number = lexNumber();
                Objects.requireNonNull(number);
                loc += number.length();

                if (number.contains(".")) {
                    return new Token(TokenType.Double, number, token_loc);
                }

                return new Token(TokenType.Integer, number, token_loc);
            }
            default: {
                String identifier = lexIdentifier();

                if (identifier != null) {
                    int token_loc = loc;
                    loc += identifier.length();

                    return new Token(TokenType.Identifier, identifier, token_loc);
                } else {
                    String name = lexName();
                    Objects.requireNonNull(name);

                    int token_loc = loc;
                    loc += name.length();

                    return new Token(TokenType.Name, name, token_loc);
                }
            }

        }
    }

    private void skipWhitespaces() {
        while (loc < code.length() && Character.isWhitespace(code.charAt(loc))) {
            loc += 1;
        }
    }

    public Token next() {
        Token current_token = peek();
        this.current_token = advanceLexer();

        return current_token;
    }

    public Token peek() {
        if (current_token == null) {
            current_token = advanceLexer();
        }

        return current_token;
    }

    public boolean hasNext() {
        if (current_token == null && loc < code.length()) {
            current_token = advanceLexer();
        }

        return current_token != null;
    }

    private String lexIdentifier() {
        Pattern pattern = Pattern.compile("^[a-z0-9_.-]+:[a-z0-9/._-]+");
        Matcher matcher = pattern.matcher(code.substring(loc));

        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    private String lexNumber() {
        Pattern pattern = Pattern.compile("\\d+(?:\\.\\d+)?");
        Matcher matcher = pattern.matcher(code.substring(loc));

        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    private String lexName() {
        Pattern pattern = Pattern.compile("\\$*[a-zA-Z][a-zA-Z0-9_]*");
        Matcher matcher = pattern.matcher(code.substring(loc));

        if (matcher.find()) {
            return matcher.group();
        }

        return null;
    }

    public Token expect(TokenType type) {
        if (peek() == null || peek().type() != type) throw new RuntimeException("Expected " + type + ", got " + peek().type());
        return next();
    }
}
