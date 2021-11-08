/* 
    *   Trabalho Prático - Compiladores 2021/2
    *   GRUPO 9
    *   Bernardo Cerqueira de Lima      586568
    *   Henrique Dornas Mendes          651252
    *   Matheus Teixeira Alves          636132
*/

import java.util.Hashtable;
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

    /*
        Classe dos Elementos da Tabela de Símbolos
        Atributos:  lexeme  -> lexema
                    addr    -> endereço na memória
                    classification -> classe (var ou const)
                    type    -> tipo 
    */
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

    /*
        Classe dos Tokens encontrados
        Atributos:  token   -> numero do tipo de token
                    lexeme  -> lexema
                    type    -> tipo 
    */
    static class Token {
        int token;
        String lexeme;
        String type;

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
        Classe do Analisador Sintático, Tradução e Geração de Código
    
        As regras de tradução estão especificadas no arquivo de documentação do trabalho.
    
        Variáveis locais:
            posMem -> endereço atual da memoria
            tempCounter -> contador da posição de temporários
            rotCounter -> contador de rótulos
            currentSection -> 0 : .data ;  1 : .text
            writer -> bufferedWriter para escrever no .asm
    
        Utiliza as seguintes variáveis globais:
            currentToken    -> Token atual no escopo global do programa
            pauseCompiling  -> Flag de erro global do Compilador
            token?          -> Tokens da linguagem
    
        ----- OBSERVAÇÃO -----
        Por convenção, EOF entra no analisador sintático como token 667.
        Na prática EOF não é token, mas nesta implementação utilizamos essa estratégia para identificar o tipo de erro sintático.
    
        O Parser segue a seguinte Gramática:
        (na implementação, simbolos não terminais foram traduzidos para INGLÊS para seguir a mesma convenção do restante do código)
    
            START-> 	{DECL_A | COMANDO} eof
    
            DECL_A-> 	(int | float | string | char) DECL_B1 {, DECL_B2} ;	|
    		            const id = [-] valor;
    
            DECL_B-> 	id [<- [-] valor ]
            
            COMANDO->	id ["[" EXP_A1 "]"] <- EXP_A2;		  |
                        while EXP_A3 TIPO_CMD				  |
                        if EXP_A4 TIPO_CMD [else TIPO_CMD]	  |
                        readln "(" id ")";				      |
                        (write | writeln) "(" LISTA_EXP ")";  |
                        ;
            
            TIPO_CMD->	COMANDO | "{" {COMANDO} "}"
            LISTA_EXP->	EXP_A1 {, EXP_A2}
            
            EXP_A-> 	EXP_B1 [ (= | != | < | > | <= | >= ) EXP_B2 ]
            EXP_B->		[-] EXP_C1 { (+ | - | "||") EXP_C2 }
            EXP_C->		EXP_D1 { ("*" | && | / | div | mod) EXP_D2 } 
            EXP_D->		{!} EXP_E 
            EXP_E->		(int | float ) "(" EXP_A ")" | EXP_F
            EXP_F->     "(" EXP_A1 ")" | id ["[" EXP_A2 "]"] | valor
    */
    static class Parser {
        static int posMem = 65536; // endereco atual da memoria
        static int tempCounter = 0;
        static int rotCounter = 0;
        static int currentSection = 0; // .data = 0 , .text = 1
        static BufferedWriter writer;
        static {
            try {
                writer = new BufferedWriter(new FileWriter("arq.asm"));
                writer.write("global _start ; Ponto inicial do programa\n");
                writer.write("_start: ; Inicio do programa\n");
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
            Método throwIdentifierError -> Acusa erros relacionados a identificadores e tipos.
            1- Se identificador nao foi declarado
            2- Se identificador esta sendo declarado de novo
            3- Se identificador possui classe incompativel
            4- Se tipo do identificador/expressao é incompativel
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
            Metodo updatePosMem(type, strSize)
                Atualiza endereco atual da memoria com base no tipo especificado.
                O parametro strSize é ignorado caso type != String
        */
        void updatePosMem(String type, int strSize) {
            if (type == "Char")
                posMem += 1;
            else if (type == "Integer" || type == "Float" || type == "Boolean")
                posMem += 4;
            else if (type == "String") {
                posMem += strSize;
            }
        }

        /*
            Metodo updateTempCounter(type, strSize)
                Atualiza endereco atual da memoria de Temporarios com base no tipo especificado.
                O parametro strSize é ignorado caso type != String
        */
        void updateTempCounter(String type, int strSize) {
            if (type == "Char")
                tempCounter += 1;
            else if (type == "Integer" || type == "Float" || type == "Boolean")
                tempCounter += 4;
            else if (type == "String") {
                tempCounter += strSize;
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

        /*
            Metodo declarationToMemory(symbol, hasValue, value)
                Para geração de código. Adiciona identificador na memória.
                Caso tenha valor, é utilizado db e dd com o valor especificado. 
                Caso nao, so reserva uma area com o tamanho certo.
        */
        void declarationToMemory(Symbol symbol, boolean hasValue, String value) throws IOException {
            if (hasValue) {
                if (symbol.type == "Char") {
                    writer.write("\tdb " + value + " ; char em M+" + posMem + "\n");
                    updatePosMem(symbol.type, 1);
                } else if (symbol.type == "Integer") {
                    writer.write("\tdd " + value + " ; inteiro em M+" + posMem + "\n");
                    updatePosMem(symbol.type, 4);
                } else if (symbol.type == "Float") {
                    String floatValue = treatFloat(value);
                    writer.write("\tdd " + floatValue + " ; float em M+" + posMem + "\n");
                    updatePosMem(symbol.type, 4);
                } else if (symbol.type == "String") {
                    if (symbol.classification == "const") {
                        writer.write("\tdb " + value + ", 0 ; string em M+" + posMem + "\n");
                        // Length -1 pq o java ta contando as aspas. -2 + 1 (zero no final) = -1
                        updatePosMem(symbol.type, value.length() - 1);
                    } else {
                        int r = 256 - (value.length() - 1);
                        writer.write("\tdb " + value + ", 0 ; string em M+" + posMem + "\n");
                        writer.write("\tresb " + r + " ; reservando espaco restante (str variavel)\n");
                        updatePosMem(symbol.type, 256);
                    }
                }
            } else {
                if (symbol.type == "Char") {
                    writer.write("\tresb 1 ; char em M+" + posMem + "\n");
                    updatePosMem(symbol.type, 1);
                } else if (symbol.type == "Integer") {
                    writer.write("\tresd 1 ; inteiro em M+" + posMem + "\n");
                    updatePosMem(symbol.type, 4);
                } else if (symbol.type == "Float") {
                    writer.write("\tresd 1 ; float em M+" + posMem + "\n");
                    updatePosMem(symbol.type, 4);
                } else if (symbol.type == "String") {
                    writer.write("\tresb 256 ; string em M+" + posMem + "\n");
                    updatePosMem(symbol.type, 256);
                }
            }
        }

        /*
            Metodo attributionToMemory(type, destAddr, sourceAddr)
                Para geração de código. Atribui valor a uma area de memoria com base no tipo
                destAddr -> endereco de destino
                sourceAddr -> endereco onde esta localizado o valor atualmente.
                Caso seja string, é atribuido char por char.
        */
        void attributionToMemory(String type, String destAddr, String sourceAddr) throws IOException {
            if (type == "Char") {
                writer.write("\tmov bl, [M+" + sourceAddr + "] ; alocando char em registrador\n");
                writer.write("\tmov [" + destAddr + "], bl ; adicionando valor a endereco do id\n");
            } else if (type == "Integer") {
                writer.write("\tmov eax, [M+" + sourceAddr + "] ; alocando inteiro em registrador\n");
                writer.write("\tmov [" + destAddr + "], eax ; adicionando valor a endereco do id\n");
            } else if (type == "Float") {
                writer.write("\tmovss xmm0, [M+" + sourceAddr + "] ; alocando float em registrador\n");
                writer.write("\tmovss [" + destAddr + "], xmm0 ; adicionando valor a endereco do id\n");
            } else if (type == "String") {
                String rotLoopStr = "Rot" + setRot();
                writer.write("\tmov rsi, " + destAddr + " ; passa o endereco da string A pra rax\n");
                writer.write("\tmov rdi, M+" + sourceAddr + " ; passa o endereco da string B pra rbx\n");
                writer.write(rotLoopStr + ": ; string loop \n");
                writer.write("\tmov al, [rdi] ; pega o caractere na posicao rdi+i da string B\n");
                writer.write("\tmov [rsi], al ; coloca o char na string A\n");
                writer.write("\tadd rsi, 1 ; incrementa o contador\n");
                writer.write("\tadd rdi, 1 ; incrementa o contador\n");
                writer.write("\tcmp al, 0 ; fim da strB?\n");
                writer.write("\tjne " + rotLoopStr + "; se nao, continua loop\n");
                writer.write("\t; se sim, fim da atribuicao de strB a strA. \n");

            }
        }

        /*
            Metodo setRot()
                Gera novo numero para Rotulos.
        */
        int setRot() {
            int current_rot = rotCounter;
            rotCounter = rotCounter + 1;

            return current_rot;
        }

        /*
            Metodo convertIntegerToString(expArgs)
                Converte um Inteiro para String, para posteriormente ser Impresso na tela com Write.
                Código igual o implementado pelo professor.
        */
        void convertIntegerToString(EXP_args expArgs) {
            int rot_a = setRot();
            int rot_b = setRot();
            int rot_c = setRot();

            try {
                writer.write("\tmov eax, [M+" + expArgs.addr + "] ; inteiro a ser convertido\n");
                writer.write("\tmov rsi, M+" + tempCounter + "; end. string ou temp.\n");
                writer.write("\tmov rcx, 0 ; contador pilha\n");
                writer.write("\tmov rdx, 0 ; tam. string convertido\n");
                writer.write("\tpush rdx ; \n");
                writer.write("\tcmp eax, 0 ; verifica sinal\n");
                writer.write("\tjge Rot" + rot_a + " ; salta se numero positivo\n");
                writer.write("\tmov bl, '-' ; senao, escreve sinal –\n");
                writer.write("\tmov [rsi], bl ; \n");
                writer.write("\tpop rdx ; \n");
                writer.write("\tmov rdx, 1 ; \n");
                writer.write("\tpush rdx ; \n");
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
                writer.write("\tmov rdx,rcx ; atualiza tam. string\n\n");
                writer.write("\t; desempilha os valores e escreve o string\n\n");
                writer.write("Rot" + rot_c + ":\n");
                writer.write("\tpop ax ; desempilha valor\n");
                writer.write("\tadd al, '0' ; transforma em caractere\n");
                writer.write("\tmov [rsi], al ; escreve caractere\n");
                writer.write("\tadd rsi, 1 ; incrementa base\n");
                writer.write("\tsub rcx, 1 ; decrementa contador\n");
                writer.write("\tcmp rcx, 0 ; verifica pilha vazia\n");
                writer.write("\tjne Rot" + rot_c + " ; se nao pilha vazia, loop\n\n");
                writer.write("\tpop rcx ; \n");
                writer.write("\tadd rdx, rcx ; \n");
                writer.write("\tmov rsi, M+" + tempCounter + "; passando pro rsi o endereço inicial da string \n");
                writer.write("\t; executa interrupcao de saida\n");
                writer.write("\t\n");
                updateTempCounter(expArgs.type, 0);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /*
           Metodo convertFloatToString(expArgs)
               Converte um Float para String, para posteriormente ser Impresso na tela com Write.
               Código igual o implementado pelo professor.
        */
        void convertFloatToString(EXP_args expArgs) {
            int rot_a = setRot();
            int rot_b = setRot();
            int rot_c = setRot();
            int rot_d = setRot();
            int rot_e = setRot();

            try {
                writer.write("\tmovss xmm0, [M+" + expArgs.addr + "] ; real a ser convertido\n");
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
                writer.write("\tjne Rot" + rot_b + " ; se nao eh 0, continua, else rot_b\n\n");
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

        /*
           Metodo translationWrite(expArgs)
               Geração de código para Impressão Write. (Igual a implementação do professor)
               Caso exp seja Int ou Float, converte para String antes.
        */
        void translationWrite(EXP_args expArgs) {
            int rot = setRot();
            try {
                if (expArgs.type == "Integer") {
                    convertIntegerToString(expArgs);
                } else if (expArgs.type == "Float") {
                    convertFloatToString(expArgs);
                } else if (expArgs.type == "String") {
                    writer.write("\tmov rsi, M+" + (expArgs.addr) + " ; registrador recebe endereco da string\n");
                    writer.write("\tmov rdx, rsi ; rdx = rsi\n");
                    writer.write("Rot" + rot + ": \n");
                    writer.write("\tmov al, [rdx] ; registrador recebe primeiro caractere da string \n");
                    writer.write("\tadd rdx, 1 ; incrementa rdx\n");
                    writer.write("\tcmp al, 0 ; al == 0 ? se True, fim da string\n");
                    writer.write("\tjne Rot" + rot + "\n");
                    writer.write("\tsub rdx, M+ " + (expArgs.addr) + " ; removendo offset (byte 0) do endereco\n");
                } else if (expArgs.type == "Char") {
                    writer.write("\tmov rsi, M+" + (expArgs.addr) + " ; registrador recebe endereco da string\n");
                    writer.write("\tmov rdx, 1 ; tamanho 1 pra char\n");
                }

                writer.write("\tmov rax, 1 ; chamada para saida\n");
                writer.write("\tmov rdi, 1 ; saida para tela\n");
                writer.write("\tsyscall\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /*
           Metodo translationWriteLn(expArgs)
               Geração de código para Impressão WriteLn. (Igual a implementação do professor)
               Nesse caso, so insere o linebreak no codigo (roda depois do translationWrite)
        */
        void translationWriteln() {
            try {
                Integer linebreak = tempCounter;
                updateTempCounter("String", 1);
                writer.write("\tmov dl, 10 ; passa linebreak pro dl \n");
                writer.write("\tmov [M+" + linebreak + "], dl ; passa o linebreak para dl\n");
                writer.write("\tmov rax, 1 ; chamada para saida\n");
                writer.write("\tmov rdi, 1 ; saida para tela\n");
                writer.write("\tmov rsi, M+" + linebreak + " ; saida para tela\n");
                writer.write("\tmov rdx, 1\n");
                writer.write("\tsyscall\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        /*
           Metodo translationReadLn()
               Geração de código para Leitura Read. (Igual a implementação do professor)
        */
        void translationReadln() {
            int rotFimStr = setRot();
            int rotBufStr = setRot();
            int rot_a = setRot();
            int rot_b = setRot();
            int rot_c = setRot();
            int rot_d = setRot();
            int rot_e = setRot();
            int rot_f = setRot();
            int rot_g = setRot();
            int rot_h = setRot();

            Symbol currentSymbol = symbolTable.get(currentToken.lexeme);

            try {
                Integer buffer_read = 0;

                if (currentSymbol.type == "String") {
                    buffer_read = currentSymbol.addr;
                } else if (currentSymbol.type == "Integer") {
                    buffer_read = tempCounter;
                    updateTempCounter("String", 11);
                } else if (currentSymbol.type == "Float") {
                    buffer_read = tempCounter;
                    updateTempCounter("String", 14);
                }

                writer.write("\tmov rsi, M+" + buffer_read + "\n");

                writer.write("\tmov  rdx, 100h  ;tamanho do buffer \n");
                writer.write("\tmov  rax, 0 ;chamada para leitura \n");
                writer.write("\tmov  rdi, 0 ;leitura do teclado \n");
                writer.write("\tsyscall \n\n");

                if (currentSymbol.type == "String") {
                    writer.write("\tadd rax, M+" + (buffer_read - 1) + " ; endereço do ultimo caractere lido\n");
                    writer.write("\tmov rbx, rax ; armazena o endereço em rbx\n");
                    writer.write("\tmov al, [rbx] ; passa o ultimo caractere lido para al\n");
                    writer.write("\tcmp al, 10 ; verifica se o ultimo char era linebreak\n");
                    writer.write("\tje Rot" + rotFimStr + " ; verifica se ainda ha chars no buffer - String\n");
                    writer.write("Rot" + rotBufStr + ":   ; rot de ler buffer de readln(String)\n");
                    writer.write("\tmov rax, 0 ; chama a leitura\n");
                    writer.write("\tmov rdi, 0 ; leitor da entrada\n");
                    writer.write("\tmov rsi, M+" + buffer_read + "\n");
                    writer.write("\tmov rdx, 1 ; le 1 byte e passa pro rdx\n");
                    writer.write("\tsyscall\n");
                    writer.write("\tmov al, [M+" + buffer_read + "] ; carrega o caractere lido em al\n");
                    writer.write("\tcmp al, 10 ; verifica se o char eh linebreak\n");
                    writer.write("\tjne Rot" + rotBufStr + " ; continua o loop se existem chars no buffer\n");
                    writer.write("Rot" + rotFimStr + ":   ; rot de fim de readln(String)\n");
                    writer.write("\tmov al, 0 ; carrega o caractere final de string no al\n");
                    writer.write("\tmov [rbx], al ; carrega o caractere no endereço de rbx\n");
                }

                if (currentSymbol.type == "Integer") {

                    writer.write("\tmov eax, 0     ;acumulador\n");
                    writer.write("\tmov ebx, 0     ;caractere \n");
                    writer.write("\tmov ecx, 10    ;base 10 \n");
                    writer.write("\tmov dx, 1      ;sinal \n");
                    writer.write("\tmov rsi, M+" + buffer_read + "       ;end. buffer \n");
                    writer.write("\tmov bl, [rsi]     ;carrega caractere \n");
                    writer.write("\tcmp bl, '-'       ;sinal - ? \n");
                    writer.write("\tjne Rot" + rot_a + "       ;se dif -, salta \n");
                    writer.write("\tmov dx, -1              ;senão, armazena - \n");
                    writer.write("\tadd rsi, 1           ;inc. ponteiro string \n");
                    writer.write("\tmov bl, [rsi]     ;carrega caractere \n\n");

                    writer.write("Rot" + rot_a + ": \n");
                    writer.write("\tpush dx      ;empilha sinal \n");
                    writer.write("\tmov  edx, 0     ;reg. multiplicação \n\n");

                    writer.write("Rot" + rot_b + ": \n");
                    writer.write("\tcmp  bl, 0Ah    ;verifica fim string \n");
                    writer.write("\tje Rot" + rot_c + "      ;salta se fim string \n");
                    writer.write("\timul ecx     ;mult. eax por 10 \n");
                    writer.write("\tsub bl, '0'    ;converte caractere \n");
                    writer.write("\tadd eax, ebx    ;soma valor caractere \n");
                    writer.write("\tadd rsi, 1     ;incrementa base \n");
                    writer.write("\tmov bl, [rsi]     ;carrega caractere \n");
                    writer.write("\tjmp Rot" + rot_b + "     ;loop \n\n");

                    writer.write("Rot" + rot_c + ": \n");
                    writer.write("\tpop cx      ;desempilha sinal \n");
                    writer.write("\tcmp cx, 0 \n");
                    writer.write("\tjg Rot" + rot_d + " \n");
                    writer.write("\tneg eax      ;mult. sinal \n\n");

                    writer.write("\tRot" + rot_d + ": \n\n");
                    writer.write("\tmov [M+" + currentSymbol.addr + "], eax ; move pro endereço do simbolo\n");
                }

                if (currentSymbol.type == "Float") {

                    writer.write("\tmov rax, 0     ;acumul. parte int. \n");
                    writer.write("\tsubss xmm0,xmm0      ;acumul. parte frac. \n");
                    writer.write("\tmov rbx, 0              ;caractere  \n");
                    writer.write("\tmov rcx, 10               ;base 10 \n");
                    writer.write("\tcvtsi2ss xmm3,rcx    ;base 10 \n");
                    writer.write("\tmovss xmm2,xmm3   ;potência de 10 \n");
                    writer.write("\tmov rdx, 1              ;sinal \n");
                    writer.write("\tmov rsi, M+" + buffer_read + "      ;end. buffer \n");
                    writer.write("\tmov bl, [rsi]     ;carrega caractere \n");
                    writer.write("\tcmp bl, '-'       ;sinal - ? \n");
                    writer.write("\tjne Rot" + rot_e + "        ;se dif -, salta \n");
                    writer.write("\tmov rdx, -1               ;senão, armazena - \n");
                    writer.write("\tadd rsi, 1           ;inc. ponteiro string \n");
                    writer.write("\tmov bl, [rsi]     ;carrega caractere \n\n");

                    writer.write("Rot" + rot_e + ": \n");
                    writer.write("\tpush rdx     ;empilha sinal \n");
                    writer.write("\tmov  rdx, 0     ;reg. multiplicação \n\n");

                    writer.write("Rot" + rot_f + ": \n");
                    writer.write("\tcmp  bl, 0Ah    ;verifica fim string \n");
                    writer.write("\tje Rot" + rot_g + "      ;salta se fim string \n");
                    writer.write("\tcmp  bl, '.'    ;senão verifica ponto \n");
                    writer.write("\tje Rot" + rot_h + "      ;salta se ponto \n");
                    writer.write("\timul ecx     ;mult. eax por 10 \n");
                    writer.write("\tsub bl, '0'    ;converte caractere \n");
                    writer.write("\tadd eax, ebx    ;soma valor caractere \n");
                    writer.write("\tadd rsi, 1     ;incrementa base \n");
                    writer.write("\tmov bl, [rsi]     ;carrega caractere \n");
                    writer.write("\tjmp Rot" + rot_f + "    ;loop \n\n");

                    writer.write("Rot" + rot_h + ": \n");
                    writer.write("\t  ;calcula parte fracionária em xmm0 \n\n");

                    writer.write("\tadd rsi, 1           ;inc. ponteiro string \n");
                    writer.write("\tmov bl, [rsi]     ;carrega caractere \n");
                    writer.write("\tcmp  bl, 0Ah    ;*verifica fim string \n");
                    writer.write("\tje Rot" + rot_g + "     ;salta se fim string \n");
                    writer.write("\tsub bl, '0'    ;converte caractere \n");
                    writer.write("\tcvtsi2ss xmm1,rbx    ;conv real \n");
                    writer.write("\tdivss xmm1,xmm2   ;transf. casa decimal \n");
                    writer.write("\taddss xmm0,xmm1   ;soma acumul. \n");
                    writer.write("\tmulss xmm2,xmm3   ;atualiza potência \n");
                    writer.write("\tjmp Rot" + rot_h + "     ;loop \n\n");

                    writer.write("Rot" + rot_g + ": \n");
                    writer.write("\tcvtsi2ss xmm1,rax    ;conv parte inteira \n");
                    writer.write("\taddss xmm0,xmm1   ;soma parte frac. \n");
                    writer.write("\tpop rcx      ;desempilha sinal \n");
                    writer.write("\tcvtsi2ss xmm1,rcx    ;conv sinal \n");
                    writer.write("\tmulss xmm0,xmm1   ;mult. sinal \n\n");

                    writer.write("\tmovss [M+" + currentSymbol.addr + "], xmm0 \n\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Trata inserção de floats que começam com "."
        String treatFloat(String value) {
            String firstChar = String.valueOf(value.charAt(0));
            if (firstChar.equals(".")) {
                value = "0" + value;
            } else if (firstChar.equals("-")) {
                StringBuilder str = new StringBuilder(value);
                str.insert(1, "0");
                value = str.toString();
            }

            return value;
        }

        /*
            Classe EXP_args
                Como em java nao existe passagem por parametro e referencia, foi criada essa classe para os parametros de expressoes.
                Em toda chamada de expressão, uma nova instância é iniciada antes e passada no parâmetro.
                Atributos:  type -> tipo da expressao
                            addr -> endereço de memória da expressão
        */
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
            Na gramática: 
            DECL_A-> 	(int | float | string | char) (45) DECL_B1 {, (46) DECL_B2} ;	|
        	            const id (38) = [- (31)] valor (39) (40) (41);
        
            Metodo DECL_A -> Símbolo não terminal de Declaração da gramática. 
            Caso inicio com (int | float | string | char), vai para DECL_B e pode rodar ,DECL_B 0 ou + vezes depois.
            Caso inicio com const, proximos tokens são um identificador, token de igual, menos opcional e um valor.
        
            Regras de Semântica:
            (31): {minus = True}
            (38): {if id.simbolo.classe != null then ERRO_id_declarado}
            (39): {if (minus & !(valor.tipo = integer | valor.tipo = float)) then ERRO}
            (40): {id.simbolo.tipo := valor.tipo_constante}
            (41): {id.simbolo.classe := constante}
            (45): {DECL_B1.tipo := idType}
            (46): {DECL_B2.tipo := idType}
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

                                String value;
                                if (minus)
                                    value = "-" + currentToken.lexeme;
                                else
                                    value = currentToken.lexeme;

                                try {
                                    declarationToMemory(currentSymbol, true, value);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

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
            Na gramática: 
            DECL_B-> 	id (38) [<- [- (31)] valor (39) (42) ] (43)
        
            Metodo DECL_B -> Símbolo não terminal auxiliar para Declaração
            Le token identificador e opcionalmente pode ter uma atribuição com valor.
        
            Regras de Semântica:
            (31): {minus = True}
            (38): {if id.simbolo.classe != null then ERRO_id_declarado}
            (39): {if (minus & !(valor.tipo = integer | valor.tipo = float)) then ERRO}
            (42): {id.simbolo.classe := variavel}
            (43): {id.simbolo.tipo := DECL_B.tipo}
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
                            if (pauseCompiling)
                                return;
                            minus = true;
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
                            if (minus)
                                value = "-" + currentToken.lexeme;
                            else
                                value = currentToken.lexeme;

                            if (idType == "Float" && currentToken.type == "Integer")
                                value += ".0";

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
                }
            }
        }

        /* 
            Na gramática:
            COMANDO->	id (2) (14) ["[" (15) EXP_A1 (44) "]"] <- EXP_A2 (16);  |
                        while EXP_A3 (17) TIPO_CMD                              |
                        if EXP_A4 (18) TIPO_CMD [else TIPO_CMD]                 |
                        readln "(" id (2) (47)")";                              |
                        (write | writeln) "(" LISTA_EXP ")";                    |
                        ;
        
            Metodo COMANDO -> Símbolo não terminal para Comandos da linguagem
        
            Execução:
            1. Caso leia um token identificador, opcionalmente podera ler [EXP_A]. Em seguida, sera necessario um token de atribuicao, vai chamar EXP_A e finalmente um token de ponto e virgula.
            2. Caso leia um token while, sera chamado EXP_A e depois TIPO_CMD.
            3. Caso leia um token if, sera chamado EXP_A e depois TIPO_CMD. Opcionalmente pode-se ter um token else seguido de uma chamada TIPO_CMD.
            4. Caso leia um token readln, deverao aparecer os tokens (identificador), seguido de token ponto e virgula.
            5. Caso leia token write ou writeln, sera chamado LISTA_EXP dentro de tokens ( e ), seguido de token ponto e virgula.
            6. Caso leia token ponto e virgula, so chama o CasaToken mesmo.
            7. Caso contrario, erro.
        
            Regras de Semântica:
            (2): {if id.simbolo.classe = null then ERRO}
            (14): {if id.simbolo.classe != variavel then ERRO}
            (15): {isStringIndex = true}
            (16): {if ((isStringIndex & EXP_A2.tipo != char) | 
                        (!isStringIndex & (!(EXP_A2.tipo == "Integer" & id.simbolo.tipo == "Float") & (EXP_A2.tipo != id.simbolo.tipo)))) 
                        then ERRO}
            (17): {if EXP_A3.tipo != bool then ERRO}
            (18): {if EXP_A4.tipo != bool then ERRO}
            (44): {if (EXP_A1.tipo != integer | id.simbolo.tipo != string) then ERRO}
            (47): {if id.simbolo.classe != "var" then ERRO}
        */
        void COMMAND() {
            if (!pauseCompiling) {
                if (currentToken.token == tokenId) {
                    EXP_args expArgsA1 = new EXP_args();
                    boolean isStringIndex = false;
                    String destAddr = ""; // endereço destino da atribuição
                    String atribType = ""; // tipo da atribuição

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

                        checkToken(tokenOpenSq);
                        if (pauseCompiling)
                            return;

                        tempCounter = 0;
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

                        isStringIndex = true;
                        atribType = "Char";
                    }
                    if (currentToken.token == tokenAtrib) {
                        checkToken(tokenAtrib);
                        if (pauseCompiling)
                            return;

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

                        /* 
                            Caso não seja acesso a posição de um String, o endereço destino é o proprio endereço do id, assim como seu tipo.
                            Caso contario, aloca em registrador rax o endereço do indice + posicao inicial do string. Com isso, rax é o endereço destino.
                        */
                        if (!isStringIndex) {
                            destAddr = "M+" + String.valueOf(currentSymbol.addr);
                            atribType = currentSymbol.type;
                        } else {
                            try {
                                writer.write("\tmov rax, 0 ; zerando o rax \n");
                                writer.write("\tmov eax, [M+" + expArgsA1.addr
                                        + "] ; alocando valor em end. de expArgsA1 a registrador (indice)\n");
                                writer.write("\tadd rax, M+" + currentSymbol.addr
                                        + " ; indice + posicao inicial do string\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            destAddr = "rax";
                        }

                        /* 
                            Caso o id seja float e a atribuição seja int, converte o int em float.
                        */
                        if (currentSymbol.type == "Float" && expArgsA2.type == "Integer") {
                            try {
                                writer.write("\tmov eax, [M+" + expArgsA2.addr
                                        + "] ; alocando valor em end. de expArgsA2 a registrador\n");
                                writer.write("\tcvtsi2ss xmm0, eax ; int32 para float\n");
                                writer.write("\tmovss [M+" + expArgsA2.addr
                                        + "], xmm0 ; alocando valor de xmm0 a end. de expArgsA2\n");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        // Executa atribuição
                        try {
                            attributionToMemory(atribType, destAddr, String.valueOf(expArgsA2.addr));
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
                    if (pauseCompiling)
                        return;

                    /* 
                        Geração de código do While. Basicamente compara resultado da expressão e realiza o jump com base no resultado.
                    */
                    String rotBegin = "Rot" + setRot();
                    String rotEnd = "Rot" + setRot();

                    try {
                        writer.write("\t" + rotBegin + ": ; RotInicio\n");
                        EXP_A(expArgsA3);
                        if (expArgsA3.type != "Boolean") {
                            throwIdentifierError("incompatible_types");
                            return;
                        }
                        writer.write("\tmov eax, [M+" + expArgsA3.addr
                                + "] ; alocando valor em end. de expArgsA3 a registrador\n");
                        writer.write("\tcmp eax, 1 ; verifica se expressao eh verdadeira\n");
                        writer.write("\tjne " + rotEnd + " ; caso false, desvia para RotFim\n");

                        CMD_TYPE();
                        if (pauseCompiling)
                            return;

                        writer.write("\tjmp " + rotBegin + " ; desvia para RotInicio\n");
                        writer.write(rotEnd + ": ; RotFim\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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

                    /* 
                        Geração de Código do IF ELSE.
                        Basicamente compara resultado da expressão e realiza o jump com base no resultado.
                    */
                    String rotFalse = "Rot" + setRot();
                    String rotEnd = "Rot" + setRot();

                    try {
                        writer.write("\tmov eax, [M+" + expArgsA4.addr
                                + "] ; alocando valor em end. de expArgsA4 a registrador\n");
                        writer.write("\tcmp eax, 1 ; verifica se expressao eh verdadeira\n");
                        writer.write("\tjne " + rotFalse + " ; caso falso, desvia para RotFalso\n");

                        CMD_TYPE();
                        if (pauseCompiling)
                            return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (currentToken.token == tokenElse) {
                        checkToken(tokenElse);
                        if (pauseCompiling)
                            return;

                        try {
                            writer.write("\tjmp " + rotEnd + " ; desvio para RotFim\n");
                            writer.write(rotFalse + ": ; RotFalso\n");

                            CMD_TYPE();
                            if (pauseCompiling)
                                return;

                            writer.write(rotEnd + ": ; RotFim\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    } else {
                        try {
                            writer.write(rotFalse + ": ; RotFalso else\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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

                            // Chamada para a leitura.
                            translationReadln();

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
            Na gramática: 
            TIPO_CMD->	COMANDO | "{" {COMANDO} "}"
        
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
            Na gramática:
            LISTA_EXP->	EXP_A1 (48) {, EXP_A2 (49)}
        
            Metodo LISTA_EXP -> Símbolo não terminal para lista de expressao.
            Pode rodar uma ou mais expressoes com uso de virgula.
        
            Regras de Semântica:
            (48): {if EXP_A1.tipo = **tipo_proibido** then ERRO} //verificar se existe algum tipo proibido de printar
            (49): {if EXP_A2.tipo = **tipo_proibido** then ERRO} //verificar se existe algum tipo proibido de printar
        */
        void EXP_LIST() {
            if (!pauseCompiling) {
                tempCounter = 0;
                EXP_args expArgsA1 = new EXP_args();
                EXP_A(expArgsA1);

                if (pauseCompiling)
                    return;

                // Nao pode imprimir Boolean.
                if (expArgsA1.type == "Boolean") {
                    throwIdentifierError("incompatible_types");
                    return;
                }

                // Chamada para Write 
                translationWrite(expArgsA1);

                while (currentToken.token == tokenComma) {
                    checkToken(tokenComma);
                    if (pauseCompiling)
                        return;

                    tempCounter = 0;
                    EXP_args expArgsA2 = new EXP_args();
                    EXP_A(expArgsA2);
                    if (pauseCompiling)
                        return;

                    // Nao pode imprimir Boolean.
                    if (expArgsA2.type == "Boolean") {
                        throwIdentifierError("incompatible_types");
                        return;
                    }

                    // Chamada para Write 
                    translationWrite(expArgsA2);
                }
            }
        }

        /* 
            Na gramática: 
            EXP_A-> EXP_B1 (32) [(33) (= (36)| != (37) | < (37)| > (37)| <= (37)| >= (37)) EXP_B2 (34) (35)]
        
            Metodo EXP_A -> Símbolo não terminal para expressoes.
            Chama metodo EXP_B e pode rodar OPERADOR EXP_B opcionalmente, quantas vezes quiser.
        
            Regras de Semântica:
            (32): {EXP_A.tipo := EXP_B1.tipo} 
            (33): {OPERADOR.tipo := EXP_B1.tipo}
            (34): {if !((EXP_B1.tipo = string & EXP_B2.tipo = string) |
                    (EXP_B1.tipo = char & EXP_B2.tipo = char) |
                    (EXP_B1.tipo = integer & EXP_B2.tipo = integer) |
                    (EXP_B1.tipo = float & EXP_B2.tipo = integer) |
                    (EXP_B1.tipo = integer & EXP_B2.tipo = float) |
                    (EXP_B1.tipo = float & EXP_B2.tipo = float)) then ERRO}
            (35): {EXP_A.tipo := bool}
            (36): {if OPERADOR.tipo != (string|int|float|char) then ERRO}
            (37): {if OPERADOR.tipo != (int|float|char) then ERRO}
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

                    int tokenOperator = -1;
                    if (currentToken.token == tokenEqual) {
                        if (expArgsB1.type != "String" && expArgsB1.type != "Integer" && expArgsB1.type != "Float"
                                && expArgsB1.type != "Char") {
                            throwIdentifierError("incompatible_types");
                            return;
                        }
                        tokenOperator = tokenEqual;
                        checkToken(tokenEqual);
                        if (pauseCompiling)
                            return;
                    } else if (currentToken.token == tokenDif) {
                        if (expArgsB1.type != "Integer" && expArgsB1.type != "Float" && expArgsB1.type != "Char") {
                            throwIdentifierError("incompatible_types");
                            return;
                        }
                        tokenOperator = tokenDif;
                        checkToken(tokenDif);
                        if (pauseCompiling)
                            return;
                    } else if (currentToken.token == tokenLess) {
                        if (expArgsB1.type != "Integer" && expArgsB1.type != "Float" && expArgsB1.type != "Char") {
                            throwIdentifierError("incompatible_types");
                            return;
                        }
                        tokenOperator = tokenLess;
                        checkToken(tokenLess);
                        if (pauseCompiling)
                            return;
                    } else if (currentToken.token == tokenGtr) {
                        if (expArgsB1.type != "Integer" && expArgsB1.type != "Float" && expArgsB1.type != "Char") {
                            throwIdentifierError("incompatible_types");
                            return;
                        }
                        tokenOperator = tokenGtr;
                        checkToken(tokenGtr);
                        if (pauseCompiling)
                            return;
                    } else if (currentToken.token == tokenLessEqual) {
                        if (expArgsB1.type != "Integer" && expArgsB1.type != "Float" && expArgsB1.type != "Char") {
                            throwIdentifierError("incompatible_types");
                            return;
                        }
                        tokenOperator = tokenLessEqual;
                        checkToken(tokenLessEqual);
                        if (pauseCompiling)
                            return;
                    } else if (currentToken.token == tokenGtrEqual) {
                        if (expArgsB1.type != "Integer" && expArgsB1.type != "Float" && expArgsB1.type != "Char") {
                            throwIdentifierError("incompatible_types");
                            return;
                        }
                        tokenOperator = tokenGtrEqual;
                        checkToken(tokenGtrEqual);
                        if (pauseCompiling)
                            return;
                    } else
                        throwParserError();

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

                    String rotTrue = "";
                    String rotFalse = "";

                    /* 
                        Geração de código das operações de comparação
                            Caso as duas expressões sejam Int, so aloca em registradores e realiza as comparacoes de inteiros com base no Operador.
                            Caso uma ou duas delas seja float, verifica necessidade de conversão de tipo e aloca para registradores xmm e realiza comparacoes de reais com base no Operador.
                            Caso seja comparação de Strings, isso é feito char a char.
                    */
                    if (expArgsB1.type == "Integer" && expArgsB2.type == "Integer") {
                        try {
                            writer.write("\tmov eax, [M+" + expArgsA.addr
                                    + "] ; alocando valor em end. de expArgsA a registrador\n");
                            writer.write("\tmov ebx, [M+" + expArgsB2.addr
                                    + "] ; alocando valor em end. de expArgsB2 a registrador\n");
                            writer.write("\tcmp eax, ebx ; comparando eax com ebx\n");

                            rotTrue = "Rot" + setRot();

                            if (tokenOperator == tokenEqual) {
                                writer.write("\tje " + rotTrue + " ; caso iguais, jmp para RotVerdadeiro\n");
                            } else if (tokenOperator == tokenDif) {
                                writer.write("\tjne " + rotTrue + " ; caso diferentes, jmp para RotVerdadeiro\n");
                            } else if (tokenOperator == tokenLess) {
                                writer.write("\tjl " + rotTrue + " ; caso menor, jmp para RotVerdadeiro\n");
                            } else if (tokenOperator == tokenGtr) {
                                writer.write("\tjg " + rotTrue + " ; caso maior, jmp para RotVerdadeiro\n");
                            } else if (tokenOperator == tokenLessEqual) {
                                writer.write("\tjle " + rotTrue + " ; caso menor ou igual, jmp para RotVerdadeiro\n");
                            } else if (tokenOperator == tokenGtrEqual) {
                                writer.write("\tjge " + rotTrue + " ; caso maior ou igual, jmp para RotVerdadeiro\n");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (expArgsB1.type == "Float" || expArgsB2.type == "Float") {
                        try {
                            if (expArgsA.type == "Float") {
                                writer.write("\tmovss xmm0, [M+" + expArgsA.addr
                                        + "] ; alocando valor em end. de expArgsA para registrador\n");
                            } else {
                                writer.write("\tmov eax, [M+" + expArgsA.addr
                                        + "] ; alocando valor em end. de expArgsA para registrador\n");
                                writer.write("\tcvtsi2ss xmm0, eax ; int32 para float\n");
                            }

                            if (expArgsB2.type == "Float") {
                                writer.write("\tmovss xmm1, [M+" + expArgsB2.addr
                                        + "] ; alocando valor em end. de expArgsB2 para registrador\n");

                            } else {
                                writer.write("\tmov ebx, [M+" + expArgsB2.addr
                                        + "] ; alocando valor em end. de expArgsB2 para registrador\n");
                                writer.write("\tcvtsi2ss xmm1, ebx ; int32 para float\n");
                            }

                            writer.write("\tcomiss xmm0, xmm1 ; comparando xmm0 com xmm1\n");

                            rotTrue = "Rot" + setRot();

                            if (tokenOperator == tokenEqual) {
                                writer.write("\tje " + rotTrue + " ; caso iguais, jmp para RotVerdadeiro\n");
                            } else if (tokenOperator == tokenDif) {
                                writer.write("\tjne " + rotTrue + " ; caso diferentes, jmp para RotVerdadeiro\n");
                            } else if (tokenOperator == tokenLess) {
                                writer.write("\tjb " + rotTrue + " ; caso menor, jmp para RotVerdadeiro\n");
                            } else if (tokenOperator == tokenGtr) {
                                writer.write("\tja " + rotTrue + " ; caso maior, jmp para RotVerdadeiro\n");
                            } else if (tokenOperator == tokenLessEqual) {
                                writer.write("\tjbe " + rotTrue + " ; caso menor ou igual, jmp para RotVerdadeiro\n");
                            } else if (tokenOperator == tokenGtrEqual) {
                                writer.write("\tjae " + rotTrue + " ; caso maior ou igual, jmp para RotVerdadeiro\n");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (expArgsB1.type == "Char") {
                        try {
                            writer.write("\tmov al, [M+" + expArgsA.addr
                                    + "] ; alocando valor em end. de expArgsA a registrador\n");
                            writer.write("\tmov bl, [M+" + expArgsB2.addr
                                    + "] ; alocando valor em end. de expArgsB2 a registrador\n");

                            writer.write("\tcmp al, bl ; comparando al com bl\n");

                            rotTrue = "Rot" + setRot();

                            if (tokenOperator == tokenEqual) {
                                writer.write("\tje " + rotTrue + " ; caso iguais, jmp para RotVerdadeiro\n");
                            } else if (tokenOperator == tokenDif) {
                                writer.write("\tjne " + rotTrue + " ; caso diferentes, jmp para RotVerdadeiro\n");
                            } else if (tokenOperator == tokenLess) {
                                writer.write("\tjl " + rotTrue + " ; caso menor, jmp para RotVerdadeiro\n");
                            } else if (tokenOperator == tokenGtr) {
                                writer.write("\tjg " + rotTrue + " ; caso maior, jmp para RotVerdadeiro\n");
                            } else if (tokenOperator == tokenLessEqual) {
                                writer.write("\tjle " + rotTrue + " ; caso menor ou igual, jmp para RotVerdadeiro\n");
                            } else if (tokenOperator == tokenGtrEqual) {
                                writer.write("\tjge " + rotTrue + " ; caso maior ou igual, jmp para RotVerdadeiro\n");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (expArgsB1.type == "String") { 
                        try {
                            rotTrue = "Rot" + setRot();
                            rotFalse = "Rot" + setRot();
                            String rotLoopStr = "Rot" + setRot();

                            writer.write("\tmov rsi, M+" + expArgsA.addr + " ; passa o endereco da string A pra rax\n");
                            writer.write(
                                    "\tmov rdi, M+" + expArgsB2.addr + " ; passa o endereco da string B pra rbx\n");
                            writer.write(rotLoopStr + ": ; string loop \n");
                            writer.write("\tmov al, [rsi] ; pega o caractere na posicao rax+eax da string A\n");
                            writer.write("\tmov bl, [rdi] ; pega o caractere na posicao rax+eax da string B\n");
                            writer.write("\tcmp al, bl ; comparando al com bl\n");
                            writer.write("\tjne " + rotFalse + "; char nao eh igual, fim\n");
                            writer.write("\tadd rsi, 1 ; incrementa o contador\n");
                            writer.write("\tadd rdi, 1 ; incrementa o contador\n");
                            writer.write("\tcmp al, 0 ; fim da strA?\n");
                            writer.write("\tje " + rotTrue + "; se sim, passa pro check final\n");
                            writer.write("\tjmp " + rotLoopStr + "; se nao, continua loop\n");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // Com base no resultado da comparacao, coloca resultado 0 ou 1 no registrador eax, cria novo temporario para expArgsA e aloca eax neste endereco.
                    try {
                        String rotEnd = "Rot" + setRot();

                        if (expArgsB1.type == "String")
                            writer.write(rotFalse + ": ; rotFalse\n");
                        writer.write("\tmov eax, 0 ; teste deu false\n");
                        writer.write("\tjmp " + rotEnd + " ; jmp para RotFim\n");

                        writer.write(rotTrue + ": ; RotVerdadeiro\n");
                        writer.write("\t\tmov eax, 1 ; teste deu true\n");

                        expArgsA.type = "Boolean";
                        expArgsA.addr = tempCounter;
                        updateTempCounter(expArgsA.type, 4);

                        writer.write(rotEnd + ": ; RotFim\n");
                        writer.write("\tmov [M+ " + expArgsA.addr
                                + "], eax ; alocando resultado bool no endereco de expArgsA\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /* 
            Na gramática: 
            EXP_B->	[- (31)] EXP_C1 (26) (27) { (+ | - | "||") EXP_C2 (28) (29) (30)}
        
            Metodo EXP_B -> Símbolo não terminal auxiliar 1 para expressoes.
            Opcionalmente pode iniciar com token de menos. Chama EXP_C e pode opcionalmente rodar (+ | - | "||") EXP_C, quantas vezes quiser.
        
            Regras de Semântica:
            (26): {if (minus & !(EXP_C1.tipo = integer | EXP_C2.tipo = float)) then ERRO}
            (27): {EXP_B.tipo := EXP_C1.tipo}
            (28): {if (operador = +) then if (!((EXP_C1.tipo = integer | EXP_C1.tipo = float) & (EXP_C2.tipo = integer | EXP_C2.tipo = float)) | (EXP_D1.tipo != integer && EXP_D1.tipo != float) |  (EXP_D2.tipo != integer && EXP_D2.tipo != float)))  then ERRO;
                    else if (EXP_C1.tipo = float | EXP_C2.tipo = float) then EXP_B.tipo = float else EXP_B.tipo = integer}
            (29): {if (operador = -) then if (!((EXP_C1.tipo = integer | EXP_C1.tipo = float) & (EXP_C2.tipo = integer | EXP_C2.tipo = float)) | (EXP_D1.tipo != integer && EXP_D1.tipo != float) |  (EXP_D2.tipo != integer && EXP_D2.tipo != float))) then ERRO;
                    else if (EXP_C1.tipo = float | EXP_C2.tipo = float) then EXP_B.tipo = float else EXP_B.tipo = integer}
            (30): {if (operador = "||") then if !((EXP_C1.tipo = boolean & EXP_C2.tipo = boolean) then ERRO}
            (31): {minus = True}
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

                // Menos so pode em Int e Float
                if (minus && !(expArgsC1.type == "Integer" || expArgsC1.type == "Float")) {
                    throwIdentifierError("incompatible_types");
                    return;
                }

                /* 
                    Caso tenha tokenMinus, nega valor da expressao. 
                    Se a expressao for int, usa o neg. Se for float, realiza multiplicacao por -1.
                */
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
                            writer.write("\tmovss xmm0, [M+" + expArgsC1.addr
                                    + "] ; alocando valor em end. de expArgsC1 a registrador\n");
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

                    /* 
                        Geração de Código para operacoes + - e Or.
                        + e - : 
                            Caso as duas expressões sejam Int, so aloca em registradores e realiza a operacao de inteiros com base no Operador.
                            Caso uma ou duas delas seja float, verifica necessidade de conversão de tipo e aloca para registradores xmm e realiza a operacao de reais com base no Operador.
                        Or:
                            So pode com boolean, codigo sera explicado abaixo.
                    */
                    if (operator == tokenPlus) {
                        if ((expArgsC1.type != "Integer" && expArgsC1.type != "Float")
                                || (expArgsC2.type != "Integer" && expArgsC2.type != "Float")
                                || ((expArgsC1.type == "Integer" || expArgsC1.type == "Float")
                                        && (expArgsC2.type != "Integer" && expArgsC2.type != "Float"))) {
                            throwIdentifierError("incompatible_types");
                            return;
                        } else if (expArgsC1.type == "Float" || expArgsC2.type == "Float") {

                            if (expArgsB.type == "Float") {
                                try {
                                    writer.write("\tmovss xmm0, [M+" + expArgsB.addr
                                            + "] ; alocando valor em end. de expArgsB a registrador\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    writer.write("\tmov eax, [M+" + expArgsB.addr
                                            + "] ; alocando valor em end. de expArgsB a registrador\n");
                                    writer.write("\tcvtsi2ss xmm0, eax ; int32 para float\n");
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
                                    writer.write("\tmov ebx, [M+" + expArgsC2.addr
                                            + "] ; alocando valor em end. de expArgsC2 a registrador\n");
                                    writer.write("\tcvtsi2ss xmm1, ebx ; int32 para float\n");
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

                            expArgsB.type = "Float";

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

                            if (expArgsB.type == "Float") {
                                try {
                                    writer.write("\tmovss xmm0, [M+" + expArgsB.addr
                                            + "] ; alocando valor em end. de expArgsB a registrador\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    writer.write("\tmov eax, [M+" + expArgsB.addr
                                            + "] ; alocando valor em end. de expArgsB a registrador\n");
                                    writer.write("\tcvtsi2ss xmm0, eax ; int32 para float\n");
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

                                expArgsB.type = "Float";
                            } else {
                                try {
                                    writer.write("\tmov ebx, [M+" + expArgsC2.addr
                                            + "] ; alocando valor em end. de expArgsC2 a registrador\n");
                                    writer.write("\tcvtsi2ss xmm1, ebx ; int32 para float\n");
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

                        /* 
                            Geração de Código do OR
                            Aloca o resultado das expressões nos registradores eax e ebx, e aloca 2 no ecx.
                            Soma eax e ebx, o resultado vai para eax.
                            Limpa o rdx com CDQ.
                            Realiza divisao inteira de eax por ecx (2)
                            Soma quociente (eax) e resto (edx), esse é o resultado do Or.
                            Dessa forma, se a soma deu 2, vai virar 1. Se deu 1 vai continuar 1 e se deu 0 vai continuar 0.
                        */
                        try {
                            writer.write("\tmov eax, [M+" + expArgsB.addr
                                    + "] ; alocando valor em end. de expArgsB a registrador\n");
                            writer.write("\tmov ebx, [M+" + expArgsC2.addr
                                    + "] ; alocando valor em end. de expArgsC2 a registrador\n");
                            writer.write("\tmov ecx, 2 ; alocando valor 2 a ecx\n");
                            writer.write("\tadd eax, ebx ; eax = eax + ebx\n");
                            writer.write("\tcdq ; limpa o rdx \n");
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
            Na gramática: 
            EXP_C->	EXP_D1 (20) { ("*" | && | / | div | mod) EXP_D2 (21) (22) (23) (24) (25)} 
        
            Metodo EXP_C -> Símbolo não terminal auxiliar 2 para expressoes.
            Chama EXP_D e pode opcionalmente rodar ("*" | && | / | div | mod) EXP_D, quantas vezes quiser.
        
            Regras de Semântica:
            (20): {EXP_C.tipo := EXP_D1.tipo}
            (21): {if (operador = "*") then if (!((EXP_D1.tipo = integer | EXP_D1.tipo = float) & (EXP_D2.tipo = integer | EXP_D2.tipo = float)) | (EXP_D1.tipo != integer && EXP_D1.tipo != float) |  (EXP_D2.tipo != integer && EXP_D2.tipo != float)) then ERRO;
                    else if (EXP_D1.tipo = float | EXP_D2.tipo = float) then EXP_C.tipo = float else EXP_C.tipo = integer}
            (22): {if (operador = /) then if (!((EXP_D1.tipo = integer | EXP_D1.tipo = float) & (EXP_D2.tipo = integer | EXP_D2.tipo = float)) | (EXP_D1.tipo != integer && EXP_D1.tipo != float) |  (EXP_D2.tipo != integer && EXP_D2.tipo != float))  then ERRO;
                    else if (EXP_D1.tipo = float | EXP_D2.tipo = float) then EXP_C.tipo = float else EXP_C.tipo = integer}
            (23): {if (operador = div) then if !((EXP_D1.tipo = integer & EXP_D2.tipo = integer) then ERRO}
            (24): {if (operador = mod) then if !((EXP_D1.tipo = integer & EXP_D2.tipo = integer) then ERRO}
            (25): {if (operador = &&) then if !((EXP_D1.tipo = boolean & EXP_D2.tipo = boolean) then ERRO}
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

                    /* 
                        Geração de Código para operacoes * / div mod e AND.
                        * : 
                            Caso as duas expressões sejam Int, so aloca em registradores e realiza a operacao de inteiros com base no Operador.
                            Caso uma ou duas delas seja float, verifica necessidade de conversão de tipo e aloca para registradores xmm e realiza a operacao de reais com base no Operador.
                        / :
                            Verifica necessidade de conversao de tipos das expressoes e aloca em registradores xmm, visto que o resultado dessa divisao real sempre sera float.
                        div e mod:
                            So funciona com inteiros. Realiza idiv e aloca eax (quociente) para div ou edx (resto) para mod em endereco da expressao.
                        AND:
                            So pode com boolean, codigo sera explicado abaixo.
                    */
                    if (operator == tokenMult) {
                        if ((expArgsD1.type != "Integer" && expArgsD1.type != "Float")
                                || (expArgsD2.type != "Integer" && expArgsD2.type != "Float")
                                || ((expArgsD1.type == "Integer" || expArgsD1.type == "Float")
                                        && (expArgsD2.type != "Integer" && expArgsD2.type != "Float"))) {
                            throwIdentifierError("incompatible_types");
                            return;
                        } else if (expArgsC.type == "Float" || expArgsD2.type == "Float") {
                            if (expArgsC.type == "Float") {
                                try {
                                    writer.write("\tmovss xmm0, [M+" + expArgsC.addr
                                            + "] ; alocando valor em end. de expArgsC a registrador\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    writer.write("\tmov eax, [M+" + expArgsC.addr
                                            + "] ; alocando valor em end. de expArgsC a registrador\n");
                                    writer.write("\tcvtsi2ss xmm0, eax ; int32 para float\n");
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
                                    writer.write("\tmov ebx, [M+" + expArgsD2.addr
                                            + "] ; alocando valor em end. de expArgsD2 a registrador\n");
                                    writer.write("\tcvtsi2ss xmm1, ebx ; int32 para float\n");
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

                            expArgsC.type = "Float";

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

                            if (expArgsC.type == "Float") {
                                try {
                                    writer.write("\tmovss xmm0, [M+" + expArgsC.addr
                                            + "] ; alocando valor em end. de expArgsC a registrador\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    writer.write("\tmov eax, [M+" + expArgsC.addr
                                            + "] ; alocando valor em end. de expArgsC a registrador\n");
                                    writer.write("\tcvtsi2ss xmm0, eax ; int64 para float\n");
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
                                    writer.write("\tmov ebx, [M+" + expArgsD2.addr
                                            + "] ; alocando valor em end. de expArgsD2 a registrador\n");
                                    writer.write("\tcvtsi2ss xmm1, ebx ; int64 para float\n");
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            expArgsC.type = "Float";

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
                                    + "] ; alocando valor em end. de expArgsD2 a registrador \n");
                            writer.write("\tcdq ; limpa o rdx \n");
                            writer.write("\tidiv ebx ; eax div ebx eax: " + expArgsC.addr + " ebx: " + expArgsD2.addr
                                    + "\n");
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
                            writer.write("\tcdq ; limpa o rbx\n");
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

                        /* 
                            Geração de Código do AND.
                            Aloca resultados das expressões em registradores eax e ebx e realiza multiplicacao deles com imul. 
                            Aloca em endereco de expArgsC (novoTemp) o resultado.
                        */
                        try {
                            writer.write("\tmov eax, [M+" + expArgsC.addr
                                    + "] ; alocando valor em end. de expArgsC a registrador\n");
                            writer.write("\tmov ebx, [M+" + expArgsD2.addr
                                    + "] ; alocando valor em end. de expArgsD2 a registrador\n");
                            writer.write("\timul ebx ; eax AND ebx\n");
                            expArgsC.addr = tempCounter;
                            updateTempCounter(expArgsC.type, 0);
                            writer.write("\tmov [M+" + expArgsC.addr
                                    + "], eax ; aloca resultado do AND em endereco de expArgsC\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        /* 
            Na gramática: 
            EXP_D->	{! (11) } EXP_E (12) (13)
        
            Metodo EXP_D -> Símbolo não terminal auxiliar 3 para expressoes.
            Pode iniciar com token !, e enquanto o proximo for igual a !, continua nesse loop. Depois chama EXP_E.
        
            Regras de Semântica:
            (11): {houve_negacao := True}
            (12): {if (houve_negacao & EXP_E.tipo != bool) then ERRO}
            (13): {EXP_D.tipo := EXP_E.tipo}
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

                // Not so funciona com boolean
                if (not && expArgsE.type != "Boolean") {
                    throwIdentifierError("incompatible_types");
                    return;
                }

                expArgsD.type = expArgsE.type;

                /* 
                    Caso not, aloca resultado da expressao em registrador eax e nega com neg.
                    Soma 1 para finalizar not logico.
                    Aloca resultado em endereco de expArgsD (novoTemp)
                */
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
            Na gramática: 
            EXP_E->	(int (9) | float (10) ) "(" EXP_A (8) ")" | EXP_F (7)
        
            Metodo EXP_E -> Símbolo não terminal auxiliar 4 para expressoes.
            Caso inicia com token int ou float, precisa de token (, depois chama EXP_A e volta para verificar token ). 
            Caso contrario, chama EXP_F.
        
            Regras de Semântica:
            (7): {EXP_E.tipo := EXP_F.tipo}
            (8): {if !(EXP_A.tipo = integer | EXP_A.tipo = float) then ERRO}
            (9): {EXP_E.tipo := integer}
            (10): {EXP_E.tipo := float}
        */
        void EXP_E(EXP_args expArgsE) {
            if (!pauseCompiling) {
                if (currentToken.token == tokenInt || currentToken.token == tokenFloat) {
                    if (currentToken.token == tokenInt) {
                        expArgsE.type = "Integer";

                        checkToken(tokenInt);
                        if (pauseCompiling)
                            return;
                    } else if (currentToken.token == tokenFloat) {
                        expArgsE.type = "Float";

                        checkToken(tokenFloat);
                        if (pauseCompiling)
                            return;
                    }

                    if (currentToken.token == tokenOpenPar) {
                        checkToken(tokenOpenPar);
                        if (pauseCompiling)
                            return;

                        EXP_args expArgsA = new EXP_args();
                        EXP_A(expArgsA);
                        if (pauseCompiling)
                            return;

                        // Conversao de int / float so funciona com estes tipos.
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

                        /* 
                            Caso a conversao seja de Float para int -> novoTemp, aloca em xmm0 valor em expArgsA, utiliza cvtss2si e aloca resultado em endereco de expArgsE
                            Caso a conversao seja de Int para float -> novoTemp, aloca em eax valor em expArgsA, utiliza cvtsi2ss e e aloca resultado em endereco de expArgsE
                        */
                        try {
                            if (expArgsE.type == "Integer" && expArgsA.type == "Float") {
                                expArgsE.addr = tempCounter;
                                updateTempCounter(expArgsE.type, 0);
                                writer.write("\tmovss xmm0, [M+" + expArgsA.addr
                                        + "] ; alocando valor em end. de expArgsA a registrador\n");
                                writer.write("\tcvtss2si rax, xmm0 ; convertendo float para int64\n");
                                writer.write("\tmov [M+" + expArgsE.addr
                                        + "], eax ; alocando valor de eax a end. de expArgsE\n");
                            } else if (expArgsE.type == "Float" && expArgsA.type == "Integer") {
                                expArgsE.addr = tempCounter;
                                updateTempCounter(expArgsE.type, 0);
                                writer.write("\tmov eax, [M+" + expArgsA.addr
                                        + "] ; alocando valor em end. de expArgsA a registrador\n");
                                writer.write("\tcvtsi2ss xmm0, eax ; convertendo int32 para float\n");
                                writer.write("\tmovss [M+" + expArgsE.addr
                                        + "], xmm0 ; alocando valor de xmm0 a end. de expArgsE\n");
                            } else {
                                expArgsE.addr = expArgsA.addr;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
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
            Na gramática: 
            EXP_F-> "(" EXP_A1 ")" (6) | id (2)(3) ["[" EXP_A2 (4)(5) "]"] | valor (1)
        
            Metodo EXP_F -> Símbolo não terminal auxiliar 5 para expressoes.
            Caso inicia com token = (, executa EXP_A e fecha o parenteses com token = ).
            Caso inicia com token identificador, pode opcionalmente ter tambem "[" EXP_A "]".
            Por ultimo, pode ser tambem um valor. 
            Else, erro.
        
            Regras de Semântica:
            (1): {EXP_F.tipo := valor.tipo}
            (2): {if id.simbolo.classe = null then ERRO}
            (3): {EXP_F.tipo := id.simbolo.tipo}
            (4): {if (EXP_A2.tipo != integer | id.simbolo.tipo != string) then ERRO}
            (5): {EXP_F.tipo := char}
            (6): {EXP_F.tipo := EXP_A1.tipo}
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

                        /*  
                            Acesso a posicao de String. 
                            Endereço de expArgsF é novo temporario. Zera rax, indice em eax e soma o mesmo com endereco inicial da string em registrador de 64 bits (rax).
                            Valor em rax vai para registrador bl (char). Aloca valor em bl para endereco de expArgsF.
                        */
                        expArgsF.type = "Char";
                        expArgsF.addr = tempCounter;
                        updateTempCounter(expArgsF.type, 0);
                        try {
                            writer.write("\tmov rax, 0 ; zerando rax\n");
                            writer.write("\tmov eax, [M+" + expArgsA2.addr
                                    + "] ; alocando valor em end. de expArgsA2 a registrador (indice)\n");
                            writer.write(
                                    "\tadd rax, M+" + currentSymbol.addr + " ; indice + posicao inicial do string\n");
                            writer.write("\tmov bl, [rax] ; alocando valor em rax a registrador bl\n");
                            writer.write("\tmov [M+" + expArgsF.addr
                                    + "], bl ; alocando conteudo de bl em end. de expArgsF\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (currentToken.token == tokenCloseSq) {
                            checkToken(tokenCloseSq);
                            if (pauseCompiling)
                                return;
                        } else
                            throwParserError();
                    }
                } else if (currentToken.token == tokenValue) {
                    expArgsF.type = currentToken.type;

                    /* 
                        A expressão é um valor.
                        Caso seja Float ou String, abre sessão de dados e aloca na memoria pois nao tem como usar movss com imediato. Atualiza posicao global de memoria.
                        Caso seja int ou char, coloca valor em registrador eax ou al e aloca em endereco de expArgsF (novo Temporario).
                    */
                    if (currentToken.type == "Float" || currentToken.type == "String") {
                        try {
                            writer.write("section .data\n");
                            if (currentToken.type == "Float") {
                                String floatValue = treatFloat(currentToken.lexeme);
                                writer.write("\tdd " + floatValue + " ; declarando valor na area de dados\n");
                            } else if (currentToken.type == "String") {
                                writer.write(
                                        "\tdb " + currentToken.lexeme + ", 0 ; declarando valor na area de dados\n");
                            }
                            writer.write("section .text\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        expArgsF.addr = posMem;
                        // Length -1 pq o java ta contando as aspas. -2 + 1 (zero no final) = -1
                        updatePosMem(currentToken.type, currentToken.lexeme.length() - 1);

                    } else {
                        expArgsF.addr = tempCounter;
                        updateTempCounter(currentToken.type, 0);

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

                    expArgsF.type = currentToken.type;

                    checkToken(tokenValue);
                    if (pauseCompiling)
                        return;
                } else
                    throwParserError();
            }
        }
    }

    // Metodo para leitura do arquivo fonte.
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

        BufferedReader br = new BufferedReader(new FileReader("tests/codegen.in"));
        //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        fileStr = readAllCharsOneByOne(br);

        lexer.getLexeme(fileStr);

        parser.START();

        if (!pauseCompiling && lineCount != 1)
            System.out.println(lineCount + " linhas compiladas.");
    }
}
