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

    static int lineCount = 0;
    static boolean pauseCompiling = false;

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
            pauseCompiling = true;

            // invalid character
            if (type == "invalid_char") {
                System.out.println("caractere invalido.");
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
                    nextState = 14;
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
                nextState = 13;
            } else if (c == '=' || c == '+' || c == '-' || c == ',' || c == '(' || c == ')' || c == '{' || c == '}'
                    || c == '[' || c == ']') {
                nextState = 17;
            } else if (c == '#') {
                nextState = 19;
            } else if (c == ';' || c == ' ') {
                nextState = 0;
                lexeme = "";
            }
            return nextState;
        }

        // id / reserved words
        int state1(char c) {
            int nextState = 1;

            if (Character.isLetter(c) || Character.isDigit(c) || c == '.' || c == '_') {
                lexeme += c;
            } else {
                if (c == '#')
                    nextState = 19;
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
                if (c == '#')
                    nextState = 19;
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
                if (c == '#')
                    nextState = 19;
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
                throwError("invalid_char");
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
                throwError("invalid_char");
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
                // fim do comentario
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
            int nextState = 12;

            if (c == '#') {
                throwError("unexpected_eof");
            } else if (c == '\"' || c == '$') {
                throwError("invalid_char");
            } else {
                lexeme += c;
            }

            return nextState;
        }

        // 'c
        int state12(char c) {
            int nextState = 0;

            if (c == '#') {
                throwError("unexpected_eof");
            } else if (c != '\'') {
                lexeme += c;
                throwError("invalid_lexeme");
            } else {
                lexeme += c;

                Token token = new Token(lexeme, tokenConst, "Char");

                System.out.println(token.lexeme);

                lexeme = "";
            }

            return nextState;
        }

        // "
        int state13(char c) {
            int nextState = 13;

            if (c == '$' || c == '\n') {
                throwError("invalid_char");
            } else if (c == '\"') {

                lexeme += "$" + c;
                nextState = 0;

                Token token = new Token(lexeme, tokenConst, "String");

                System.out.println(token.lexeme);

                lexeme = "";
            } else {
                lexeme += c;
            }

            return nextState;
        }

        // 0
        int state14(char c) {
            int nextState = 15;

            if (Character.isDigit(c)) {
                lexeme += c;
                nextState = 2;
            } else if (c == 'x' || c == 'X') {
                lexeme += c;
            } else {
                if (c == '#')
                    nextState = 19;
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
            int nextState = 16;

            lexeme += c;

            if (c == '#') {
                throwError("unexpected_eof");
            } else if (!isHexa(c)) {
                throwError("invalid_lexeme");
            }

            return nextState;
        }

        // 0x?
        int state16(char c) {
            int nextState = 0;

            lexeme += c;

            if (c == '#') {
                throwError("unexpected_eof");
            } else if (!isHexa(c)) {
                throwError("invalid_lexeme");
            } else {
                Token token = new Token(lexeme, tokenConst, "Char");

                System.out.println(token.lexeme);

                lexeme = "";
            }

            return nextState;
        }

        // = + - , ( ) { } [ ]
        int state17(char c) {
            int nextState = 0;

            if (c == '#')
                nextState = 19;
            else {
                i--;
            }

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "String");

            System.out.println(token.lexeme);

            lexeme = "";

            return nextState;
        }

        void getLexemes(String line) {
            lexeme = "";
            i = 0;
            int currentState = 0;
            char c;

            while (currentState != 19 && !pauseCompiling) {
                if (i < line.length()) {
                    c = line.charAt(i);
                    i++;
                } else {
                    c = '#';
                }

                switch (currentState) {
                    case 0:
                        // System.out.println("estado 0");
                        currentState = state0(c);
                        break;
                    case 1:
                        // System.out.println("estado 1");
                        currentState = state1(c);
                        break;
                    case 2:
                        // System.out.println("estado 2");
                        currentState = state2(c);
                        break;
                    case 3:
                        // System.out.println("estado 3");
                        currentState = state3(c);
                        break;
                    case 4:
                        // System.out.println("estado 4");
                        currentState = state4(c);
                        break;
                    case 5:
                        // System.out.println("estado 5");
                        currentState = state5(c);
                        break;
                    case 6:
                        // System.out.println("estado 6");
                        currentState = state6(c);
                        break;
                    case 7:
                        // System.out.println("estado 7");
                        currentState = state7(c);
                        break;
                    case 8:
                        // System.out.println("estado 8");
                        currentState = state8(c);
                        break;
                    case 9:
                        // System.out.println("estado 9");
                        currentState = state9(c);
                        break;
                    case 10:
                        // System.out.println("estado 10");
                        currentState = state10(c);
                        break;
                    case 11:
                        // System.out.println("estado 11");
                        currentState = state11(c);
                        break;
                    case 12:
                        // System.out.println("estado 12");
                        currentState = state12(c);
                        break;
                    case 13:
                        // System.out.println("estado 13");
                        currentState = state13(c);
                        break;
                    case 14:
                        // System.out.println("estado 14");
                        currentState = state14(c);
                        break;
                    case 15:
                        // System.out.println("estado 15");
                        currentState = state15(c);
                        break;
                    case 16:
                        // System.out.println("estado 16");
                        currentState = state16(c);
                        break;
                    case 17:
                        // System.out.println("estado 17");
                        currentState = state17(c);
                        break;
                    case 18:
                        // System.out.println("estado 18");
                        currentState = state17(c);
                        break;
                    case 19:
                        // System.out.println("estado 19");
                        break;
                }

            }
        }
    }

    public static void main(String[] args) {

        for (int i = 0; i < reservedWords.length; i++) {
            Symbol symbol = new Symbol(reservedWords[i], i + 2);
            symbolTable.put(reservedWords[i], symbol);
        }

        try {
            File file = new File("programa.txt");
            Scanner scanner = new Scanner(file);
            Lexer lexer = new Lexer();
            // while (scanner.hasNext() && !pauseCompiling) {
            // String str = scanner.next();
            // System.out.println("String Lida: " + str);
            // String lex = lexer.getLexemes(str);
            // }
            while (scanner.hasNextLine() && !pauseCompiling) {
                String line = scanner.nextLine();

                if (line != "") {
                    lineCount++;
                    System.out.println("String Lida: " + line);
                    lexer.getLexemes(line);
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // System.out.println(symbolTable.get("+"));

    }

}
