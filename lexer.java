import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String input;
    private int pos = 0;

    public Lexer(String input) {
        this.input = input;
    }

    public List<Token> tokenize() throws LexerException {
        List<Token> tokens = new ArrayList<>();

        while (pos < input.length()) {
            char c = input.charAt(pos);

            if (Character.isDigit(c)) {
                String value = Integer.toString(parseNumber());
                tokens.add(new Token(TokenType.NUM, value));
            } else if (Character.isLetter(c)) {
                String value = parseIdentifier();
                TokenType type = TokenType.KEYWORD_MAP.getOrDefault(value, TokenType.ID);
                tokens.add(new Token(type, value));
            } else if (c == '+' || c == '-') {
                tokens.add(new Token(c == '+' ? TokenType.PLUS : TokenType.MINUS));
                pos++;
            } else if (c == '=') {
                if (match('=')) {
                    tokens.add(new Token(TokenType.EQ));
                } else {
                    tokens.add(new Token(TokenType.ASSIGN));
                }
            } else if (c == '<') {
                if (match('=')) {
                    tokens.add(new Token(TokenType.LE));
                } else {
                    tokens.add(new Token(TokenType.LT));
                }
            } else if (c == '>') {
                if (match('=')) {
                    tokens.add(new Token(TokenType.GE));
                } else {
                    tokens.add(new Token(TokenType.GT));
                }
            } else if (c == '!') {
                if (match('=')) {
                    tokens.add(new Token(TokenType.NE));
                } else {
                    throw new LexerException("Unexpected token: !");
                }
            } else if (c == '(') {
                tokens.add(new Token(TokenType.LPAREN));
                pos++;
            } else if (c == ')') {
                tokens.add(new Token(TokenType.RPAREN));
                pos++;
            } else if (c == '{') {
                tokens.add(new Token(TokenType.LBRACE));
                pos++;
            } else if (c == '}') {
                tokens.add(new Token(TokenType.RBRACE));
                pos++;
            } else if (c == ';') {
                tokens.add(new Token(TokenType.SEMI));
                pos++;
            } else if (Character.isWhitespace(c)) {
                pos++;
            } else {
                throw new LexerException("Unexpected character: " + c);
            }
        }

        return tokens;
    }

    private int parseNumber() {
        int value = 0;

        while (pos < input.length() && Character.isDigit(input.charAt(pos))) {
            value = value * 10 + (input.charAt(pos) - '0');
            pos++;
        }

        return value;
    }

    private String parseIdentifier() {
        StringBuilder builder = new StringBuilder();

        while (pos < input.length() && Character.isLetterOrDigit(input.charAt(pos))) {
            builder.append(input.charAt(pos));
            pos++;
        }

        return builder.toString();
    }

    private boolean match(char expected) {
        if (pos < input.length() && input.charAt(pos) == expected) {
            pos++;
            return true;
        } else {
            return false;
        }
    }
}
