import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;

public class Compilador {
    static Hashtable<String, Symbol> symbolTable = new Hashtable<String, Symbol>();
    static final String[] reservedWords = { "string", "const", "int", "char", "while", "if", "float", "else", "&&",
            "||", "!", "<-", "=", "(", ")", "<", ">", "!=", ">=", "<=", ",", "+", "-", "*", "/", ";", "{", "}",
            "readln", "div", "write", "writeln", "mod", "[", "]" };

    static int lineCount = 1;
    static boolean pauseCompiling = false;
    static String fileStr = "";
    static Lexer lexer = new Lexer();
    static Parser parser = new Parser();
    static Token currentToken;
    static char lineSeparator = System.lineSeparator().charAt(0);

    static final int tokenId = 0;
    static final int tokenStr = 1;
    static final int tokenConst = 2;
    static final int tokenInt = 3;
    static final int tokenChar = 4;
    static final int tokenWhile = 5;
    static final int tokenIf = 6;
    static final int tokenFloat = 7;
    static final int tokenElse = 8;
    static final int tokenAnd = 9;
    static final int tokenOr = 10;
    static final int tokenNot = 11;
    static final int tokenAtrib = 12;
    static final int tokenEqual = 13;
    static final int tokenOpenPar = 14;
    static final int tokenClosePar = 15;
    static final int tokenLess = 16;
    static final int tokenGtr = 17;
    static final int tokenDif = 18;
    static final int tokenGtrEqual = 19;
    static final int tokenLessEqual = 20;
    static final int tokenComma = 21;
    static final int tokenPlus = 22;
    static final int tokenMinus = 23;
    static final int tokenMult = 24;
    static final int tokenDiv = 25;
    static final int tokenSemiColon = 26;
    static final int tokenOpenBra = 27;
    static final int tokenCloseBra = 28;
    static final int tokenRead = 29;
    static final int tokenDiv2 = 30;
    static final int tokenWrite = 31;
    static final int tokenWriteLn = 32;
    static final int tokenMod = 33;
    static final int tokenOpenSq = 34;
    static final int tokenCloseSq = 35;
    static final int tokenValue = 36;

    static class Symbol {
        String lexeme;
        int token;

        Symbol(String lex, int tk) {
            lexeme = lex;
            token = tk;
        }
    }

    static class Token {
        int token;
        String lexeme;
        String type;
        byte bsize;

        Token(String lex, int tk, String t) {
            lexeme = lex;
            token = tk;
            type = t;
        }
    }

    static class Lexer {
        static String lexeme = "";
        static int i = 0;
        static int currentState = 0;

        void throwError(String type) {
            pauseCompiling = true;
            currentToken = new Token("ERRO", 666, "ERRO");

            System.out.println(lineCount);

            // invalid character
            if (type == "invalid_char") {
                System.out.println("caractere invalido.");
            }
            // non identified lexeme
            else if (type == "invalid_lexeme") {
                if (lexeme.charAt(lexeme.length() - 1) == lineSeparator)
                    lexeme = lexeme.substring(0, lexeme.length() - 1);
                System.out.println("lexema nao identificado [" + lexeme + "].");
            }
            // unexpected EOF
            else if (type == "unexpected_eof") {
                System.out.println("fim de arquivo nao esperado.");
            }
        }

        boolean isValid(char c) {
            if (Character.isDigit(c) || isLetter(c) || c == ' ' || c == '_' || c == '.' || c == ';' | c == ','
                    || c == ':' || c == '(' || c == ')' || c == '[' || c == ']' || c == '{' || c == '}' || c == '+'
                    || c == '-' || c == '\"' || c == '\'' || c == '/' || c == '*' || c == '|' || c == '\\' || c == '&'
                    || c == '%' || c == '!' || c == '?' || c == '>' || c == '<' || c == '=' || c == lineSeparator
                    || c == '#')
                return true;
            else
                return false;
        }

        boolean isLetter(char c) {
            if ((c >= 'A' && c <= 'Z') || (c >= 97 && c <= 122))
                return true;
            else
                return false;
        }

        boolean isHexa(char c) {
            if (Character.isDigit(c) || (c >= 'A' && c <= 'F'))
                return true;
            else
                return false;
        }

        int state0(char c) {
            int nextState = 0;

            lexeme += c;

            if (isLetter(c) || c == '_') {
                nextState = 1;
            } else if (Character.isDigit(c)) {
                if (c != '0')
                    nextState = 2;
                else
                    nextState = 16;
            } else if (c == '.') {
                nextState = 3;
            } else if (c == '<') {
                nextState = 5;
            } else if (c == '>') {
                nextState = 6;
            } else if (c == '!') {
                nextState = 7;
            } else if (c == '&') {
                nextState = 8;
            } else if (c == '|') {
                nextState = 9;
            } else if (c == '/') {
                nextState = 10;
            } else if (c == '\'') {
                nextState = 13;
            } else if (c == '\"') {
                nextState = 15;
            } else if (c == '=' || c == '*' || c == '+' || c == '-' || c == ',' || c == '(' || c == ')' || c == '{'
                    || c == '}' || c == '[' || c == ']') {
                nextState = 19;
            } else if (c == '#') {
                nextState = 20;
                currentToken = new Token("EOF", 667, "EOF");
            } else if (c == ';') {
                currentToken = new Token(lexeme, tokenSemiColon, "String");
                nextState = 20;
            } else if (c == ' ') {
                lexeme = "";
            } else if (c == lineSeparator) {
                lexeme = "";
                lineCount++;
            } else {
                throwError("invalid_lexeme");
            }
            return nextState;
        }

        // id / reserved words
        int state1(char c) {
            int nextState = 1;

            if (isLetter(c) || Character.isDigit(c) || c == '.' || c == '_') {
                lexeme += c;

                if (lexeme.length() > 32)
                    throwError("invalid_lexeme");
            } else {
                Symbol symbol = symbolTable.get(lexeme);

                if (symbol != null) {
                    Token token = new Token(lexeme, symbol.token, "String");

                    currentToken = token;

                    // System.out.println(token.lexeme);

                } else {
                    Symbol newSymbol = new Symbol(lexeme, tokenId);
                    symbolTable.put(lexeme, newSymbol);

                    Token token = new Token(lexeme, tokenId, "String");
                    currentToken = token;

                    // System.out.println(token.lexeme);
                }

                nextState = 20;
                i--;
            }

            return nextState;
        }

        // int
        int state2(char c) {
            int nextState = 2;

            if (Character.isDigit(c)) {
                lexeme += c;
            } else if (c == '.') {
                lexeme += c;
                nextState = 3;
            } else {
                Token token = new Token(lexeme, tokenValue, "Integer");
                currentToken = token;

                // System.out.println(token.lexeme);

                nextState = 20;
                i--;
            }

            return nextState;
        }

        // ?. (float)
        int state3(char c) {
            int nextState = 4;

            if (Character.isDigit(c)) {
                lexeme += c;
            } else if (c == '#') {
                throwError("unexpected_eof");
            } else {
                lexeme += c;
                throwError("invalid_lexeme");
            }

            return nextState;
        }

        // ?.d float
        int state4(char c) {
            int nextState = 4;

            if (Character.isDigit(c)) {
                lexeme += c;

                if (lexeme.length() > 7)
                    throwError("invalid_lexeme");
            } else {
                Token token = new Token(lexeme, tokenValue, "Float");
                currentToken = token;

                // System.out.println(token.lexeme);

                nextState = 20;
                i--;
            }

            return nextState;
        }

        // <
        int state5(char c) {
            int nextState = 20;

            if (c == '-' || c == '=')
                lexeme += c;
            else
                i--;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "String");
            currentToken = token;

            // System.out.println(token.lexeme);

            return nextState;
        }

        // >
        int state6(char c) {
            int nextState = 20;

            if (c == '=')
                lexeme += c;
            else
                i--;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "String");
            currentToken = token;

            // System.out.println(token.lexeme);

            return nextState;
        }

        // !
        int state7(char c) {
            int nextState = 20;

            if (c == '=')
                lexeme += c;
            else
                i--;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "String");
            currentToken = token;

            // System.out.println(token.lexeme);

            return nextState;
        }

        // &
        int state8(char c) {
            int nextState = 20;

            if (c == '&') {
                lexeme += c;

                Symbol symbol = symbolTable.get(lexeme);

                Token token = new Token(lexeme, symbol.token, "String");
                currentToken = token;

                // System.out.println(token.lexeme);
            } else {
                throwError("invalid_lexeme");
            }

            return nextState;
        }

        // |
        int state9(char c) {
            int nextState = 20;

            if (c == '|') {
                lexeme += c;

                Symbol symbol = symbolTable.get(lexeme);

                Token token = new Token(lexeme, symbol.token, "String");
                currentToken = token;

                // System.out.println(token.lexeme);
            } else {
                throwError("invalid_lexeme");
            }

            return nextState;
        }

        // /
        int state10(char c) {
            int nextState = 20;

            if (c == '*') {
                nextState = 11;
                lexeme = "";
            } else {
                i--;

                Symbol symbol = symbolTable.get(lexeme);

                Token token = new Token(lexeme, symbol.token, "String");
                currentToken = token;

                // System.out.println(token.lexeme);
            }

            return nextState;
        }

        // /* inside comment
        int state11(char c) {
            int nextState = 11;

            if (c == '#')
                throwError("unexpected_eof");
            else if (c == '*')
                nextState = 12;

            if (c == lineSeparator)
                lineCount++;

            return nextState;
        }

        // /* ? *
        int state12(char c) {
            int nextState = 12;

            if (c == '#')
                throwError("unexpected_eof");
            else if (c == '/')
                nextState = 0;
            else if (c != '*')
                nextState = 11;

            if (c == lineSeparator)
                lineCount++;

            return nextState;
        }

        // '
        int state13(char c) {
            int nextState = 14;

            if (c == '#') {
                throwError("unexpected_eof");
            } else {
                lexeme += c;
            }

            return nextState;
        }

        // 'c
        int state14(char c) {
            int nextState = 20;

            if (c == '#') {
                throwError("unexpected_eof");
            } else if (c != '\'') {
                lexeme += c;
                throwError("invalid_lexeme");
            } else {
                lexeme += c;

                Token token = new Token(lexeme, tokenValue, "Char");
                currentToken = token;

                // System.out.println(token.lexeme);
            }

            return nextState;
        }

        // "
        int state15(char c) {
            int nextState = 15;

            if (c == lineSeparator) {
                throwError("invalid_lexeme");
            } else if (c == '#') {
                throwError("unexpected_eof");
            } else if (c == '\"') {
                lexeme += "0" + c;
                nextState = 20;

                Token token = new Token(lexeme, tokenValue, "String");
                currentToken = token;

                // System.out.println(token.lexeme);
            } else {
                lexeme += c;
                if (lexeme.length() > 256)
                    throwError("invalid_lexeme");
            }

            return nextState;
        }

        // 0
        int state16(char c) {
            int nextState = 17;

            if (Character.isDigit(c)) {
                lexeme += c;
                nextState = 2;
            } else if (c == 'x') {
                lexeme += c;
            } else {

                Token token = new Token(lexeme, tokenValue, "Integer");
                currentToken = token;

                // System.out.println(token.lexeme);

                nextState = 20;
                i--;
            }

            return nextState;
        }

        // 0x
        int state17(char c) {
            int nextState = 18;

            if (c == '#') {
                throwError("unexpected_eof");
            } else if (!isHexa(c)) {
                lexeme += c;
                throwError("invalid_lexeme");
            } else {
                lexeme += c;
            }

            return nextState;
        }

        // 0x?
        int state18(char c) {
            int nextState = 20;

            if (c == '#') {
                throwError("unexpected_eof");
            } else if (!isHexa(c)) {
                lexeme += c;
                throwError("invalid_lexeme");
            } else {
                lexeme += c;

                Token token = new Token(lexeme, tokenValue, "Char");
                currentToken = token;

                // System.out.println(token.lexeme);
            }

            return nextState;
        }

        // = * + - , ( ) { } [ ]
        int state19(char c) {
            int nextState = 20;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "String");
            currentToken = token;

            // System.out.println(token.lexeme);

            i--;

            return nextState;
        }

        Token getLexeme(String fileStr) {
            lexeme = "";
            char c;
            currentState = 0;

            while (currentState != 20 && !pauseCompiling) {
                if (i < fileStr.length()) {
                    c = fileStr.charAt(i);
                    i++;
                } else {
                    c = '#';
                }

                if (isValid(c)) {
                    switch (currentState) {
                        case 0:
                            currentState = state0(c);
                            break;
                        case 1:
                            currentState = state1(c);
                            break;
                        case 2:
                            currentState = state2(c);
                            break;
                        case 3:
                            currentState = state3(c);
                            break;
                        case 4:
                            currentState = state4(c);
                            break;
                        case 5:
                            currentState = state5(c);
                            break;
                        case 6:
                            currentState = state6(c);
                            break;
                        case 7:
                            currentState = state7(c);
                            break;
                        case 8:
                            currentState = state8(c);
                            break;
                        case 9:
                            currentState = state9(c);
                            break;
                        case 10:
                            currentState = state10(c);
                            break;
                        case 11:
                            currentState = state11(c);
                            break;
                        case 12:
                            currentState = state12(c);
                            break;
                        case 13:
                            currentState = state13(c);
                            break;
                        case 14:
                            currentState = state14(c);
                            break;
                        case 15:
                            currentState = state15(c);
                            break;
                        case 16:
                            currentState = state16(c);
                            break;
                        case 17:
                            currentState = state17(c);
                            break;
                        case 18:
                            currentState = state18(c);
                            break;
                        case 19:
                            currentState = state19(c);
                            break;
                        case 20:
                            break;
                    }
                } else {
                    throwError("invalid_char");
                }

            }
            return currentToken;
        }
    }

    static class Parser {

        void throwParserError() {
            if (currentToken.token != 667) {
                pauseCompiling = true;
                System.out.println(lineCount);
                System.out.println("token nao esperado [" + currentToken.lexeme + "].");
            } else {
                pauseCompiling = true;
                System.out.println(lineCount);
                System.out.println("fim de arquivo nao esperado.");
            }
        }

        void checkToken(int expectedToken) {
            if (expectedToken == currentToken.token)
                lexer.getLexeme(fileStr);
            else
                throwParserError();
        }

        void START() {
            while (currentToken.token != 667 && !pauseCompiling) {
                if (currentToken.token == tokenStr || currentToken.token == tokenConst || currentToken.token == tokenInt
                        || currentToken.token == tokenChar || currentToken.token == tokenFloat) {
                    DECL();
                } else
                    COMMAND();
            }

        }

        void DECL() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenStr) {
                    checkToken(tokenStr);
                    if (pauseCompiling)
                        return;
                    DECL1();
                    if (pauseCompiling)
                        return;
                    while (currentToken.token == tokenComma) {
                        checkToken(tokenComma);
                        if (pauseCompiling)
                            return;
                        DECL1();
                        if (pauseCompiling)
                            return;
                    }
                } else if (currentToken.token == tokenConst) {
                    checkToken(tokenConst);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenId) {
                        checkToken(tokenId);
                        if (pauseCompiling)
                            return;
                        if (currentToken.token == tokenEqual) {
                            checkToken(tokenEqual);
                            if (pauseCompiling)
                                return;
                            DECL_TYPE();
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenInt) {
                    checkToken(tokenInt);
                    if (pauseCompiling)
                        return;
                    DECL1();
                    if (pauseCompiling)
                        return;
                    while (currentToken.token == tokenComma) {
                        checkToken(tokenComma);
                        if (pauseCompiling)
                            return;
                        DECL1();
                        if (pauseCompiling)
                            return;
                    }
                } else if (currentToken.token == tokenChar) {
                    checkToken(tokenChar);
                    if (pauseCompiling)
                        return;
                    DECL1();
                    if (pauseCompiling)
                        return;
                    while (currentToken.token == tokenComma) {
                        checkToken(tokenComma);
                        if (pauseCompiling)
                            return;
                        DECL1();
                        if (pauseCompiling)
                            return;
                    }
                } else if (currentToken.token == tokenFloat) {
                    checkToken(tokenFloat);
                    if (pauseCompiling)
                        return;
                    DECL1();
                    if (pauseCompiling)
                        return;
                    while (currentToken.token == tokenComma) {
                        checkToken(tokenComma);
                        if (pauseCompiling)
                            return;
                        DECL1();
                        if (pauseCompiling)
                            return;
                    }
                } else
                    throwParserError();

                if (currentToken.token == tokenSemiColon) {
                    checkToken(tokenSemiColon);
                } else
                    throwParserError();
            }
        }

        void DECL1() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenId) {
                    checkToken(tokenId);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenAtrib) {
                        checkToken(tokenAtrib);
                        if (pauseCompiling)
                            return;
                        DECL_TYPE();
                        if (pauseCompiling)
                            return;
                    }
                }
            }
        }

        void DECL_TYPE() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenMinus) {
                    checkToken(tokenMinus);
                    if (pauseCompiling)
                        return;
                }
                if (currentToken.token == tokenValue) {
                    checkToken(tokenValue);
                    if (pauseCompiling)
                        return;
                } else
                    throwParserError();
            }
        }

        void COMMAND() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenId) {
                    checkToken(tokenId);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenSq) {
                        checkToken(tokenOpenSq);
                        if (pauseCompiling)
                            return;
                        EXP();
                        if (pauseCompiling)
                            return;
                        if (currentToken.token == tokenCloseSq) {
                            checkToken(tokenCloseSq);
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    }
                    if (currentToken.token == tokenAtrib) {
                        checkToken(tokenAtrib);
                        if (pauseCompiling)
                            return;
                        EXP();
                        if (pauseCompiling)
                            return;
                        if (currentToken.token == tokenSemiColon) {
                            checkToken(tokenSemiColon);
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenWhile) {
                    checkToken(tokenWhile);
                    if (pauseCompiling)
                        return;
                    EXP();
                    if (pauseCompiling)
                        return;
                    CMD_TYPE();
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenIf) {
                    checkToken(tokenIf);
                    if (pauseCompiling)
                        return;
                    EXP();
                    if (pauseCompiling)
                        return;
                    CMD_TYPE();
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenSemiColon) {
                        checkToken(tokenSemiColon);
                        if (pauseCompiling)
                            return;
                    }
                    if (currentToken.token == tokenElse) {
                        checkToken(tokenElse);
                        if (pauseCompiling)
                            return;
                        CMD_TYPE();
                        if (pauseCompiling)
                            return;
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenRead) {
                    checkToken(tokenRead);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenPar) {
                        checkToken(tokenOpenPar);
                        if (pauseCompiling)
                            return;
                        if (currentToken.token == tokenId) {
                            checkToken(tokenId);
                            if (pauseCompiling)
                                return;
                            if (currentToken.token == tokenClosePar) {
                                checkToken(tokenClosePar);
                                if (pauseCompiling)
                                    return;
                                if (currentToken.token == tokenSemiColon) {
                                    checkToken(tokenSemiColon);
                                    if (pauseCompiling)
                                        return;
                                } else
                                    throwParserError();
                            } else
                                throwParserError();
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenWrite) {
                    checkToken(tokenWrite);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenPar) {
                        checkToken(tokenOpenPar);
                        if (pauseCompiling)
                            return;
                        EXP_LIST();
                        if (pauseCompiling)
                            return;
                        if (currentToken.token == tokenClosePar) {
                            checkToken(tokenClosePar);
                            if (pauseCompiling)
                                return;
                            if (currentToken.token == tokenSemiColon) {
                                checkToken(tokenSemiColon);
                                if (pauseCompiling)
                                    return;
                            } else
                                throwParserError();
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenWriteLn) {
                    checkToken(tokenWriteLn);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenPar) {
                        checkToken(tokenOpenPar);
                        if (pauseCompiling)
                            return;
                        EXP_LIST();
                        if (currentToken.token == tokenClosePar) {
                            checkToken(tokenClosePar);
                            if (pauseCompiling)
                                return;
                            if (currentToken.token == tokenSemiColon) {
                                checkToken(tokenSemiColon);
                                if (pauseCompiling)
                                    return;
                            } else
                                throwParserError();
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenSemiColon) {
                    checkToken(tokenSemiColon);
                    if (pauseCompiling)
                        return;
                } else
                    throwParserError();
            }
        }

        void CMD_TYPE() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenOpenBra) {
                    checkToken(tokenOpenBra);
                    if (pauseCompiling)
                        return;
                    while (currentToken.token != tokenCloseBra) {
                        COMMAND();
                        if (pauseCompiling)
                            return;
                    }
                    checkToken(tokenCloseBra);
                    if (pauseCompiling)
                        return;
                } else {
                    COMMAND();
                    if (pauseCompiling)
                        return;
                }
            }
        }

        void EXP_LIST() {
            if (!pauseCompiling) {
                EXP();
                if (pauseCompiling)
                    return;
                while (currentToken.token == tokenComma) {
                    checkToken(tokenComma);
                    if (pauseCompiling)
                        return;
                    EXP();
                    if (pauseCompiling)
                        return;
                }
            }
        }

        void OPERATOR() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenEqual) {
                    checkToken(tokenEqual);
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenDif) {
                    checkToken(tokenDif);
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenLess) {
                    checkToken(tokenLess);
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenGtr) {
                    checkToken(tokenGtr);
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenLessEqual) {
                    checkToken(tokenLessEqual);
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenGtrEqual) {
                    checkToken(tokenGtrEqual);
                    if (pauseCompiling)
                        return;
                } else
                    throwParserError();
            }
        }

        void EXP() {
            if (!pauseCompiling) {
                EXP1();
                if (pauseCompiling)
                    return;
                while (currentToken.token == tokenEqual || currentToken.token == tokenDif
                        || currentToken.token == tokenLess || currentToken.token == tokenGtr
                        || currentToken.token == tokenLessEqual || currentToken.token == tokenGtrEqual) {
                    OPERATOR();
                    if (pauseCompiling)
                        return;
                    EXP1();
                    if (pauseCompiling)
                        return;
                }
            }
        }

        void EXP1() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenMinus) {
                    checkToken(tokenMinus);
                    if (pauseCompiling)
                        return;
                }
                EXP2();
                if (pauseCompiling)
                    return;
                while (currentToken.token == tokenPlus || currentToken.token == tokenMinus
                        || currentToken.token == tokenOr) {
                    if (currentToken.token == tokenPlus) {
                        checkToken(tokenPlus);
                        if (pauseCompiling)
                            return;
                    } else if (currentToken.token == tokenMinus) {
                        checkToken(tokenMinus);
                        if (pauseCompiling)
                            return;
                    } else if (currentToken.token == tokenOr) {
                        checkToken(tokenOr);
                        if (pauseCompiling)
                            return;
                    } else
                        throwParserError();
                    EXP2();
                    if (pauseCompiling)
                        return;
                }
            }
        }

        void EXP2() {
            if (!pauseCompiling) {
                EXP3();
                if (pauseCompiling)
                    return;
                while (currentToken.token == tokenMult || currentToken.token == tokenAnd
                        || currentToken.token == tokenDiv || currentToken.token == tokenMod) {
                    if (currentToken.token == tokenMult) {
                        checkToken(tokenMult);
                        if (pauseCompiling)
                            return;
                    } else if (currentToken.token == tokenAnd) {
                        checkToken(tokenAnd);
                        if (pauseCompiling)
                            return;
                    } else if (currentToken.token == tokenDiv) {
                        checkToken(tokenDiv);
                        if (pauseCompiling)
                            return;
                    } else if (currentToken.token == tokenMod) {
                        checkToken(tokenMod);
                        if (pauseCompiling)
                            return;
                    } else
                        throwParserError();
                    EXP3();
                    if (pauseCompiling)
                        return;
                }
            }
        }

        void EXP3() {
            if (!pauseCompiling) {
                while (currentToken.token == tokenNot) {
                    checkToken(tokenNot);
                    if (pauseCompiling)
                        return;
                }
                EXP4();
                if (pauseCompiling)
                    return;
            }
        }

        void EXP4() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenInt) {
                    checkToken(tokenInt);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenPar) {
                        checkToken(tokenOpenPar);
                        if (pauseCompiling)
                            return;
                        EXP();
                        if (pauseCompiling)
                            return;
                        if (currentToken.token == tokenClosePar) {
                            checkToken(tokenClosePar);
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenFloat) {
                    checkToken(tokenFloat);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenPar) {
                        checkToken(tokenOpenPar);
                        if (pauseCompiling)
                            return;
                        EXP();
                        if (currentToken.token == tokenClosePar) {
                            checkToken(tokenClosePar);
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else {
                    EXP5();
                    if (pauseCompiling)
                        return;
                }
            }
        }

        void EXP5() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenOpenPar) {
                    checkToken(tokenOpenPar);
                    if (pauseCompiling)
                        return;
                    EXP();
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenClosePar) {
                        checkToken(tokenClosePar);
                        if (pauseCompiling)
                            return;
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenId) {
                    checkToken(tokenId);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenSq) {
                        checkToken(tokenOpenSq);
                        if (pauseCompiling)
                            return;
                        EXP();
                        if (pauseCompiling)
                            return;
                        if (currentToken.token == tokenCloseSq) {
                            checkToken(tokenCloseSq);
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    }
                } else if (currentToken.token == tokenValue) {
                    checkToken(tokenValue);
                    if (pauseCompiling)
                        return;
                } else
                    throwParserError();
            }
        }
    }

    public static void main(String[] args) {

        for (int i = 0; i < reservedWords.length; i++) {
            Symbol symbol = new Symbol(reservedWords[i], i + 1);
            symbolTable.put(reservedWords[i], symbol);
        }

        // try {
        // File file = new File("programa.in");
        // Scanner scanner = new Scanner(file);

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()) {
            fileStr += scanner.nextLine() + lineSeparator;
        }

        lexer.getLexeme(fileStr);

        parser.START();

        if (!pauseCompiling && lineCount != 1)
            System.out.println(lineCount + " linhas compiladas.");

        scanner.close();
        // } catch (FileNotFoundException e) {
        // e.printStackTrace();
        // }
    }

}
