package com.koralix.oneforall.base.parser;

import com.koralix.oneforall.base.parser.ast.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final Lexer lexer;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    public Expression parse() throws ParseException {
        return parse(0);
    }

    public Expression parse(int bindingPower) throws ParseException {
        Token token = lexer.next();
        Expression lhs = switch (token.type()) {
            case Identifier -> new IdentifierExpr(token.content());
            case Name -> {
                lexer.expect(TokenType.OpenParen);

                List<Expression> arguments = parseExpressionList();
                yield new FuncCallExpr(token.content(), arguments);
            }
            case Integer -> new IntegerExpr(Integer.parseInt(token.content()));
            case Double -> new DoubleExpr(Double.parseDouble(token.content()));
            case Macro -> new MacroExpr(token.content().substring(1));
            case Substraction -> {
                Expression expr = parse(0);
                yield new UnaryExpr(expr, OpType.Substraction);
            }
            case OpenParen -> {
                Expression expr = parse(0);
                lexer.expect(TokenType.CloseParen);
                yield expr;
            }
            default -> throw new ParseException(token.loc(), "Cannot start with operation.");
        };

        while (true) {
            Token op = lexer.peek();
            if (op == null || !op.isBinop()) break;

            Pair<Integer, Integer> bp = op.bindingPower();
            int left_bp = bp.getLeft();
            int right_bp = bp.getRight();

            if (left_bp < bindingPower) break;

            lexer.next();
            Expression rhs = parse(right_bp);

            lhs = new BinopExpr(lhs, rhs, OpType.fromTokenType(op.type()));
        }

        return lhs;
    }

    public List<Expression> parseExpressionList() throws ParseException {
        List<Expression> exprList = new ArrayList<>();

        while (true) {
            exprList.add(parse(0));
            Token token = lexer.peek();

            if (token == null || token.type() == TokenType.CloseParen) break;

            lexer.expect(TokenType.Comma);
        }
        lexer.next();

        return exprList;
    }
}
