import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Compilador {
    static Hashtable<String, Symbol> symbolTable = new Hashtable<String, Symbol>();
    // static List<Token> tokenList = new ArrayList<Token>();
    static final String[] reservedWords = { "const", "int", "char", "while", "if", "float", "else", "&&", "||", "!",
            "<-", "=", "(", ")", "<", ">", "!=", ">=", "<=", ",", "+", "-", "*", "/", ";", "{", "}", "readln", "div",
            "write", "writeln", "mod", "[", "]" };

    static final int tokenId = 1;
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
        static boolean commenting = false;

        void throwError(String type) {
            // invalid character
            if (type == "invalid_char") {

            }
            // non identified lexeme
            else if (type == "invalid_lexeme") {
                System.out.println("lexema nao identificado [" + lexeme + "].");
            }
            // unexpected EOF
            else if (type == "unexpected_eof") {
                System.out.println("fim de arquivo nao esperado.");
            }
        }

        boolean isHexa(char c) {
            if (Character.isDigit(c) || c == 'a' || c == 'A' || c == 'b' || c == 'B' || c == 'c' || c == 'C' || c == 'd'
                    || c == 'D' || c == 'e' || c == 'E' || c == 'f' || c == 'F')
                return true;
            else
                return false;
        }

        int state0(char c) {
            int nextState = 0;

            lexeme += c;

            if (Character.isLetter(c) || c == '_') {
                nextState = 1;
            } else if (Character.isDigit(c)) {
                if (c != '0')
                    nextState = 2;
                else
                    nextState = 13;
            } else if (c == '.') {
                nextState = 3;
            } else if (c == '<') {
                nextState = 4;
            } else if (c == '>') {
                nextState = 5;
            } else if (c == '!') {
                nextState = 6;
            } else if (c == '&') {
                nextState = 7;
            } else if (c == '|') {
                nextState = 8;
            } else if (c == '/') {
                nextState = 9;
            } else if (c == '*') {
                nextState = 10;
            } else if (c == '\'') {
                nextState = 11;
            } else if (c == '\"') {
                nextState = 12;
            } else if (c == '=' || c == '+' || c == '-' || c == ',' || c == '(' || c == ')' || c == '{' || c == '}'
                    || c == '[' || c == ']') {
                nextState = 14;
            } else if (c == ';') {
                nextState = 15;
            } else if (c == '\u0020') {
                nextState = 15;
            }
            return nextState;
        }

        // id / reserved words
        int state1(char c) {
            int nextState = 1;

            if (Character.isLetter(c) || Character.isDigit(c) || c == '.' || c == '_') {
                lexeme += c;
            } else {
                if (c == '\u0020')
                    nextState = 15;
                else {
                    nextState = 0;
                    i--;
                }

                Symbol symbol = symbolTable.get(lexeme);

                if (symbol != null) {
                    Token token = new Token(lexeme, symbol.token, "String");

                    System.out.println(token.lexeme);

                } else {
                    Symbol newSymbol = new Symbol(lexeme, tokenId);
                    symbolTable.put(lexeme, newSymbol);

                    Token token = new Token(lexeme, tokenId, "String");

                    System.out.println(token.lexeme);
                }

                lexeme = "";
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
                if (c == '\u0020')
                    nextState = 15;
                else {
                    nextState = 0;
                    i--;
                }

                Token token = new Token(lexeme, tokenConst, "Integer");

                System.out.println(token.lexeme);

                lexeme = "";
            }

            return nextState;
        }

        // float
        int state3(char c) {
            int nextState = 3;

            if (Character.isDigit(c)) {
                lexeme += c;
            } else {
                if (c == '\u0020')
                    nextState = 15;
                else {
                    nextState = 0;
                    i--;
                }

                Token token = new Token(lexeme, tokenConst, "Integer");

                System.out.println(token.lexeme);

                lexeme = "";
            }

            return nextState;
        }

        // <
        int state4(char c) {
            int nextState = 0;

            if (c == '-' || c == '=')
                lexeme += c;
            else
                i--;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "String");

            System.out.println(token.lexeme);

            lexeme = "";

            return nextState;
        }

        // >
        int state5(char c) {
            int nextState = 0;

            if (c == '=')
                lexeme += c;
            else
                i--;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "String");

            System.out.println(token.lexeme);

            lexeme = "";

            return nextState;
        }

        // !
        int state6(char c) {
            int nextState = 0;

            if (c == '=')
                lexeme += c;
            else
                i--;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "String");

            System.out.println(token.lexeme);

            lexeme = "";

            return nextState;
        }

        // &
        int state7(char c) {
            int nextState = 0;

            if (c == '&') {
                lexeme += c;
                Symbol symbol = symbolTable.get(lexeme);

                Token token = new Token(lexeme, symbol.token, "String");

                System.out.println(token.lexeme);

                lexeme = "";
            } else {
                // gerar erro
                i--;
            }

            return nextState;
        }

        // |
        int state8(char c) {
            int nextState = 0;

            if (c == '|') {
                lexeme += c;
                Symbol symbol = symbolTable.get(lexeme);

                Token token = new Token(lexeme, symbol.token, "String");

                System.out.println(token.lexeme);

                lexeme = "";
            } else {
                // gerar erro
                i--;
            }

            return nextState;
        }

        // /
        int state9(char c) {
            int nextState = 0;

            if (c == '*') {
                // eh comentario
                commenting = true;
            } else {
                i--;

                Symbol symbol = symbolTable.get(lexeme);

                Token token = new Token(lexeme, symbol.token, "String");

                System.out.println(token.lexeme);

                lexeme = "";
            }

            return nextState;
        }

        // *
        int state10(char c) {
            int nextState = 0;

            if (c == '/') {
                // eh comentario
                commenting = false;
            } else {
                i--;

                Symbol symbol = symbolTable.get(lexeme);

                Token token = new Token(lexeme, symbol.token, "String");

                System.out.println(token.lexeme);

                lexeme = "";
            }

            return nextState;
        }

        // '
        int state11(char c) {
            int nextState = 11;

            if (c == '\u0020') {

            }

            if (c != '\"' && c != '$') {
                lexeme += c;
            } else if (c == '\'') {
                lexeme += c;
                nextState = 0;

                Token token = new Token(lexeme, tokenConst, "Char");

                System.out.println(token.lexeme);

                lexeme = "";
            } else {
                // erro
            }

            return nextState;
        }

        // 'c
        int state12(char c) {
            int nextState = 11;

            if (c != '\"' && c != '$') {
                lexeme += c;
            } else if (c == '\'') {
                lexeme += c;
                nextState = 0;

                Token token = new Token(lexeme, tokenConst, "Char");

                System.out.println(token.lexeme);

                lexeme = "";
            } else {
                // erro
            }

            return nextState;
        }

        // "
        int state13(char c) {
            int nextState = 12;

            if (c != '$') {
                lexeme += c;
            } else if (c == '\"') {
                lexeme += c + "$";
                nextState = 0;

                Token token = new Token(lexeme, tokenConst, "String");

                System.out.println(token.lexeme);

                lexeme = "";
            } else {
                // erro
            }

            return nextState;
        }

        // 0
        int state14(char c) {
            int nextState = 13;

            if (Character.isDigit(c)) {
                lexeme += c;
                nextState = 2;
            } else if (c == 'x' || c == 'X') {
                lexeme += c;
                nextState = 14;
            } else {
                if (c == '\u0020')
                    nextState = 15;
                else {
                    nextState = 0;
                    i--;
                }

                Token token = new Token(lexeme, tokenConst, "Integer");

                System.out.println(token.lexeme);

                lexeme = "";
            }

            return nextState;
        }

        // 0x
        int state15(char c) {
            int nextState = 14;

            if (c == '\u0020') {
                throwError("unexpected_eof");
            } else if (!isHexa(c)) {
                lexeme += c;
                throwError("invalid_lexeme");
            }

            return nextState;
        }

        // 0x?
        int state16(char c) {
            int nextState = 14;

            if (isHexa(c) && lexeme.length() < 4) {
                lexeme += c;
            } else {
                if (c == '\u0020')
                    nextState = 15;
                else {
                    nextState = 0;
                    i--;
                }

                Token token = new Token(lexeme, tokenConst, "Char");

                System.out.println(token.lexeme);

                lexeme = "";
            }

            return nextState;
        }

        // = + - , ( ) { } [ ]
        int state17(char c) {
            int nextState = 0;

            if (c == '\u0020')
                nextState = 15;
            else {
                i--;
            }

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "String");

            System.out.println(token.lexeme);

            lexeme = "";

            return nextState;
        }

        String getLexeme(String str) {
            lexeme = "";
            i = 0;
            int currentState = 0;
            char c;

            while (currentState != 15) {
                if (i < str.length()) {
                    c = str.charAt(i);
                    i++;
                } else {
                    c = '\u0020';
                }

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
                        break;
                }

            }
            return lexeme;
        }
    }

    public static void main(String[] args) {
        // int qntLines = 0;

        for (int i = 0; i < reservedWords.length; i++) {
            Symbol symbol = new Symbol(reservedWords[i], i + 2);
            symbolTable.put(reservedWords[i], symbol);
        }

        try {
            File file = new File("programa.txt");
            Scanner scanner = new Scanner(file);
            Lexer lexer = new Lexer();
            while (scanner.hasNext()) {
                // qntLines++;
                String str = scanner.next();
                System.out.println("String Lida: " + str);
                String lex = lexer.getLexeme(str);
                // System.out.println(str);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // System.out.println(symbolTable.get("+"));

    }

}
