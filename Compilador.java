/* 
    *   Trabalho Prático - Compiladores 2021/2
    *   GRUPO 9
    *   Bernardo Cerqueira de Lima      586568
    *   Henrique Dornas Mendes          651252
    *   Matheus Teixeira Alves          636132
*/

// import java.io.File;
// import java.io.FileNotFoundException;
import java.util.Hashtable;
// import java.util.Scanner;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

public class Compilador {

    /*
     * Declarações iniciais: 
     * symbolTable      -> Tabela de Símbolos 
     * reservedWords    -> Palavras reservadas da linguagem 
     * lineCount        -> Contador de linhas
     * pauseCompiling   -> Pausa compilação ao encontrar erro 
     * fileStr          -> String que compoe arquivo fonte 
     * lexer, parser    -> Analisador Lexico e Sintático
     * lineSeparator    -> Separador de linha
     * Tokens           -> Tokens da linguagem
     */

    static Hashtable<String, Symbol> symbolTable = new Hashtable<String, Symbol>();
    static final String[] reservedWords = { "string", "const", "int", "char", "while", "if", "float", "else", "&&",
            "||", "!", "<-", "=", "(", ")", "<", ">", "!=", ">=", "<=", ",", "+", "-", "*", "/", ";", "{", "}",
            "readln", "div", "write", "writeln", "mod", "[", "]" };

    static int lineCount = 1;
    static boolean pauseCompiling = false;
    static String fileStr = "";
    static Lexer lexer = new Lexer();
    static Parser parser = new Parser();
    static Token currentToken = null;
    static char lineSeparator = '\n';

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

    // Classe dos Elementos da Tabela de Símbolos
    static class Symbol {
        String lexeme;
        int token;
        int addr;
        String classification;
        String type;

        Symbol(String lex, int tk) {
            lexeme = lex;
            token = tk;
            classification = "undefined";
            type = "undefined";
        }
    }

    // Classe dos Tokens encontrados
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

    /*
        Classe do Analisador Lexico 
    
        Variáveis locais: 
            lexeme          -> lexema da formação do token
            i               -> posição no arquivo fonte
            currentState    -> estado atual
    
        --- OBSERVAÇÃO ---
        Utilizamos o caracter # como EOF.
    */
    static class Lexer {
        static String lexeme = "";
        static int i = 0;
        static int currentState = 0;

        /* 
            Metodo ThrowError -> Encerra o programa ao encontrar erro lexico
        */
        void throwError(String type) {
            pauseCompiling = true;
            currentToken = new Token("ERRO", 666, "ERRO");

            System.out.println(lineCount);

            // caracter invalido
            if (type == "invalid_char") {
                System.out.println("caractere invalido.");
            }
            // lexema nao identificado
            else if (type == "invalid_lexeme") {
                if (lexeme.charAt(lexeme.length() - 1) == lineSeparator || lexeme.charAt(lexeme.length() - 1) == ';')
                    lexeme = lexeme.substring(0, lexeme.length() - 1);
                System.out.println("lexema nao identificado [" + lexeme + "].");
            }
            // EOF inesperado
            else if (type == "unexpected_eof") {
                System.out.println("fim de arquivo nao esperado.");
            }
        }

        /*  
            Método isValid -> Verifica se caracter é válido
            
            # é EOF
        */
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

        /*  
            Método isLetter -> Verifica se caracter é letra
        */
        boolean isLetter(char c) {
            if ((c >= 'A' && c <= 'Z') || (c >= 97 && c <= 122))
                return true;
            else
                return false;
        }

        /*  
            Método isHexa -> Verifica se caracter é Hexadecimal
        */
        boolean isHexa(char c) {
            if (Character.isDigit(c) || (c >= 'A' && c <= 'F'))
                return true;
            else
                return false;
        }

        /*  
            Estado Inicial do Automato: Com base no primeiro caracter lido, define proximo estado.
        */
        int state0(char c) {
            int nextState = 0;

            if (c == ' ')
                return nextState;

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
                currentToken = new Token(lexeme, tokenSemiColon, "ReservedWord");
                nextState = 20;
            } else if (c == lineSeparator) {
                lexeme = "";
                lineCount++;
            } else {
                throwError("invalid_lexeme");
            }
            return nextState;
        }

        /*  
            Estado 1 -> Trata de identificadores e palavras reservadas.
            Continua no mesmo estado enquanto for um caracter valido para estes tipos e length <= 32.
            Caso contrario, salva e vai pro estado final.
        */
        int state1(char c) {
            int nextState = 1;

            if (isLetter(c) || Character.isDigit(c) || c == '.' || c == '_') {
                lexeme += c;

                if (lexeme.length() > 32)
                    throwError("invalid_lexeme");
            } else {
                Symbol symbol = symbolTable.get(lexeme);
                Token token;

                if (symbol != null) {
                    if (symbol.token != tokenId) {
                        token = new Token(lexeme, symbol.token, "ReservedWord");
                    } else {
                        token = new Token(lexeme, symbol.token, "Identifier");
                    }
                } else {
                    token = new Token(lexeme, tokenId, "Identifier");
                }

                currentToken = token;
                nextState = 20;
                i--;
            }
            return nextState;
        }

        /*  
            Estado 2 -> Trata de inteiros.
            Continua no mesmo estado enquanto for digito.
            Caso leia . , vai pro estado 3.
            Caso contrario, salva e vai pro estado final.
        */
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

                nextState = 20;
                i--;
            }
            return nextState;
        }

        /*  
            Estado 3 -> ?. Trata de floats com so um ponto
            Caso leia digito, vai pro estado 4.
            Caso contrario, da erro de eof ou lexema invalido.
        */
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

        /*  
            Estado 4 -> ?.d Trata de floats com ponto e 1 digito
            Continua no mesmo estado enquanto c for digito
            Caso length > 7, erro de lexema invalido
            Caso contrario, salva e vai pro estado final.
        */
        int state4(char c) {
            int nextState = 4;

            if (Character.isDigit(c)) {
                lexeme += c;

                if (lexeme.length() > 7)
                    throwError("invalid_lexeme");
            } else {
                Token token = new Token(lexeme, tokenValue, "Float");
                currentToken = token;

                nextState = 20;
                i--;
            }
            return nextState;
        }

        /*  
            Estado 5 -> Trata de <
            Estado final se c = - ou = ou diferente.
        */
        int state5(char c) {
            int nextState = 20;

            if (c == '-' || c == '=')
                lexeme += c;
            else
                i--;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "ReservedWord");
            currentToken = token;

            return nextState;
        }

        /*  
            Estado 6 -> Trata de >
            Estado final se = ou diferente.
        */
        int state6(char c) {
            int nextState = 20;

            if (c == '=')
                lexeme += c;
            else
                i--;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "ReservedWord");
            currentToken = token;

            return nextState;
        }

        /*  
            Estado 7 -> Trata de !
            Estado final se = ou diferente.
        */
        int state7(char c) {
            int nextState = 20;

            if (c == '=')
                lexeme += c;
            else
                i--;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "ReservedWord");
            currentToken = token;

            return nextState;
        }

        /*  
            Estado 8 -> Trata de &
            Estado final se c = &, caso contrario erro de lexema invalido.
        */
        int state8(char c) {
            int nextState = 20;

            if (c == '&') {
                lexeme += c;

                Symbol symbol = symbolTable.get(lexeme);

                Token token = new Token(lexeme, symbol.token, "ReservedWord");
                currentToken = token;
            } else {
                throwError("invalid_lexeme");
            }
            return nextState;
        }

        /*  
            Estado 9 -> Trata de |
            Estado final se c = |, caso contrario erro de lexema invalido.
        */
        int state9(char c) {
            int nextState = 20;

            if (c == '|') {
                lexeme += c;

                Symbol symbol = symbolTable.get(lexeme);

                Token token = new Token(lexeme, symbol.token, "ReservedWord");
                currentToken = token;
            } else {
                throwError("invalid_lexeme");
            }
            return nextState;
        }

        /*  
            Estado 10 -> Trata de /
            Caso c = *, vai pro estado 11 e reinicia lexema. (comentario n eh token)
            Estado final se c != *
        */
        int state10(char c) {
            int nextState = 20;

            if (c == '*') {
                nextState = 11;
                lexeme = "";
            } else {
                i--;

                Symbol symbol = symbolTable.get(lexeme);

                Token token = new Token(lexeme, symbol.token, "ReservedWord");
                currentToken = token;
            }
            return nextState;
        }

        /*  
            Estado 11 -> Trata de comentario /* 
            Caso EOF, erro
            else Caso c =  *, estado 12
        
            caso c = \n, aumenta contagem de linha
        */
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

        /*  
            Estado 12 -> Trata de comentario /* ? *
            Caso EOF, erro
            else Caso c = /, estado 0
            else caso c != *, estado 11
        
            caso c = \n, aumenta contagem de linha
        */
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

        /*  
            Estado 13 -> Trata de ' (char)
            Caso EOF, erro
            else estado 14
        */
        int state13(char c) {
            int nextState = 14;

            if (c == '#') {
                throwError("unexpected_eof");
            } else if (Character.isDigit(c) || isLetter(c)) {
                lexeme += c;
            } else {
                lexeme += c;
                throwError("invalid_lexeme");
            }
            return nextState;
        }

        /*  
            Estado 14 -> Trata de '? (char)
            Caso EOF, erro
            else Caso c != ', lexema invalido
            else estado final
        */
        int state14(char c) {
            int nextState = 20;

            if (c == '#') {
                // lineCount--;
                throwError("unexpected_eof");
            } else if (c != '\'') {
                lexeme += c;
                throwError("invalid_lexeme");
            } else {
                lexeme += c;

                Token token = new Token(lexeme, tokenValue, "Char");
                currentToken = token;
            }
            return nextState;
        }

        /*  
            Estado 15 -> Trata de " (string)
            Caso \n, erro de lexema invalido
            Caso EOF, erro de eof
            else Caso c != ", continua no estado 15
            else Caso c = ", encerra string e estado final
        */
        int state15(char c) {
            int nextState = 15;

            if (c == lineSeparator) {
                throwError("invalid_lexeme");
            } else if (c == '#') {
                throwError("unexpected_eof");
            } else if (c == '\"') {
                lexeme += c;
                nextState = 20;

                Token token = new Token(lexeme, tokenValue, "String");
                currentToken = token;

            } else {
                lexeme += c;
                if (lexeme.length() > 256)
                    throwError("invalid_lexeme");
            }
            return nextState;
        }

        /*  
            Estado 16 -> Trata de 0
            Caso c = digito, estado 2
            else c = . , estado 3
            else c = x, estado 17
            else salva 0 e estado final
        */
        int state16(char c) {
            int nextState = 17;

            if (Character.isDigit(c)) {
                lexeme += c;
                nextState = 2;
            } else if (c == '.') {
                lexeme += c;
                nextState = 3;
            } else if (c == 'x') {
                lexeme += c;
            } else {
                Token token = new Token(lexeme, tokenValue, "Integer");
                currentToken = token;

                nextState = 20;
                i--;
            }
            return nextState;
        }

        /*  
            Estado 17 -> Trata de 0x (hexadecimal)
            Caso EOF, erro de eof
            else Caso c != Hexa, erro de lexema invalido
            else Estado 18
        */
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

        /*  
            Estado 18 -> Trata de 0xD (hexadecimal)
            Caso EOF, erro de eof
            else Caso c != Hexa, erro de lexema invalido
            else salva e estado final
        */
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
            }
            return nextState;
        }

        /*  
            Estado 19 -> Trata de tokens = * + - , ( ) { } [ ]
            Salva e vai pro estado final.
        */
        int state19(char c) {
            int nextState = 20;

            Symbol symbol = symbolTable.get(lexeme);

            Token token = new Token(lexeme, symbol.token, "ReservedWord");
            currentToken = token;

            i--;

            return nextState;
        }

        /* 
            Metodo getLexeme -> Roda o analisador léxico para encontrar um token
            Quando é chamado, se inicia no estado 0 e com lexema vazio.
            Continua tratando próximo caracter enquanto o estado é diferente de 20 (final) e não tenha erros.
            Caso o caracter seja invalido, acusa erro de caracter invalido.
            
            Caso chegue no estado final, retorna token global currentToken.
        
            ----- OBSERVAÇÃO -----
            Por convenção adotamos # como EOF, visto que java não possui EOF e # não é caracter válido.
        */
        Token getLexeme(String fileStr) {
            lexeme = "";
            char c;
            currentState = 0;

            while (currentState != 20 && !pauseCompiling) {
                if (i < fileStr.length()) {
                    c = fileStr.charAt(i);
                    i++;

                    if (c == '\r') {
                        c = fileStr.charAt(i);
                        i++;
                    }
                } else {
                    // EOF
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

    /* 
        Classe do Analisador Sintático
    
        Apesar de não possuir variáveis locais, utiliza as seguintes variáveis globais:
            currentToken    -> Token atual no escopo global do programa
            pauseCompiling  -> Flag de erro global do Compilador
            token?          -> Tokens da linguagem
    
        ----- OBSERVAÇÃO -----
        Por convenção, EOF entra no analisador sintático como token 667.
        Na prática EOF não é token, mas nesta implementação utilizamos essa estratégia para identificar o tipo de erro sintático.
    
        O Parser segue a seguinte Gramática:
        (na implementação, simbolos não terminais foram traduzidos para INGLÊS para seguir a mesma convenção do restante do código)
    
            INÍCIO-> 	{D | C} eof
        
            DECLARAÇÃO-> 	(int | float | string | char) DECL_B {,DECL_B};	|
                            const id = TIPO_DECL;
        
            DECL_B-> 		id [<- TIPO_DECL ]
            TIPO_DECL-> 	[-]num | string | hexa | caractere
        
            COMANDO->	id ["[" EXP "]"] <- EXP;                    |
                        while EXP TIPO_CMD				            |
                        if EXP TIPO_CMD [else TIPO_CMD]	            |
                        readln "(" id ")";				            |
                        (write | writeln) "(" LISTA_EXP ")";		|
                        ;
        
            TIPO_CMD->	    COMANDO | "{" {COMANDO}+ "}"
            LISTA_EXP->	    EXP {, EXP}
            OPERADOR->    	= | != | < | > | <= | >=
        
            EXP-> 		EXP_B {OPERADOR EXP_B}
            EXP_B->		[-] EXP_C { (+ | - | "||") EXP_C }
            EXP_C->		EXP_D { ("*" | && | / | div | mod) EXP_D }
            EXP_D->		{!} EXP_E
            EXP_E->		(int | float) "(" EXP ")" | EXP_F
            EXP_F->     	"(" EXP ")" | id ["[" EXP "]"] | num
    */
    static class Parser {
        static int posMem = 0x10000;
        static int tempCounter = 0x0;
        static int rotCounter = 0;
        static int currentSection = 0; // .data = 0 , .text = 1
        static BufferedWriter writer;
        static {
            try {
                writer = new BufferedWriter(new FileWriter("arq.asm"));
                writer.write("section .data ; sessao de dados\n");
                writer.write("M: ; rotulo de inicio da sessao de dados\n");
                writer.write("\tresb 10000h ; reserva de temporarios\n");
            } catch (final IOException exception) {
                exception.printStackTrace();
            }
        }

        /* 
            Metodo throwParserError -> Acusa erro sintático e pausa compilador.
            Caso 667 (EOF), fim de arquivo nao esperado.
            Else token nao esperado.
        */
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

        /* 
            Método throwIdentifierError -> Acusa erros relacionados a identificadores.
            1- Se identificador nao foi declarado
            2- Se identificador esta sendo declarado de novo
            3- Se identificador possui classe incompativel
            4- Se tipo do identificador é incompativel
        */
        void throwIdentifierError(String errorType) {
            if (errorType == "id_not_declared") {
                pauseCompiling = true;
                System.out.println(lineCount);
                System.out.println("identificador nao declarado [" + currentToken.lexeme + "].");
            } else if (errorType == "id_already_declared") {
                pauseCompiling = true;
                System.out.println(lineCount);
                System.out.println("identificador ja declarado [" + currentToken.lexeme + "].");
            } else if (errorType == "id_incompatible_class") {
                pauseCompiling = true;
                System.out.println(lineCount);
                System.out.println("classe de identificador incompativel [" + currentToken.lexeme + "].");
            } else {
                pauseCompiling = true;
                System.out.println(lineCount);
                System.out.println("tipos incompativeis.");
            }
        }

        /* 
            Metodo checkToken (CasaToken) -> Verifica se token atual é o token esperado.
            Caso iguais, roda analisador léxico para pegar próximo token.
            Caso diferentes, erro.
        */
        void checkToken(int expectedToken) {
            if (expectedToken == currentToken.token)
                lexer.getLexeme(fileStr);
            else {
                throwParserError();
            }
        }

        void updatePosMem(String type, int strSize) {
            if (type == "Char")
                posMem += 1;
            else if (type == "Integer" || type == "Float" || type == "Boolean")
                posMem += 4;
            else if (type == "String") {
                posMem += strSize + 1;
            }
        }

        void updateTempCounter(String type, int strSize) {
            if (type == "Char")
                tempCounter += 1;
            else if (type == "Integer" || type == "Float" || type == "Boolean")
                tempCounter += 4;
            else if (type == "String") {
                tempCounter += strSize + 1;
            }
        }

        /* 
            Metodo identifierIsDeclared -> Verifica se identificador ja foi declarado.
        */
        boolean identifierIsDeclared(Token token) {
            Symbol symbol = symbolTable.get(token.lexeme);
            if (symbol != null)
                return true;
            return false;
        }

        void declarationToMemory(Symbol symbol, boolean hasValue, String value) throws IOException {
            if (hasValue) {
                if (symbol.type == "Char") {
                    writer.write("\tdb " + value + " ; char em " + posMem + "\n");
                } else if (symbol.type == "Integer") {
                    writer.write("\tdd " + value + " ; inteiro em " + posMem + "\n");
                } else if (symbol.type == "Float") {
                    writer.write("\tdd " + value + " ; float em " + posMem + "\n");
                } else if (symbol.type == "String") {
                    writer.write("\tdb " + value + ", 0 ; string em " + posMem + "\n");
                }
            } else {
                if (symbol.type == "Char") {
                    writer.write("\tresb 1 ; char em " + posMem + "\n");
                } else if (symbol.type == "Integer") {
                    writer.write("\tresd 1 ; inteiro em " + posMem + "\n");
                } else if (symbol.type == "Float") {
                    writer.write("\tresd 1 ; float em " + posMem + "\n");
                } else if (symbol.type == "String") {
                    writer.write("\tresb 256 ; string em " + posMem + "\n");
                }
            }
        }

        void attributionToMemory(Symbol symbol, String value) throws IOException {
            if (symbol.type == "Char") {
                writer.write("\tmov al, " + value + " ; alocando char em registrador\n");
                writer.write("\tmov [M+" + symbol.addr + "], al ; adicionando valor a endereco do id: " + symbol.lexeme
                        + "\n");
            } else if (symbol.type == "Integer") {
                writer.write("\tmov eax, " + value + " ; alocando inteiro em registrador\n");
                writer.write("\tmov [M+" + symbol.addr + "], eax ; adicionando valor a endereco do id: " + symbol.lexeme
                        + "\n");
            } else if (symbol.type == "Float") {
                writer.write("\tmovss xmm0, " + value + " ; alocando float em registrador\n");
                writer.write("\tmov [M+" + symbol.addr + "], xmm0 ; adicionando valor a endereco do id: "
                        + symbol.lexeme + "\n");
            }
        }

        int setRot() {
            int current_rot = rotCounter;
            rotCounter = rotCounter + 1;

            return current_rot;
        }

        void convertIntegerToString(EXP_args expArgs) {
            int rot_a = setRot();
            int rot_b = setRot();
            int rot_c = setRot();
            try {
                writer.write("\tmov eax, [M+" + expArgs.addr + "] ; inteiro a ser convertido\n");
                writer.write("\tmov rsi, M+" + tempCounter + "; end. string ou temp.\n");
                writer.write("\tmov rcx, 0 ; contador pilha\n");
                writer.write("\tmov rdi, 0 ; tam. string convertido\n");
                writer.write("\tcmp eax, 0 ; verifica sinal\n");
                writer.write("\tjge Rot" + rot_a + " ; salta se numero positivo\n");
                writer.write("\tmov bl, '-' ; senao, escreve sinal –\n");
                writer.write("\tmov [rsi], bl\n");
                writer.write("\tadd rsi, 1 ; incrementa indice\n");
                writer.write("\tneg eax ; toma modulo do numero\n\n");
                writer.write("Rot" + rot_a + ":\n");
                writer.write("\tmov ebx, 10 ; divisor\n\n");
                writer.write("Rot" + rot_b + ":\n");
                writer.write("\tadd rcx, 1 ; incrementa contador\n");
                writer.write("\tcdq ; estende edx:eax p/ div.\n");
                writer.write("\tidiv ebx ; divide edx;eax por ebx\n");
                writer.write("\tpush dx ; empilha valor do resto\n");
                writer.write("\tcmp eax, 0 ; verifica se quoc. eh 0\n");
                writer.write("\tjne Rot" + rot_b + " ; se nao eh 0, continua\n\n");
                writer.write("\tadd rdi,rcx ; atualiza tam. string\n\n");
                writer.write("\t; desempilha os valores e escreve o string\n\n");
                writer.write("Rot" + rot_c + ":\n");
                writer.write("\tpop dx ; desempilha valor\n");
                writer.write("\tadd dl, '0' ; transforma em caractere\n");
                writer.write("\tmov [rsi], dl ; escreve caractere\n");
                writer.write("\tadd rsi, 1 ; incrementa base\n");
                writer.write("\tsub rcx, 1 ; decrementa contador\n");
                writer.write("\tcmp rcx, 0 ; verifica pilha vazia\n");
                writer.write("\tjne Rot" + rot_c + " ; se nao pilha vazia, loop\n\n");
                updateTempCounter(expArgs.type, 0);
                writer.write("\t; executa interrupcao de saida\n");
                writer.write("\t\n");

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        void convertFloatToString(EXP_args expArgs) {
            int rot_a = setRot();
            int rot_b = setRot();
            int rot_c = setRot();
            int rot_d = setRot();
            int rot_e = setRot();

            try {
                writer.write("\tmov xmm0, [M+" + expArgs.addr + "] ; real a ser convertido\n");
                writer.write("\tmov rsi, M+" + tempCounter + "; end. temporario\n");
                writer.write("\tmov rcx, 0 ; contador pilha\n");
                writer.write("\tmov rdi, 6 ; precisao 6 casas compart\n");
                writer.write("\tmov rbx, 10 ; divisor\n");
                writer.write("\tcvtsi2ss xmm2, rbx ; divisor real\n");
                writer.write("\tsubss xmm1, xmm1 ; zera registrador\n");
                writer.write("\tcomiss xmm0, xmm1 ; verifica sinal\n");
                writer.write("\tjae Rot" + rot_a + " ; salta (rot_a) se numero positivo\n");
                writer.write("\tmov dl, '-' ; senao, escreve sinal –\n");
                writer.write("\tmov [rsi], dl\n");
                writer.write("\tmov rdx, -1 ; carrega -1 em RDX\n");
                writer.write("\tcvtsi2ss xmm1, rdx ; converte para real\n");
                writer.write("\tmulss xmm0, xmm1 ; Toma modulo\n");
                writer.write("\tadd rsi, 1 ; incrementa indice\n\n");
                writer.write("Rot" + rot_a + ":\n");
                writer.write("\troundss xmm1, xmm0, 0b0011 ; parte inteira xmm1\n");
                writer.write("\tsubss xmm0, xmm1 ; parte fracionaria xmm0\n");
                writer.write("\tcvtss2si rax, xmm1 ; convertido para int\n\n");
                writer.write("\t;converte parte inteira que esta em rax\n\n");
                writer.write("Rot" + rot_b + ":\n");
                writer.write("\tadd rcx, 1 ; incrementa contador\n");
                writer.write("\tcdq ; estende edx:eax p/ div.\n");
                writer.write("\tidiv ebx ; divide edx;eax por ebx\n");
                writer.write("\tpush dx ; empilha valor do resto\n");
                writer.write("\tcmp eax, 0 ; verifica se quoc. eh 0\n");
                writer.write("\tjne " + rot_b + " ; se nao eh 0, continua, else rot_b\n\n");
                writer.write("\tsub rdi, rcx ; decrementa precisao\n\n");
                writer.write("\t; desempilha valores e escreve parte int\n\n");
                writer.write("Rot" + rot_c + ":\n");
                writer.write("\tpop dx ; desempilha valor\n");
                writer.write("\tadd dl, '0' ; transforma em caractere\n");
                writer.write("\tmov [rsi], dl ; escreve caractere\n");
                writer.write("\tadd rsi, 1 ; incrementa base\n");
                writer.write("\tsub rcx, 1 ; decrementa contador\n");
                writer.write("\tcmp rcx, 0 ; verifica pilha vazia\n");
                writer.write("\tjne Rot" + rot_c + " ; se nao pilha vazia, loop, else rot_c\n\n");
                writer.write("\tmov dl, '.' ; escreve ponto decimal\n");
                writer.write("\tmov [rsi], dl\n");
                writer.write("\tadd rsi, 1 ; incrementa base\n\n");
                writer.write("\t; converte parte fracionaria que esta em xmm0\n\n");
                writer.write("Rot" + rot_d + ":\n");
                writer.write("\tcmp rdi, 0 ; verifica precisao\n");
                writer.write("\tjle Rot" + rot_e + " ; terminou precisao ?, else rot_e\n");
                writer.write("\tmulss xmm0,xmm2  ; desloca para esquerda\n");
                writer.write("\troundss xmm1,xmm0,0b0011 ; parte inteira xmm1\n");
                writer.write("\tsubss xmm0,xmm1    ; atualiza xmm0\n");
                writer.write("\tcvtss2si rdx, xmm1 ; convertido para int\n");
                writer.write("\tadd dl, '0' ; transforma em caractere\n");
                writer.write("\tmov [rsi], dl ; escreve caractere\n");
                writer.write("\tadd rsi, 1 ; incrementa base\n");
                writer.write("\tsub rdi, 1 ; decrementa precisao\n");
                writer.write("\tjmp Rot" + rot_d + "\n\n");
                writer.write("\t; impressao\n\n");
                writer.write("Rot" + rot_e + ":\n");
                writer.write("\tmov dl, 0 ; fim string, opcional\n");
                writer.write("\tmov [rsi], dl ; escreve caractere\n");
                writer.write("\tmov rdx, rsi ; calc tam str convertido\n");
                writer.write("\tmov rbx, M+" + tempCounter + "\n");
                writer.write("\tsub rdx, rbx ; tam=rsi-M-buffer.end\n");
                writer.write("\tmov rsi, M+" + tempCounter + "; endereco do buffer\n\n");
                updateTempCounter(expArgs.type, 0);
                writer.write(
                        "\t; executa interrupcao de saida. rsi e rdx ja foram calculados entao usar so as instrucoes para a chamada do kernel.\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void translationWrite(EXP_args expArgs, int endereco) {
            int rot = setRot();

            if (expArgs.type != "Boolean") {
                if (expArgs.type == "Integer") {
                    convertIntegerToString(expArgs);
                } else if (expArgs.type == "Float") {
                    convertFloatToString(expArgs);
                }
            }
            try {
                writer.write("\tmov rsi, M+" + (expArgs.addr - (2 * endereco))
                        + " ; registrador recebe endereco da string\n");
                writer.write("\tmov rdx, rsi ; rdx = rsi\n");
                writer.write("Rot" + rot + ": \n");
                writer.write("\tmov al, [rdx] ; registrador recebe primeiro caractere da string \n");
                writer.write("\tadd rdx, 1 ; incrementa rdx\n");
                writer.write("\tcmp al, 0 ; al == 0 ? se True, fim da string\n");
                writer.write("\tjne Rot" + rot + "\n");
                writer.write("\tsub rdx, M+ " + (expArgs.addr - ((2 * endereco) - 1))
                        + " ; removendo offset (byte 0) do endereco\n");
                writer.write("\tmov rax, 1 ; chamada para saida\n");
                writer.write("\tmov rdi, 1 ; saida para tela\n");
                writer.write("\tsyscall\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        void translationWriteln() {
            try {
                writer.write("section .data\n");
                writer.write("\tdb \"\\n\" ; passa um linebreak\n");
                writer.write("section .text\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        class EXP_args {
            String type;
            int addr;

            EXP_args() {
                type = "";
                addr = 0;
            }
        }

        /* 
            Na gramática: INÍCIO-> 	{DECL_A | COMANDO} eof
        
            Metodo START -> Símbolo não terminal inicial da gramática. 
            Aceita declaração ou comando até EOF ou erro.
        */
        void START() throws IOException {
            while (currentToken.token != 667 && !pauseCompiling) {
                if (currentToken.token == tokenStr || currentToken.token == tokenConst || currentToken.token == tokenInt
                        || currentToken.token == tokenChar || currentToken.token == tokenFloat) {
                    if (currentSection != 0) {
                        writer.write("section .data ; sessao de dados\n");
                    }
                    currentSection = 0;
                    DECL_A();
                } else {
                    if (currentSection != 1) {
                        writer.write("section .text ; sessao de codigo\n");
                        writer.write("global _start ; Ponto inicial do programa\n");
                        writer.write("_start: ; Inicio do programa\n");
                    }
                    currentSection = 1;
                    COMMAND();
                }
            }
            writer.write("\tmov rax, 60 ; Chamada de saida\n");
            writer.write("\tmov rdi, 0 ; Codigo de saida sem erros\n");
            writer.write("syscall ; Chama o kernel\n");
            writer.close();
        }

        /* 
            Na gramática: DECL_A-> 	(int | float | string | char) DECL_B {,DECL_B};	|
                                    const id = TIPO_DECL;
        
            Metodo DECL_A -> Símbolo não terminal de Declaração da gramática. 
            Caso inicio com (int | float | string | char), vai para DECL_B e pode rodar ,DECL_B 0 ou + vezes depois.
            Caso inicio com const, proximos tokens são um identificador, token de igual e vai para TIPO_DECL.
        */
        void DECL_A() {
            if (!pauseCompiling) {
                String idType = "";
                if (currentToken.token == tokenStr) {
                    idType = "String";
                    checkToken(tokenStr);
                    if (pauseCompiling)
                        return;
                    DECL_B(idType);
                    if (pauseCompiling)
                        return;
                    while (currentToken.token == tokenComma) {
                        checkToken(tokenComma);
                        if (pauseCompiling)
                            return;
                        DECL_B(idType);
                        if (pauseCompiling)
                            return;
                    }
                } else if (currentToken.token == tokenConst) {
                    boolean minus = false;
                    String idLexeme = "";
                    checkToken(tokenConst);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenId) {
                        if (identifierIsDeclared(currentToken)) {
                            throwIdentifierError("id_already_declared");
                            return;
                        }
                        idLexeme = currentToken.lexeme;
                        checkToken(tokenId);
                        if (pauseCompiling)
                            return;
                        if (currentToken.token == tokenEqual) {
                            checkToken(tokenEqual);
                            if (pauseCompiling)
                                return;
                            if (currentToken.token == tokenMinus) {
                                checkToken(tokenMinus);
                                minus = true;
                                if (pauseCompiling)
                                    return;
                            }
                            if (currentToken.token == tokenValue) {
                                if (minus && !(currentToken.type == "Integer" || currentToken.type == "Float")) {
                                    throwIdentifierError("incompatible_types");
                                    return;
                                }
                                idType = currentToken.type;

                                Symbol currentSymbol = new Symbol(idLexeme, tokenId);
                                currentSymbol.addr = posMem;
                                currentSymbol.classification = "const";
                                currentSymbol.type = idType;
                                symbolTable.put(currentSymbol.lexeme, currentSymbol);

                                try {
                                    declarationToMemory(currentSymbol, true, currentToken.lexeme);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                updatePosMem(idType, currentToken.lexeme.length());

                                checkToken(tokenValue);
                                if (pauseCompiling)
                                    return;
                            } else
                                throwParserError();
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenInt) {
                    idType = "Integer";
                    checkToken(tokenInt);
                    if (pauseCompiling)
                        return;
                    DECL_B(idType);
                    if (pauseCompiling)
                        return;
                    while (currentToken.token == tokenComma) {
                        checkToken(tokenComma);
                        if (pauseCompiling)
                            return;
                        DECL_B(idType);
                        if (pauseCompiling)
                            return;
                    }
                } else if (currentToken.token == tokenChar) {
                    idType = "Char";
                    checkToken(tokenChar);
                    if (pauseCompiling)
                        return;
                    DECL_B(idType);
                    if (pauseCompiling)
                        return;
                    while (currentToken.token == tokenComma) {
                        checkToken(tokenComma);
                        if (pauseCompiling)
                            return;
                        DECL_B(idType);
                        if (pauseCompiling)
                            return;
                    }
                } else if (currentToken.token == tokenFloat) {
                    idType = "Float";
                    checkToken(tokenFloat);
                    if (pauseCompiling)
                        return;
                    DECL_B(idType);
                    if (pauseCompiling)
                        return;
                    while (currentToken.token == tokenComma) {
                        checkToken(tokenComma);
                        if (pauseCompiling)
                            return;
                        DECL_B(idType);
                        if (pauseCompiling)
                            return;
                    }
                } else
                    throwParserError();

                if (currentToken.token == tokenSemiColon) {
                    checkToken(tokenSemiColon);
                } else {
                    throwParserError();
                }
            }
        }

        /* 
            Na gramática: DECL_B-> 	id [<- TIPO_DECL ]
        
            Metodo DECL_B -> Símbolo não terminal auxiliar 1 para Declaração
            Le token identificador e opcionalmente pode ter uma atribuição <- TIPO_DECL
        */
        void DECL_B(String idType) {
            if (!pauseCompiling) {
                boolean minus = false;
                boolean hasValue = false;
                String value = "";
                if (currentToken.token == tokenId) {
                    if (identifierIsDeclared(currentToken)) {
                        throwIdentifierError("id_already_declared");
                        return;
                    }

                    Symbol currentSymbol = new Symbol(currentToken.lexeme, tokenId);
                    currentSymbol.addr = posMem;
                    currentSymbol.classification = "var";
                    currentSymbol.type = idType;
                    symbolTable.put(currentSymbol.lexeme, currentSymbol);

                    checkToken(tokenId);
                    if (pauseCompiling)
                        return;

                    if (currentToken.token == tokenAtrib) {
                        checkToken(tokenAtrib);
                        if (pauseCompiling)
                            return;
                        if (currentToken.token == tokenMinus) {
                            checkToken(tokenMinus);
                            minus = true;
                            if (pauseCompiling)
                                return;
                        }
                        if (currentToken.token == tokenValue) {
                            if (minus && !(currentToken.type == "Integer" || currentToken.type == "Float")) {
                                throwIdentifierError("incompatible_types");
                                return;
                            }

                            if ((idType == "Integer" && currentToken.type != "Integer")
                                    || (idType == "Float"
                                            && (currentToken.type != "Float" && currentToken.type != "Integer"))
                                    || (idType == "String" && currentToken.type != "String")
                                    || (idType == "Char" && currentToken.type != "Char")) {
                                throwIdentifierError("incompatible_types");
                                return;
                            }

                            hasValue = true;
                            value = currentToken.lexeme;

                            checkToken(tokenValue);
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                        if (pauseCompiling)
                            return;
                    }

                    try {
                        declarationToMemory(currentSymbol, hasValue, value);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (hasValue)
                        updatePosMem(idType, value.length());
                    else
                        updatePosMem(idType, 255);
                }
            }
        }

        /* 
            Na gramática:
            COMANDO->	id ["[" EXP_A "]"] <- EXP_A;		            |
        	            while EXP_A TIPO_CMD				            |
                        if EXP_A TIPO_CMD [else TIPO_CMD]	            |
                        readln "(" id ")";				            |
                        (write | writeln) "(" LISTA_EXP ")";		|
                        ;
        
            Metodo COMANDO -> Símbolo não terminal para Comandos da linguagem
        
            1. Caso leia um token identificador, opcionalmente podera ler [EXP_A]. Em seguida, sera necessario um token de atribuicao, vai chamar EXP_A e finalmente um token de ponto e virgula.
            2. Caso leia um token while, sera chamado EXP_A e depois TIPO_CMD.
            3. Caso leia um token if, sera chamado EXP_A e depois TIPO_CMD. Opcionalmente pode-se ter um token else seguido de uma chamada TIPO_CMD.
            4. Caso leia um token readln, deverao aparecer os tokens (identificador), seguido de token ponto e virgula.
            5. Caso leia token write ou writeln, sera chamado LISTA_EXP dentro de tokens ( e ), seguido de token ponto e virgula.
            6. Caso leia token ponto e virgula, so chama o CasaToken mesmo.
            7. Caso contrario, erro.
        */
        void COMMAND() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenId) {
                    boolean isStringIndex = false;

                    if (!identifierIsDeclared(currentToken)) {
                        throwIdentifierError("id_not_declared");
                        return;
                    }

                    Symbol currentSymbol = symbolTable.get(currentToken.lexeme);

                    if (currentSymbol.classification != "var") {
                        throwIdentifierError("id_incompatible_class");
                        return;
                    }

                    checkToken(tokenId);
                    if (pauseCompiling)
                        return;

                    if (currentToken.token == tokenOpenSq) {
                        if (currentSymbol.type != "String") {
                            throwIdentifierError("incompatible_types");
                            return;
                        }

                        isStringIndex = true;

                        checkToken(tokenOpenSq);
                        if (pauseCompiling)
                            return;

                        tempCounter = 0;
                        EXP_args expArgsA1 = new EXP_args();
                        EXP_A(expArgsA1);

                        if (pauseCompiling)
                            return;

                        if (expArgsA1.type != "Integer") {
                            throwIdentifierError("incompatible_types");
                            return;
                        }

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

                        tempCounter = 0;
                        EXP_args expArgsA2 = new EXP_args();
                        EXP_A(expArgsA2);

                        if (pauseCompiling)
                            return;

                        if ((isStringIndex && expArgsA2.type != "Char")
                                || (!isStringIndex && (!(expArgsA2.type == "Integer" && currentSymbol.type == "Float")
                                        && expArgsA2.type != currentSymbol.type))) {
                            throwIdentifierError("incompatible_types");
                            return;
                        }

                        try {
                            attributionToMemory(currentSymbol, "[M+" + expArgsA2.addr + "]");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (currentToken.token == tokenSemiColon) {
                            checkToken(tokenSemiColon);
                            if (pauseCompiling)
                                return;
                        } else {
                            throwParserError();
                        }
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenWhile) {
                    checkToken(tokenWhile);
                    if (pauseCompiling)
                        return;

                    tempCounter = 0;
                    EXP_args expArgsA3 = new EXP_args();
                    EXP_A(expArgsA3);
                    if (pauseCompiling)
                        return;

                    if (expArgsA3.type != "Boolean") {
                        throwIdentifierError("incompatible_types");
                        return;
                    }

                    CMD_TYPE();
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenIf) {
                    checkToken(tokenIf);
                    if (pauseCompiling)
                        return;

                    tempCounter = 0;
                    EXP_args expArgsA4 = new EXP_args();
                    EXP_A(expArgsA4);
                    if (pauseCompiling)
                        return;

                    if (expArgsA4.type != "Boolean") {
                        throwIdentifierError("incompatible_types");
                        return;
                    }

                    CMD_TYPE();
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenElse) {
                        checkToken(tokenElse);
                        if (pauseCompiling)
                            return;
                        CMD_TYPE();
                        if (pauseCompiling)
                            return;
                    }
                } else if (currentToken.token == tokenRead) {
                    checkToken(tokenRead);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenPar) {
                        checkToken(tokenOpenPar);
                        if (pauseCompiling)
                            return;
                        if (currentToken.token == tokenId) {
                            if (!identifierIsDeclared(currentToken)) {
                                throwIdentifierError("id_not_declared");
                                return;
                            }
                            if (symbolTable.get(currentToken.lexeme).classification != "var") {
                                throwIdentifierError("id_incompatible_class");
                                return;
                            }
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
                                } else {
                                    throwParserError();
                                }
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
                            } else {
                                throwParserError();
                            }
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
                        translationWriteln();
                        if (currentToken.token == tokenClosePar) {
                            checkToken(tokenClosePar);
                            if (pauseCompiling)
                                return;
                            if (currentToken.token == tokenSemiColon) {
                                checkToken(tokenSemiColon);
                                if (pauseCompiling)
                                    return;
                            } else {
                                throwParserError();
                            }
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenSemiColon) {
                    checkToken(tokenSemiColon);
                    if (pauseCompiling)
                        return;
                } else {
                    throwParserError();
                }
            }
        }

        /* 
            Na gramática: TIPO_CMD->	C | "{" {C}+ "}"
        
            Metodo TIPO_CMD -> Símbolo não terminal para comando ou bloco de comandos.
            Caso token = {, devera rodar "{" {COMANDO}+ "}". (bloco)
            Caso contrario, so roda COMANDO.
        */
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

        /* 
            Na gramática: LISTA_EXP->	EXP {, EXP}
        
            Metodo LISTA_EXP -> Símbolo não terminal para lista de expressao.
            Caso token = {, devera rodar "{" {COMANDO}+ "}". (bloco)
            Caso contrario, so roda COMANDO.
        */
        void EXP_LIST() {
            if (!pauseCompiling) {
                tempCounter = 0;
                EXP_args expArgsA1 = new EXP_args();
                EXP_A(expArgsA1);
                if (pauseCompiling)
                    return;

                int endereco = 0;
                translationWrite(expArgsA1, endereco);

                while (currentToken.token == tokenComma) {
                    checkToken(tokenComma);
                    if (pauseCompiling)
                        return;

                    tempCounter = 0;
                    EXP_args expArgsA2 = new EXP_args();
                    EXP_A(expArgsA2);
                    if (pauseCompiling)
                        return;

                    endereco++;
                    translationWrite(expArgsA2, endereco);
                }
            }
        }

        /* 
            Na gramática: OPERADOR->   = | != | < | > | <= | >=
        
            Metodo OPERADOR -> Símbolo não terminal para operadores.
            Caso token = (= | != | < | > | <= | >=), continua
            Caso seja diferente, erro
        */
        void OPERATOR(EXP_args operatorArgs) {
            if (!pauseCompiling) {
                if (currentToken.token == tokenEqual) {
                    if (operatorArgs.type != "String" && operatorArgs.type != "Integer" && operatorArgs.type != "Float"
                            && operatorArgs.type != "Char") {
                        throwIdentifierError("incompatible_types");
                        return;
                    }
                    checkToken(tokenEqual);
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenDif) {
                    if (operatorArgs.type != "Integer" && operatorArgs.type != "Float" && operatorArgs.type != "Char") {
                        throwIdentifierError("incompatible_types");
                        return;
                    }
                    checkToken(tokenDif);
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenLess) {
                    if (operatorArgs.type != "Integer" && operatorArgs.type != "Float" && operatorArgs.type != "Char") {
                        throwIdentifierError("incompatible_types");
                        return;
                    }
                    checkToken(tokenLess);
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenGtr) {
                    if (operatorArgs.type != "Integer" && operatorArgs.type != "Float" && operatorArgs.type != "Char") {
                        throwIdentifierError("incompatible_types");
                        return;
                    }
                    checkToken(tokenGtr);
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenLessEqual) {
                    if (operatorArgs.type != "Integer" && operatorArgs.type != "Float" && operatorArgs.type != "Char") {
                        throwIdentifierError("incompatible_types");
                        return;
                    }
                    checkToken(tokenLessEqual);
                    if (pauseCompiling)
                        return;
                } else if (currentToken.token == tokenGtrEqual) {
                    if (operatorArgs.type != "Integer" && operatorArgs.type != "Float" && operatorArgs.type != "Char") {
                        throwIdentifierError("incompatible_types");
                        return;
                    }
                    checkToken(tokenGtrEqual);
                    if (pauseCompiling)
                        return;
                } else
                    throwParserError();
            }
        }

        /* 
            Na gramática: EXP-> EXP_B {OPERADOR EXP_B}
        
            Metodo EXP -> Símbolo não terminal para expressoes.
            Chama metodo EXP_B e pode rodar OPERADOR EXP_B opcionalmente, quantas vezes quiser.
        */
        void EXP_A(EXP_args expArgsA) {
            if (!pauseCompiling) {
                EXP_args expArgsB1 = new EXP_args();
                EXP_B(expArgsB1);
                if (pauseCompiling)
                    return;

                expArgsA.type = expArgsB1.type;
                expArgsA.addr = expArgsB1.addr;

                if (currentToken.token == tokenEqual || currentToken.token == tokenDif
                        || currentToken.token == tokenLess || currentToken.token == tokenGtr
                        || currentToken.token == tokenLessEqual || currentToken.token == tokenGtrEqual) {

                    EXP_args operatorArgs = new EXP_args();
                    operatorArgs.type = expArgsB1.type;

                    OPERATOR(operatorArgs);
                    if (pauseCompiling)
                        return;

                    EXP_args expArgsB2 = new EXP_args();
                    EXP_B(expArgsB2);
                    if (pauseCompiling)
                        return;

                    if (((expArgsB1.type == "Integer" || expArgsB1.type == "Float")
                            && (expArgsB2.type != "Integer" && expArgsB2.type != "Float"))
                            || (expArgsB1.type == "Char" && expArgsB2.type != "Char")
                            || (expArgsB1.type == "String" && expArgsB2.type != "String")) {
                        throwIdentifierError("incompatible_types");
                        return;
                    }

                    expArgsA.type = "Boolean";
                }
            }
        }

        /* 
            Na gramática: EXP_B-> [-] EXP_C { (+ | - | "||") EXP_C }
        
            Metodo EXP_B -> Símbolo não terminal auxiliar 1 para expressoes.
            Opcionalmente pode iniciar com token de menos. Chama EXP_C e pode opcionalmente rodar (+ | - | "||") EXP_C, quantas vezes quiser.
        */
        void EXP_B(EXP_args expArgsB) {
            if (!pauseCompiling) {
                boolean minus = false;
                if (currentToken.token == tokenMinus) {
                    checkToken(tokenMinus);
                    if (pauseCompiling)
                        return;
                    minus = true;
                }

                EXP_args expArgsC1 = new EXP_args();
                EXP_C(expArgsC1);
                if (pauseCompiling)
                    return;

                if (minus && !(expArgsC1.type == "Integer" || expArgsC1.type == "Float")) {
                    throwIdentifierError("incompatible_types");
                    return;
                }

                if (minus) {
                    expArgsB.addr = tempCounter;
                    updateTempCounter(expArgsC1.type, 0);
                    try {
                        if (expArgsC1.type == "Integer") {
                            writer.write("\tmov eax, [M+" + expArgsC1.addr
                                    + "] ; alocando valor em end. de expArgsC1 a registrador\n");
                            writer.write("\tneg eax ; negando valor de registrador\n");
                            writer.write("\tmov [M+" + expArgsB.addr
                                    + "], eax ; alocando valor negado em end. de expArgsB\n");
                        } else {
                            writer.write("\tmov rax, [M+" + expArgsC1.addr
                                    + "] ; alocando valor em end. de expArgsC1 a registrador\n");
                            writer.write("\tcvtsi2ss xmm0, rax ; int64 para float\n");
                            writer.write("\tmov rbx, -1 ; alocando -1 em rbx\n");
                            writer.write("\tcvtsi2ss xmm1, rbx ; -1 (int64) para float\n");
                            writer.write("\tmulss xmm0, xmm1 ; xmm * -1\n");
                            writer.write("\tmovss [M+" + expArgsB.addr
                                    + "], xmm0 ; alocando valor negado em end. de expArgsB\n");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    expArgsB.addr = expArgsC1.addr;
                }

                expArgsB.type = expArgsC1.type;

                while (currentToken.token == tokenPlus || currentToken.token == tokenMinus
                        || currentToken.token == tokenOr) {
                    int operator = currentToken.token;
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

                    EXP_args expArgsC2 = new EXP_args();
                    EXP_C(expArgsC2);
                    if (pauseCompiling)
                        return;

                    if (operator == tokenPlus) {
                        if ((expArgsC1.type != "Integer" && expArgsC1.type != "Float")
                                || (expArgsC2.type != "Integer" && expArgsC2.type != "Float")
                                || ((expArgsC1.type == "Integer" || expArgsC1.type == "Float")
                                        && (expArgsC2.type != "Integer" && expArgsC2.type != "Float"))) {
                            throwIdentifierError("incompatible_types");
                            return;
                        } else if (expArgsC1.type == "Float" || expArgsC2.type == "Float") {
                            expArgsB.type = "Float";

                            if (expArgsC1.type == "Float") {
                                try {
                                    writer.write("\tmovss xmm0, [M+" + expArgsB.addr
                                            + "] ; alocando valor em end. de expArgsB a registrador\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    writer.write("\tmov rax, [M+" + expArgsB.addr
                                            + "] ; alocando valor em end. de expArgsB a registrador\n");
                                    writer.write("\tcvtsi2ss xmm0, rax ; int64 para float\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (expArgsC2.type == "Float") {
                                try {
                                    writer.write("\tmovss xmm1, [M+" + expArgsC2.addr
                                            + "] ; alocando valor em end. de expArgsC2 a registrador\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    writer.write("\tmov rbx, [M+" + expArgsC2.addr
                                            + "] ; alocando valor em end. de expArgsC2 a registrador\n");
                                    writer.write("\tcvtsi2ss xmm1, rbx ; int64 para float\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            try {
                                writer.write("\taddss xmm0, xmm1 ; xmm0 + xmm1\n");
                                expArgsB.addr = tempCounter;
                                updateTempCounter(expArgsB.type, 0);
                                writer.write("\tmovss [M+" + expArgsB.addr
                                        + "], xmm0 ; aloca resultado da soma em endereco de expArgsB\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            expArgsB.type = "Integer";

                            try {
                                writer.write("\tmov eax, [M+" + expArgsB.addr
                                        + "] ; alocando valor em end. de expArgsB a registrador\n");
                                writer.write("\tmov ebx, [M+" + expArgsC2.addr
                                        + "] ; alocando valor em end. de expArgsC2 a registrador\n");
                                writer.write("\tadd eax, ebx ; eax + ebx\n");
                                expArgsB.addr = tempCounter;
                                updateTempCounter(expArgsB.type, 0);
                                writer.write("\tmov [M+" + expArgsB.addr
                                        + "], eax ; aloca resultado da soma em endereco de expArgsB\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (operator == tokenMinus) {
                        if ((expArgsC1.type != "Integer" && expArgsC1.type != "Float")
                                || (expArgsC2.type != "Integer" && expArgsC2.type != "Float")
                                || ((expArgsC1.type == "Integer" || expArgsC1.type == "Float")
                                        && (expArgsC2.type != "Integer" && expArgsC2.type != "Float"))) {
                            throwIdentifierError("incompatible_types");
                            return;
                        } else if (expArgsC1.type == "Float" || expArgsC2.type == "Float") {
                            expArgsB.type = "Float";

                            if (expArgsC1.type == "Float") {
                                try {
                                    writer.write("\tmovss xmm0, [M+" + expArgsB.addr
                                            + "] ; alocando valor em end. de expArgsB a registrador\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    writer.write("\tmov rax, [M+" + expArgsB.addr
                                            + "] ; alocando valor em end. de expArgsB a registrador\n");
                                    writer.write("\tcvtsi2ss xmm0, rax ; int64 para float\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (expArgsC2.type == "Float") {
                                try {
                                    writer.write("\tmovss xmm1, [M+" + expArgsC2.addr
                                            + "] ; alocando valor em end. de expArgsC2 a registrador\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    writer.write("\tmov rbx, [M+" + expArgsC2.addr
                                            + "] ; alocando valor em end. de expArgsC2 a registrador\n");
                                    writer.write("\tcvtsi2ss xmm1, rbx ; int64 para float\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            try {
                                writer.write("\tsubss xmm0, xmm1 ; xmm0 - xmm1\n");
                                expArgsB.addr = tempCounter;
                                updateTempCounter(expArgsB.type, 0);
                                writer.write("\tmovss [M+" + expArgsB.addr
                                        + "], xmm0 ; aloca resultado da subtracao em endereco de expArgsB\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            expArgsB.type = "Integer";

                            try {
                                writer.write("\tmov eax, [M+" + expArgsB.addr
                                        + "] ; alocando valor em end. de expArgsB a registrador\n");
                                writer.write("\tmov ebx, [M+" + expArgsC2.addr
                                        + "] ; alocando valor em end. de expArgsC2 a registrador\n");
                                writer.write("\tsub eax, ebx ; eax - ebx\n");
                                expArgsB.addr = tempCounter;
                                updateTempCounter(expArgsB.type, 0);
                                writer.write("\tmov [M+" + expArgsB.addr
                                        + "], eax ; aloca resultado da subtracao em endereco de expArgsB\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (operator == tokenOr) {
                        if (!(expArgsC1.type == "Boolean" && expArgsC2.type == "Boolean")) {
                            throwIdentifierError("incompatible_types");
                            return;
                        }

                        try {
                            writer.write("\tmov eax, [M+" + expArgsB.addr
                                    + "] ; alocando valor em end. de expArgsB a registrador\n");
                            writer.write("\tmov ebx, [M+" + expArgsC2.addr
                                    + "] ; alocando valor em end. de expArgsC2 a registrador\n");
                            writer.write("\tmov ecx, 2 ; alocando valor 2 a ecx\n");
                            writer.write("\tadd eax, ebx ; eax = eax + ebx\n");
                            writer.write("\tidiv ecx ; dividindo eax por 2\n");
                            writer.write("\tadd eax, edx ; somando quociente e resto da divisao (Or logico)\n");
                            expArgsB.addr = tempCounter;
                            updateTempCounter(expArgsB.type, 0);
                            writer.write("\tmov [M+" + expArgsB.addr
                                    + "], eax ; aloca resultado do Or em endereco de expArgsB\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        /* 
            Na gramática: EXP_C-> EXP_D { ("*" | && | / | div | mod) EXP_D }
        
            Metodo EXP_C -> Símbolo não terminal auxiliar 2 para expressoes.
            Chama EXP_D e pode opcionalmente rodar ("*" | && | / | div | mod) EXP_D, quantas vezes quiser.
        */
        void EXP_C(EXP_args expArgsC) {
            if (!pauseCompiling) {
                EXP_args expArgsD1 = new EXP_args();
                EXP_D(expArgsD1);
                if (pauseCompiling)
                    return;

                expArgsC.type = expArgsD1.type;
                expArgsC.addr = expArgsD1.addr;

                while (currentToken.token == tokenMult || currentToken.token == tokenAnd
                        || currentToken.token == tokenDiv || currentToken.token == tokenDiv2
                        || currentToken.token == tokenMod) {

                    int operator = currentToken.token;

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
                    } else if (currentToken.token == tokenDiv2) {
                        checkToken(tokenDiv2);
                        if (pauseCompiling)
                            return;
                    } else if (currentToken.token == tokenMod) {
                        checkToken(tokenMod);
                        if (pauseCompiling)
                            return;
                    } else
                        throwParserError();

                    EXP_args expArgsD2 = new EXP_args();
                    EXP_D(expArgsD2);
                    if (pauseCompiling)
                        return;

                    if (operator == tokenMult) {
                        if ((expArgsD1.type != "Integer" && expArgsD1.type != "Float")
                                || (expArgsD2.type != "Integer" && expArgsD2.type != "Float")
                                || ((expArgsD1.type == "Integer" || expArgsD1.type == "Float")
                                        && (expArgsD2.type != "Integer" && expArgsD2.type != "Float"))) {
                            throwIdentifierError("incompatible_types");
                            return;
                        } else if (expArgsD1.type == "Float" || expArgsD2.type == "Float") {
                            expArgsC.type = "Float";

                            if (expArgsD1.type == "Float") {
                                try {
                                    writer.write("\tmovss xmm0, [M+" + expArgsC.addr
                                            + "] ; alocando valor em end. de expArgsC a registrador\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    writer.write("\tmov rax, [M+" + expArgsC.addr
                                            + "] ; alocando valor em end. de expArgsC a registrador\n");
                                    writer.write("\tcvtsi2ss xmm0, rax ; int64 para float\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (expArgsD2.type == "Float") {
                                try {
                                    writer.write("\tmovss xmm1, [M+" + expArgsD2.addr
                                            + "] ; alocando valor em end. de expArgsD2 a registrador\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    writer.write("\tmov rbx, [M+" + expArgsD2.addr
                                            + "] ; alocando valor em end. de expArgsD2 a registrador\n");
                                    writer.write("\tcvtsi2ss xmm1, rbx ; int64 para float\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            try {
                                writer.write("\tmulss xmm0, xmm1 ; xmm0 * xmm1\n");
                                expArgsC.addr = tempCounter;
                                updateTempCounter(expArgsC.type, 0);
                                writer.write("\tmovss [M+" + expArgsC.addr
                                        + "], xmm0 ; aloca resultado da mult. em endereco de expArgsC\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            expArgsC.type = "Integer";

                            try {
                                writer.write("\tmov eax, [M+" + expArgsC.addr
                                        + "] ; alocando valor em end. de expArgsC a registrador\n");
                                writer.write("\tmov ebx, [M+" + expArgsD2.addr
                                        + "] ; alocando valor em end. de expArgsD2 a registrador\n");
                                writer.write("\timul ebx ; eax * ebx\n");
                                expArgsC.addr = tempCounter;
                                updateTempCounter(expArgsC.type, 0);
                                writer.write("\tmov [M+" + expArgsC.addr
                                        + "], eax ; aloca resultado da mult. em endereco de expArgsC\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (operator == tokenDiv) {
                        if ((expArgsD1.type != "Integer" && expArgsD1.type != "Float")
                                || (expArgsD2.type != "Integer" && expArgsD2.type != "Float")
                                || ((expArgsD1.type == "Integer" || expArgsD1.type == "Float")
                                        && (expArgsD2.type != "Integer" && expArgsD2.type != "Float"))) {
                            throwIdentifierError("incompatible_types");
                            return;
                        } else {
                            expArgsC.type = "Float";

                            if (expArgsD1.type == "Float") {
                                try {
                                    writer.write("\tmovss xmm0, [M+" + expArgsC.addr
                                            + "] ; alocando valor em end. de expArgsC a registrador\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    writer.write("\tmov rax, [M+" + expArgsC.addr
                                            + "] ; alocando valor em end. de expArgsC a registrador\n");
                                    writer.write("\tcvtsi2ss xmm0, rax ; int64 para float\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (expArgsD2.type == "Float") {
                                try {
                                    writer.write("\tmovss xmm1, [M+" + expArgsD2.addr
                                            + "] ; alocando valor em end. de expArgsD2 a registrador\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    writer.write("\tmov rbx, [M+" + expArgsD2.addr
                                            + "] ; alocando valor em end. de expArgsD2 a registrador\n");
                                    writer.write("\tcvtsi2ss xmm1, rbx ; int64 para float\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            try {
                                writer.write("\tdivss xmm0, xmm1 ; xmm0 / xmm1\n");
                                expArgsC.addr = tempCounter;
                                updateTempCounter(expArgsC.type, 0);
                                writer.write("\tmovss [M+" + expArgsC.addr
                                        + "], xmm0 ; aloca resultado da divisao em endereco de expArgsC\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (operator == tokenDiv2) {
                        if (!(expArgsD1.type == "Integer" && expArgsD2.type == "Integer")) {
                            throwIdentifierError("incompatible_types");
                            return;
                        }

                        try {
                            writer.write("\tmov eax, [M+" + expArgsC.addr
                                    + "] ; alocando valor em end. de expArgsC a registrador\n");
                            writer.write("\tmov ebx, [M+" + expArgsD2.addr
                                    + "] ; alocando valor em end. de expArgsD2 a registrador\n");
                            writer.write("\tidiv ebx ; eax div ebx\n");
                            expArgsC.addr = tempCounter;
                            updateTempCounter(expArgsC.type, 0);
                            writer.write("\tmov [M+" + expArgsC.addr
                                    + "], eax ; aloca quociente da div. em endereco de expArgsC\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else if (operator == tokenMod) {
                        if (!(expArgsD1.type == "Integer" && expArgsD2.type == "Integer")) {
                            throwIdentifierError("incompatible_types");
                            return;
                        }

                        try {
                            writer.write("\tmov eax, [M+" + expArgsC.addr
                                    + "] ; alocando valor em end. de expArgsC a registrador\n");
                            writer.write("\tmov ebx, [M+" + expArgsD2.addr
                                    + "] ; alocando valor em end. de expArgsD2 a registrador\n");
                            writer.write("\tidiv ebx ; eax divisao ebx\n");
                            expArgsC.addr = tempCounter;
                            updateTempCounter(expArgsC.type, 0);
                            writer.write("\tmov [M+" + expArgsC.addr
                                    + "], edx ; aloca resto da div. em endereco de expArgsC\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (operator == tokenAnd) {
                        if (!(expArgsD1.type == "Boolean" && expArgsD2.type == "Boolean")) {
                            throwIdentifierError("incompatible_types");
                            return;
                        }

                        try {
                            writer.write("\tmov eax, [M+" + expArgsC.addr
                                    + "] ; alocando valor em end. de expArgsC a registrador\n");
                            writer.write("\tmov ebx, [M+" + expArgsD2.addr
                                    + "] ; alocando valor em end. de expArgsD2 a registrador\n");
                            writer.write("\timul ebx ; eax AND ebx\n");
                            expArgsC.addr = tempCounter;
                            updateTempCounter(expArgsC.type, 0);
                            writer.write("\tmov [M+" + expArgsC.addr
                                    + "], edx:eax ; aloca resultado do AND em endereco de expArgsC\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        /* 
            Na gramática: EXP_D-> {!} EXP_E
        
            Metodo EXP_D -> Símbolo não terminal auxiliar 3 para expressoes.
            Pode iniciar com token !, e enquanto o proximo for igual a !, continua nesse loop. Depois chama EXP_E.
        */
        void EXP_D(EXP_args expArgsD) {
            if (!pauseCompiling) {
                boolean not = false;
                while (currentToken.token == tokenNot) {
                    if (!not)
                        not = true;
                    else
                        not = false;

                    checkToken(tokenNot);
                    if (pauseCompiling)
                        return;
                }

                EXP_args expArgsE = new EXP_args();
                EXP_E(expArgsE);
                if (pauseCompiling)
                    return;

                if (not && expArgsE.type != "Boolean") {
                    throwIdentifierError("incompatible_types");
                    return;
                }

                expArgsD.type = expArgsE.type;

                if (not) {
                    expArgsD.addr = tempCounter;
                    updateTempCounter(expArgsE.type, 0);
                    try {
                        writer.write("\tmov eax, [M+" + expArgsE.addr
                                + "] ; alocando valor em end. de expArgsE a registrador\n");
                        writer.write("\tneg eax ; negando conteudo de registrador\n");
                        writer.write("\tadd eax, 1 ; finalizando not logico\n");
                        writer.write("\tmov [M+" + expArgsD.addr
                                + "], eax ; atualizando valor em end. de expArgsD com negacao\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    expArgsD.addr = expArgsE.addr;
                }

            }
        }

        /* 
            Na gramática: EXP_E-> (int | float) "(" EXP_A ")" | EXP_F
        
            Metodo EXP_E -> Símbolo não terminal auxiliar 4 para expressoes.
            Caso inicia com token int ou float, precisa de token (, depois chama EXP_A e volta para verificar token ). 
            Caso contrario, chama EXP_F.
        */
        void EXP_E(EXP_args expArgsE) {
            if (!pauseCompiling) {
                if (currentToken.token == tokenInt) {
                    expArgsE.type = "Integer";

                    checkToken(tokenInt);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenPar) {
                        checkToken(tokenOpenPar);
                        if (pauseCompiling)
                            return;

                        EXP_args expArgsA = new EXP_args();
                        EXP_A(expArgsA);
                        if (pauseCompiling)
                            return;

                        if (expArgsA.type != "Integer" && expArgsA.type != "Float") {
                            throwIdentifierError("incompatible_types");
                            return;
                        }

                        if (currentToken.token == tokenClosePar) {
                            checkToken(tokenClosePar);
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenFloat) {
                    expArgsE.type = "Float";

                    checkToken(tokenFloat);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenPar) {
                        checkToken(tokenOpenPar);
                        if (pauseCompiling)
                            return;

                        EXP_args expArgsA = new EXP_args();
                        EXP_A(expArgsA);

                        if (expArgsA.type != "Integer" && expArgsA.type != "Float") {
                            throwIdentifierError("incompatible_types");
                            return;
                        }

                        if (currentToken.token == tokenClosePar) {
                            checkToken(tokenClosePar);
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else {
                    EXP_args expArgsF = new EXP_args();
                    EXP_F(expArgsF);
                    if (pauseCompiling)
                        return;

                    expArgsE.type = expArgsF.type;
                    expArgsE.addr = expArgsF.addr;
                }
            }
        }

        /* 
            Na gramática: EXP_F->  "(" EXP ")" | id ["[" EXP "]"] | num
        
            Metodo EXP_F -> Símbolo não terminal auxiliar 5 para expressoes.
            Caso inicia com token = (, executa EXP e fecha o parenteses com token = ).
            Caso inicia com token identificador, pode opcionalmente ter tambem "[" EXP "]".
            Por ultimo, pode ser tambem um valor. 
            Else, erro.
        */
        void EXP_F(EXP_args expArgsF) {
            if (!pauseCompiling) {
                if (currentToken.token == tokenOpenPar) {
                    checkToken(tokenOpenPar);
                    if (pauseCompiling)
                        return;

                    EXP_args expArgsA1 = new EXP_args();
                    EXP_A(expArgsA1);
                    if (pauseCompiling)
                        return;

                    expArgsF.type = expArgsA1.type;
                    expArgsF.addr = expArgsA1.addr;

                    if (currentToken.token == tokenClosePar) {
                        checkToken(tokenClosePar);
                        if (pauseCompiling)
                            return;
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenId) {
                    if (!identifierIsDeclared(currentToken)) {
                        throwIdentifierError("id_not_declared");
                        return;
                    }

                    Symbol currentSymbol = symbolTable.get(currentToken.lexeme);

                    expArgsF.type = currentSymbol.type;
                    expArgsF.addr = currentSymbol.addr;

                    checkToken(tokenId);
                    if (pauseCompiling)
                        return;

                    if (currentToken.token == tokenOpenSq) {
                        if (currentSymbol.type != "String") {
                            throwIdentifierError("incompatible_types");
                            return;
                        }

                        checkToken(tokenOpenSq);
                        if (pauseCompiling)
                            return;

                        EXP_args expArgsA2 = new EXP_args();
                        EXP_A(expArgsA2);

                        if (pauseCompiling)
                            return;

                        if (expArgsA2.type != "Integer") {
                            throwIdentifierError("incompatible_types");
                            return;
                        }

                        expArgsF.type = "Char";

                        if (currentToken.token == tokenCloseSq) {
                            checkToken(tokenCloseSq);
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    }
                } else if (currentToken.token == tokenValue) {
                    expArgsF.type = currentToken.type;

                    if (currentToken.type == "Float" || currentToken.type == "String") {
                        try {
                            writer.write("section .data\n");
                            if (currentToken.type == "Float") {
                                writer.write("\tdd " + currentToken.lexeme + " ; declarando valor na area de dados\n");
                            } else if (currentToken.type == "String") {
                                writer.write(
                                        "\tdb " + currentToken.lexeme + ", 0 ; declarando valor na area de dados\n");
                            }
                            writer.write("section .text\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        expArgsF.addr = posMem;
                        updatePosMem(currentToken.type, currentToken.lexeme.length());

                    } else {
                        expArgsF.addr = tempCounter;
                        updateTempCounter(currentToken.type, currentToken.lexeme.length());

                        if (currentToken.type == "Integer") {
                            try {
                                writer.write("\tmov eax, " + currentToken.lexeme + " ; imediato para registrador\n");
                                writer.write("\tmov [M+" + expArgsF.addr
                                        + "], eax ; alocando valor do registrador no endereco de expArgsF\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (currentToken.type == "Char") {
                            try {
                                writer.write("\tmov al, " + currentToken.lexeme + " ; imediato para registrador\n");
                                writer.write("\tmov [M+" + expArgsF.addr
                                        + "], al ; alocando valor do registrador no endereco de expArgsF\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    checkToken(tokenValue);
                    if (pauseCompiling)
                        return;
                } else
                    throwParserError();
            }
        }
    }

    static public String readAllCharsOneByOne(BufferedReader reader) throws IOException {
        StringBuilder content = new StringBuilder();

        int value;
        while ((value = reader.read()) != -1) {
            content.append((char) value);
        }
        content.append('#');

        return content.toString();
    }

    /* 
        Metodo main
        Inicia a tabela de simbolos e aloca as palavras reservadas com seus respectivos numeros de tokens.
        Inicia um scanner e le o arquivo fonte por linha, acrescentando \n no final de cada uma.
        Roda o analisador lexico uma vez para encontrar primeiro token.
        Roda o analisador sintatico, iniciando do estado inicial.
        Caso a compilacao obtenha sucesso, printa a mensagem de sucesso.
    */
    public static void main(String[] args) throws Exception {

        for (int i = 0; i < reservedWords.length; i++) {
            Symbol symbol = new Symbol(reservedWords[i], i + 1);
            symbolTable.put(reservedWords[i], symbol);
        }

        //BufferedReader br = new BufferedReader(new FileReader("io/testeclasse.in"));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        fileStr = readAllCharsOneByOne(br);

        lexer.getLexeme(fileStr);

        parser.START();

        if (!pauseCompiling && lineCount != 1)
            System.out.println(lineCount + " linhas compiladas.");
    }
}
