import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

public class Compilador {
    static Hashtable<String, Symbol> symbolTable = new Hashtable<String, Symbol>();
    static List<Token> tokenList = new ArrayList<Token>();
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

        int state0(char c) {
            int nextState = 0;
            lexeme += c;

            if (Character.isLetter(c) || c == '_') {
                nextState = 1;
            } else if (c == '<') {
                nextState = 2;
            } else if (Character.isDigit(c)) {
                nextState = 3;
            } else if (c == ';') {
                nextState = 14;
            }
            return nextState;
        }

        int state1(char c) {
            int nextState = 0;
            lexeme += c;

            if (Character.isLetter(c) || Character.isDigit(c) || c == '.' || c == '_') {
                nextState = 1;
            } else {
                nextState = 0;

                Symbol symbol = symbolTable.get(lexeme);

                if (symbol != null) {
                    Token token = new Token(lexeme, symbol.token, "String");
                    tokenList.add(token);
                } else { // is id
                    Symbol newSymbol = new Symbol(lexeme, tokenId);
                    Token token = new Token(lexeme, tokenId, "String");

                    symbolTable.put(lexeme, newSymbol);
                    tokenList.add(token);
                }
            }
            return nextState;
        }

        int state2(char c) {
            int nextState = 0;
            lexeme += c;

            if (c == '-') {
                nextState = 0;
            }
            return nextState;
        }

        int state3(char c) {
            int nextState = 0;
            lexeme += c;

            if (Character.isDigit(c)) {
                nextState = 3;
            } else if (c == '.') {
                nextState = 4;
            }
            return nextState;
        }

        // int state14(char c) {
        // int nextState = 0;
        // lexeme += c;

        // if (Character.isDigit(c)) {
        // nextState = 3;
        // }else if(c == '.'){
        // nextState = 4;
        // }
        // return nextState;
        // }

        String getLexeme(String str) {
            int currentState = 0;
            int i = 0;
            char c;

            while (currentState != 15) {
                if (i < str.length()) {
                    c = str.charAt(i);
                    i++;

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
                        case 15:
                            break;
                    }
                } else {
                }

            }

            return lexeme;
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
            while (scanner.hasNext()) {
                String str = lexer.getLexeme(scanner.next());
                System.out.println(str);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // System.out.println(symbolTable.get("+"));

    }

}
