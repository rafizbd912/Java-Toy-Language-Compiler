import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int pos = 0;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public AstNode parse() throws ParserException {
        AstNode rootNode = new AstNode(AstNodeType.PROGRAM);

        while (pos < tokens.size()) {
            rootNode.addChild(statement());
        }

        return rootNode;
    }

    private AstNode statement() throws ParserException {
        if (match(TokenType.ID) && match(TokenType.ASSIGN)) {
            AstNode assignNode = new AstNode(AstNodeType.ASSIGN);
            assignNode.addChild(new AstNode(AstNodeType.ID, previousToken().getValue()));
            assignNode.addChild(expr());
            consume(TokenType.SEMI);
            return assignNode;
        } else if (match(TokenType.IF)) {
            AstNode ifNode = new AstNode(AstNodeType.IF);
            consume(TokenType.LPAREN);
            ifNode.addChild(expr());
            consume(TokenType.RPAREN);
            ifNode.addChild(statement());
            if (match(TokenType.ELSE)) {
                ifNode.addChild(statement());
            }
            return ifNode;
        } else if (match(TokenType.WHILE)) {
            AstNode whileNode = new AstNode(AstNodeType.WHILE);
            consume(TokenType.LPAREN);
            whileNode.addChild(expr());
            consume(TokenType.RPAREN);
            whileNode.addChild(statement());
            return whileNode;
        } else if (match(TokenType.PRINT)) {
            AstNode printNode = new AstNode(AstNodeType.PRINT);
            consume(TokenType.LPAREN);
            printNode.addChild(expr());
            consume(TokenType.RPAREN);
            consume(TokenType.SEMI);
            return printNode;
        } else if (match(TokenType.FUNCTION)) {
            AstNode functionNode = new AstNode(AstNodeType.FUNCTION);
            functionNode.addChild(new AstNode(AstNodeType.ID, previousToken().getValue()));
            consume(TokenType.LPAREN);
            while (match(TokenType.ID)) {
                functionNode.addChild(new AstNode(AstNodeType.PARAM, previousToken().getValue()));
                if (!match(TokenType.COMMA)) {
                    break;
                }
            }
            consume(TokenType.RPAREN);
            functionNode.addChild(statement());
            return functionNode;
        } else {
            throw new ParserException("Unexpected token: " + peek().getType());
        }
    }
    private AstNode expr() throws ParserException {
        AstNode exprNode = term();

        while (match(TokenType.PLUS) || match(TokenType.MINUS)) {
            Token opToken = consume(match(TokenType.PLUS) ? TokenType.PLUS : TokenType.MINUS);
            AstNode termNode = term();
            AstNode binOpNode = new AstNode(AstNodeType.BIN_OP, opToken.getType());
            binOpNode.addChild(exprNode);
            binOpNode.addChild(termNode);
            exprNode = binOpNode;
        }

        return exprNode;
    }

    private AstNode term() throws ParserException {
        AstNode termNode = factor();

        while (match(TokenType.MULT) || match(TokenType.DIV)) {
            Token opToken = consume(match(TokenType.MULT) ? TokenType.MULT : TokenType.DIV);
            AstNode factorNode = factor();
            AstNode binOpNode = new AstNode(AstNodeType.BIN_OP, opToken.getType());
            binOpNode.addChild(termNode);
            binOpNode.addChild(factorNode);
            termNode = binOpNode;
        }

        return termNode;
    }

    private AstNode factor() throws ParserException {
        Token token = consume(TokenType.NUM, TokenType.ID, TokenType.LPAREN);

        if (token.getType() == TokenType.NUM) {
            return new AstNode(AstNodeType.NUM, token.getValue());
        } else if (token.getType() == TokenType.ID) {
            if (match(TokenType.LPAREN)) {
                AstNode callNode = new AstNode(AstNodeType.CALL);
                callNode.addChild(new AstNode(AstNodeType.ID, token.getValue()));
                while (!match(TokenType.RPAREN)) {
                    callNode.addChild(expr());
                    if (!match(TokenType.COMMA)) {
                        break;
                    }
                }
                return callNode;
            } else {
                return new AstNode(AstNodeType.ID, token.getValue());
            }
        } else if (token.getType() == TokenType.LPAREN) {
            AstNode exprNode = expr();
            consume(TokenType.RPAREN);
            return exprNode;
        } else {
            throw new ParserException("Unexpected token: " + token.getType());
        }
    }

    private Token consume(TokenType... expectedTypes) throws ParserException {
        Token token = peek();

        for (TokenType expectedType : expectedTypes) {
            if (match(expectedType)) {
                return token;
            }
        }

        throw new ParserException("Unexpected token: " + token.getType());
    }

    private Token peek() throws ParserException {
        if (pos >= tokens.size()) {
            throw new ParserException("Unexpected end of input");
        }

        return tokens.get(pos);
    }

    private boolean match(TokenType expectedType) {
        if (pos >= tokens.size()) {
            return false;
        }

        Token token = tokens.get(pos);

        if (token.getType() == expectedType) {
            pos++;
            return true;
        } else {
            return false;
        }
    }

    private Token previousToken() {
        return tokens.get(pos - 1);
    }
}
