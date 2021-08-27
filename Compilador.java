import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.Scanner;

public class Compilador {
    static final String[] palavrasReservadas = { "const", "int", "char", "while", "if", "float", "else", "&&", "||",
            "!", "<-", "=", "(", ")", "<", ">", "!=", ">=", "<=", ",", "+", "-", "*", "/", ";", "{", "}", "readln",
            "div", "write", "writeln", "mod", "[", "]" };

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

    class Token {
        int num;
        String lexema;
        String tipo;
        byte tamanho;
    }

    public static void main(String[] args) {
        Hashtable<String, Symbol> symbolTable = new Hashtable<String, Symbol>();

        for (int i = 0; i < palavrasReservadas.length; i++) {
            Symbol symbol = new Symbol(palavrasReservadas[i], i + 2);
            symbolTable.put(palavrasReservadas[i], symbol);
        }

        try {
            File file = new File("programa.txt");
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                // String str =
                // for(int i = 0; i < )
                System.out.println(scanner.next().charAt(0));
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // System.out.println(symbolTable.get("+"));

    }

}
