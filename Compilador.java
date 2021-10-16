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
// import java.io.FileReader;
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
                lexeme += "0" + c;
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
        String currentIdentifierLexeme = "";
        String currentIdentifierClass = "";
        String currentIdentifierType = "";

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

        /* 
            Metodo toSymbolTable -> Coloca identificador na tabela de simbolos.
        */
        void toSymbolTable(String classification, String type, String lexeme) {
            Symbol newSymbol = new Symbol(lexeme, tokenId);
            newSymbol.classification = classification;
            newSymbol.type = type;
            symbolTable.put(lexeme, newSymbol);
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

        class EXP_args {
            boolean isBoolean;
            boolean isInt;
            boolean isFloat;
            boolean isChar;
            boolean isString;

            boolean booleanResult;
            int intResult;
            float floatResult;
            String charResult;
            String stringResult;

            EXP_args() {
                isBoolean = false;
                isInt = false;
                isFloat = false;
                isChar = false;
                isString = false;
            }
        }

        /* 
            Na gramática: INÍCIO-> 	{DECL_A | COMANDO} eof
        
            Metodo START -> Símbolo não terminal inicial da gramática. 
            Aceita declaração ou comando até EOF ou erro.
        */
        void START() {
            while (currentToken.token != 667 && !pauseCompiling) {
                if (currentToken.token == tokenStr || currentToken.token == tokenConst || currentToken.token == tokenInt
                        || currentToken.token == tokenChar || currentToken.token == tokenFloat) {
                    DECL_A();
                } else
                    COMMAND();
            }

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
                if (currentToken.token == tokenStr) {
                    currentIdentifierClass = "var";
                    currentIdentifierType = "String";
                    checkToken(tokenStr);
                    if (pauseCompiling)
                        return;
                    DECL_B();
                    if (pauseCompiling)
                        return;
                    while (currentToken.token == tokenComma) {
                        checkToken(tokenComma);
                        if (pauseCompiling)
                            return;
                        DECL_B();
                        if (pauseCompiling)
                            return;
                    }
                } else if (currentToken.token == tokenConst) {
                    currentIdentifierClass = "const";
                    currentIdentifierType = "Const";
                    checkToken(tokenConst);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenId) {
                        if (identifierIsDeclared(currentToken)) {
                            throwIdentifierError("id_already_declared");
                            return;
                        }
                        currentIdentifierLexeme = currentToken.lexeme;
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
                            toSymbolTable(currentIdentifierClass, currentIdentifierType, currentIdentifierLexeme);
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else if (currentToken.token == tokenInt) {
                    currentIdentifierClass = "var";
                    currentIdentifierType = "Integer";
                    checkToken(tokenInt);
                    if (pauseCompiling)
                        return;
                    DECL_B();
                    if (pauseCompiling)
                        return;
                    while (currentToken.token == tokenComma) {
                        checkToken(tokenComma);
                        if (pauseCompiling)
                            return;
                        DECL_B();
                        if (pauseCompiling)
                            return;
                    }
                } else if (currentToken.token == tokenChar) {
                    currentIdentifierClass = "var";
                    currentIdentifierType = "Char";
                    checkToken(tokenChar);
                    if (pauseCompiling)
                        return;
                    DECL_B();
                    if (pauseCompiling)
                        return;
                    while (currentToken.token == tokenComma) {
                        checkToken(tokenComma);
                        if (pauseCompiling)
                            return;
                        DECL_B();
                        if (pauseCompiling)
                            return;
                    }
                } else if (currentToken.token == tokenFloat) {
                    currentIdentifierClass = "var";
                    currentIdentifierType = "Float";
                    checkToken(tokenFloat);
                    if (pauseCompiling)
                        return;
                    DECL_B();
                    if (pauseCompiling)
                        return;
                    while (currentToken.token == tokenComma) {
                        checkToken(tokenComma);
                        if (pauseCompiling)
                            return;
                        DECL_B();
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
        void DECL_B() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenId) {
                    if (identifierIsDeclared(currentToken)) {
                        throwIdentifierError("id_already_declared");
                        return;
                    }

                    currentIdentifierLexeme = currentToken.lexeme;
                    toSymbolTable(currentIdentifierClass, currentIdentifierType, currentIdentifierLexeme);

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

        /* 
            Na gramática: TIPO_DECL-> 	[-]num | string | hexa | caractere
        
            Metodo TIPO_DECL -> Símbolo não terminal para valores de variáveis de Declaração
            Lê opcionalmente um menos e depois é necessário um valor válido (num | string | hexa | caractere)
        */
        void DECL_TYPE() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenMinus) {
                    checkToken(tokenMinus);
                    if (pauseCompiling)
                        return;
                }
                if (currentToken.token == tokenValue) {
                    if (currentIdentifierType == "Const") {
                        currentIdentifierType = currentToken.type;
                    } else {
                        if ((currentIdentifierType == "Integer" && currentToken.type != "Integer")
                                || (currentIdentifierType == "Float"
                                        && (currentToken.type != "Float" && currentToken.type != "Integer"))
                                || (currentIdentifierType == "String" && currentToken.type != "String")
                                || (currentIdentifierType == "Char" && currentToken.type != "Char")) {
                            throwIdentifierError("incompatible_types");
                            return;
                        }
                    }
                    checkToken(tokenValue);
                    if (pauseCompiling)
                        return;
                } else
                    throwParserError();
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
                EXP_args expArgs = new EXP_args();

                if (currentToken.token == tokenId) {
                    if (!identifierIsDeclared(currentToken)) {
                        throwIdentifierError("id_not_declared");
                        return;
                    }

                    if (symbolTable.get(currentToken.lexeme).classification != "var") {
                        throwIdentifierError("id_incompatible_class");
                        return;
                    }

                    currentIdentifierType = symbolTable.get(currentToken.lexeme).type;

                    checkToken(tokenId);
                    if (pauseCompiling)
                        return;

                    if (currentToken.token == tokenOpenSq) {
                        if (currentIdentifierType != "String") {
                            throwIdentifierError("incompatible_types");
                            return;
                        }

                        checkToken(tokenOpenSq);
                        if (pauseCompiling)
                            return;

                        expArgs = new EXP_args();
                        EXP_A(expArgs);

                        if (pauseCompiling)
                            return;

                        if (!expArgs.isInt) {
                            throwIdentifierError("incompatible_types");
                            return;
                        }

                        if (currentToken.token == tokenCloseSq) {
                            checkToken(tokenCloseSq);
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();

                        if (currentToken.token == tokenAtrib) {
                            checkToken(tokenAtrib);
                            if (pauseCompiling)
                                return;

                            expArgs = new EXP_args();
                            EXP_A(expArgs);

                            if (pauseCompiling)
                                return;

                            if (!expArgs.isChar) {
                                throwIdentifierError("incompatible_types");
                                return;
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
                    } else if (currentToken.token == tokenAtrib) {
                        checkToken(tokenAtrib);
                        if (pauseCompiling)
                            return;

                        expArgs = new EXP_args();
                        EXP_A(expArgs);

                        if (pauseCompiling)
                            return;

                        if ((currentIdentifierType == "Integer" && !expArgs.isInt)
                                || (currentIdentifierType == "Float" && !expArgs.isFloat)
                                || (currentIdentifierType == "Char" && !expArgs.isChar)
                                || (currentIdentifierType == "String" && !expArgs.isString)) {
                            throwIdentifierError("incompatible_types");
                            return;
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

                    expArgs = new EXP_args();
                    EXP_A(expArgs);
                    if (pauseCompiling)
                        return;

                    if (!expArgs.isBoolean) {
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

                    expArgs = new EXP_args();
                    EXP_A(expArgs);
                    if (pauseCompiling)
                        return;

                    if (!expArgs.isBoolean) {
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
                EXP_args expArgs = new EXP_args();
                EXP_A(expArgs);
                if (pauseCompiling)
                    return;
                while (currentToken.token == tokenComma) {
                    checkToken(tokenComma);
                    if (pauseCompiling)
                        return;
                    EXP_A(expArgs);
                    if (pauseCompiling)
                        return;
                }
            }
        }

        /* 
            Na gramática: OPERADOR->   = | != | < | > | <= | >=
        
            Metodo OPERADOR -> Símbolo não terminal para operadores.
            Caso token = (= | != | < | > | <= | >=), continua
            Caso seja diferente, erro
        */
        void OPERATOR(EXP_args expArgs) {
            if (!pauseCompiling) {
                expArgs.isBoolean = true;
                expArgs.isInt = false;
                expArgs.isFloat = false;
                expArgs.isChar = false;
                expArgs.isString = false;

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

        /* 
            Na gramática: EXP-> EXP_B {OPERADOR EXP_B}
        
            Metodo EXP -> Símbolo não terminal para expressoes.
            Chama metodo EXP_B e pode rodar OPERADOR EXP_B opcionalmente, quantas vezes quiser.
        */
        void EXP_A(EXP_args expArgs) {
            if (!pauseCompiling) {
                EXP_B(expArgs);
                if (pauseCompiling)
                    return;
                while (currentToken.token == tokenEqual || currentToken.token == tokenDif
                        || currentToken.token == tokenLess || currentToken.token == tokenGtr
                        || currentToken.token == tokenLessEqual || currentToken.token == tokenGtrEqual) {
                    if (expArgs.isString && currentToken.token != tokenEqual) {
                        throwIdentifierError("incompatible_types");
                        return;
                    }
                    OPERATOR(expArgs);
                    if (pauseCompiling)
                        return;
                    EXP_B(expArgs);
                    if (pauseCompiling)
                        return;
                }
            }
        }

        /* 
            Na gramática: EXP_B-> [-] EXP_C { (+ | - | "||") EXP_C }
        
            Metodo EXP_B -> Símbolo não terminal auxiliar 1 para expressoes.
            Opcionalmente pode iniciar com token de menos. Chama EXP_C e pode opcionalmente rodar (+ | - | "||") EXP_C, quantas vezes quiser.
        */
        void EXP_B(EXP_args expArgs) {
            if (!pauseCompiling) {
                if (currentToken.token == tokenMinus) {
                    checkToken(tokenMinus);
                    if (pauseCompiling)
                        return;
                }
                EXP_C(expArgs);
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
                    EXP_C(expArgs);
                    if (pauseCompiling)
                        return;
                }
            }
        }

        /* 
            Na gramática: EXP_C-> EXP_D { ("*" | && | / | div | mod) EXP_D }
        
            Metodo EXP_C -> Símbolo não terminal auxiliar 2 para expressoes.
            Chama EXP_D e pode opcionalmente rodar ("*" | && | / | div | mod) EXP_D, quantas vezes quiser.
        */
        void EXP_C(EXP_args expArgs) {
            if (!pauseCompiling) {
                EXP_D(expArgs);
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
                    EXP_D(expArgs);
                    if (pauseCompiling)
                        return;
                }
            }
        }

        /* 
            Na gramática: EXP_D-> {!} EXP_E
        
            Metodo EXP_D -> Símbolo não terminal auxiliar 3 para expressoes.
            Pode iniciar com token !, e enquanto o proximo for igual a !, continua nesse loop. Depois chama EXP_E.
        */
        void EXP_D(EXP_args expArgs) {
            if (!pauseCompiling) {
                while (currentToken.token == tokenNot) {
                    checkToken(tokenNot);
                    if (pauseCompiling)
                        return;
                }
                EXP_E(expArgs);
                if (pauseCompiling)
                    return;
            }
        }

        /* 
            Na gramática: EXP_E-> (int | float) "(" EXP_A ")" | EXP_F
        
            Metodo EXP_E -> Símbolo não terminal auxiliar 4 para expressoes.
            Caso inicia com token int ou float, precisa de token (, depois chama EXP_A e volta para verificar token ). 
            Caso contrario, chama EXP_F.
        */
        void EXP_E(EXP_args expArgs) {
            if (!pauseCompiling) {
                if (currentToken.token == tokenInt) {
                    checkToken(tokenInt);
                    if (pauseCompiling)
                        return;
                    if (currentToken.token == tokenOpenPar) {
                        checkToken(tokenOpenPar);
                        if (pauseCompiling)
                            return;
                        EXP_A(expArgs);
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
                        EXP_A(expArgs);
                        if (currentToken.token == tokenClosePar) {
                            checkToken(tokenClosePar);
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    } else
                        throwParserError();
                } else {
                    EXP_F(expArgs);
                    if (pauseCompiling)
                        return;
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
        void EXP_F(EXP_args expArgs) {
            if (!pauseCompiling) {
                EXP_args expArgs1 = new EXP_args();

                if (currentToken.token == tokenOpenPar) {
                    checkToken(tokenOpenPar);
                    if (pauseCompiling)
                        return;

                    expArgs1 = new EXP_args();
                    EXP_A(expArgs1);
                    if (pauseCompiling)
                        return;

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

                    currentIdentifierType = symbolTable.get(currentToken.lexeme).type;

                    if (currentIdentifierType == "Integer")
                        expArgs.isInt = true;
                    else if (currentIdentifierType == "Float")
                        expArgs.isFloat = true;
                    else if (currentIdentifierType == "Char")
                        expArgs.isChar = true;
                    else if (currentIdentifierType == "String")
                        expArgs.isString = true;

                    checkToken(tokenId);
                    if (pauseCompiling)
                        return;

                    if (currentToken.token == tokenOpenSq) {
                        if (currentIdentifierType != "String") {
                            throwIdentifierError("incompatible_types");
                            return;
                        }

                        checkToken(tokenOpenSq);
                        if (pauseCompiling)
                            return;

                        expArgs1 = new EXP_args();
                        EXP_A(expArgs1);

                        if (pauseCompiling)
                            return;

                        if (!expArgs1.isInt) {
                            throwIdentifierError("incompatible_types");
                            return;
                        }

                        expArgs.isChar = true;
                        expArgs.isString = false;

                        if (currentToken.token == tokenCloseSq) {
                            checkToken(tokenCloseSq);
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    }
                } else if (currentToken.token == tokenValue) {
                    if (currentToken.type == "Integer") {
                        expArgs.isInt = true;
                        expArgs.intResult = Integer.parseInt(currentToken.lexeme);
                    } else if (currentToken.type == "Float") {
                        expArgs.isFloat = true;
                        expArgs.floatResult = Float.parseFloat(currentToken.lexeme);
                    } else if (currentToken.type == "Char") {
                        expArgs.isChar = true;
                        expArgs.charResult = currentToken.lexeme;
                    } else if (currentToken.type == "String") {
                        expArgs.isString = true;
                        expArgs.stringResult = currentToken.lexeme;
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

        // BufferedReader br = new BufferedReader(new FileReader("programa.in"));
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        fileStr = readAllCharsOneByOne(br);

        lexer.getLexeme(fileStr);

        parser.START();

        if (!pauseCompiling && lineCount != 1)
            System.out.println(lineCount + " linhas compiladas.");
    }
}