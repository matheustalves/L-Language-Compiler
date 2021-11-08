START-> 	(1) { (2) DECL_A | (3) COMANDO} eof (12)

DECL_A-> 	(| float | string | char) DECL_B1 {,  DECL_B2} ;	|
			const id (15) (4) (8) = [- (5)] valor (6) (7);

DECL_B-> 	id  (15) (4) (10) [<- (9) [- (5)] valor (11) (6) (7)] 

COMANDO->	id ["[" (28) EXP_A1 (29) "]"] <- EXP_A2 (30) (31) (32);		  |
			while (33) EXP_A3 (35) TIPO_CMD	(36)		        |
			if (37) EXP_A4 (38) TIPO_CMD (41) [else (39) TIPO_CMD (40)]	(42)		|
			readln "(" id (43) (44) ")";				            |
			(write (47) | writeln (48) ) "(" LISTA_EXP  (49) ")";		|
			;

TIPO_CMD->	COMANDO | "{" {COMANDO} "}"
LISTA_EXP->	(37) EXP_A1 (45) (46) {, (37) EXP_A2 (45) (46) }

EXP_A-> 	EXP_B1 (25) [ (= | != | < | > | <= | >= ) (21) EXP_B2 (26) (27)]
EXP_B->		(15) [- (5)] EXP_C1 (23) { (+ | - | "||") (21) EXP_C2 (24)}
EXP_C->		EXP_D1 (20) { ("*" | && | / | div | mod ) (21) EXP_D2 (22)} 
EXP_D->		(13) {! (14) } EXP_E (19)
EXP_E->		( int (50) | float (51) ) "(" EXP_A  ")" (52) | EXP_F (18)
EXP_F->     "(" EXP_A1 ")" (34)  | id (53)  ["[" EXP_A2 (54)  "]"] | valor (16) (17)

(1):
    global _start ; Ponto inicial do programa
    _start: ; Inicio do programa
    section .data ; sessao de dados
    M: ; rotulo de inicio da sessao de dados
        resb 10000h ; reserva de temporarios

(2):
    section .data ; sessao de dados

(3):
    section .text ; sessao de codigo

(4):
    id.simbolo.end := posMem(id.simbolo.tipo); 
    id.simbolo.class := "const";
    id.simbolo.tipo := idTipo;

(5):
    has_minus = True;

(6):
    id.simbolo.valor = token.lex;
    has_minus then id.simbolo.valor = "-id.simbolo.valor; 

(7):
    if id.simbolo.tipo != null
        if id.simbolo.tipo = Char
            db id.simbolo.valor ; char no simbolo.end
        if id.simbolo.tipo = Integer
            dd id.simbolo.valor ; inteiro no simbolo.end
        if id.simbolo.tipo = Float
            dd id.simbolo.valor
        if id.simbolo.tipo = String
            if id.simbolo.class = "const"
                db id.simbolo.valor, 0
            else
                db id.simbolo.valor, 0
                resb 256 - len(id.simbolo.valor) - 1
    else
        if id.simbolo.tipo = Char
            resb 1 ; char no simbolo.end
        if id.simbolo.tipo = Integer
            resd 1 ; inteiro no simbolo.end
        if id.simbolo.tipo = Float
            resd 1
        if id.simbolo.tipo = String
            resb 256
(8):
    id.simbolo.valor := token.lex;

(9):
    has_value = True;

(10):
    has_value = False;

(11):
    if has_value
        id.simbolo.valor := token.lex;

(12):
    mov rax, 60
    mov rdi, 0
    syscall

(13):
    has_neg = False;

(14):
    has_neg = True;

(15):
    has_minus = False;

(16):
    EXP_F.tipo := token.tipo;

(17):
    if token.tipo = Float or String
        section .data
            if token.tipo = Float
                dd token.lex
            else if token.tipo = String
                db token.lex, 0
        section .text

        EXP_F.end := posMem(token.tipo);
    else
        EXP_F.end := novoTemp(token.tipo)  
        if token.tipo = Integer
            mov eax, token.lex ; imediato para registrador
            mov [M+EXP_F.end], eax ; alocando valor do registrador no endereco de expArgsF
        if token.tipo = Char
            mov al, token.lex ; imediato para registrador
            mov [M+EXP_F.end], al ; alocando valor do registrador no endereco de expArgsF

    EXP_F.tipo = token.tipo;
    

(18):
    EXP_E.tipo := EXP_F.tipo;
    EXP_E.end := EXP_F.end;

(19):
    EXP_D.tipo := EXP_E.tipo;
    if has_neg
        EXP_D.end := novoTemp(EXP_E.tipo);
            mov eax, [M+EXP_E.end] ; alocando valor em end. de expArgsE a registrador
            neg eax ; negando conteudo de registrador
            add eax, 1 ; finalizando not logico
            mov [M+EXP_D.end], eax ; atualizando valor em end. de expArgsD com negacao
    else
        EXP_D.end := EXP_E.end;

(20):
    EXP_C.tipo := EXP_D1.tipo;
    EXP_C.end := EXP_D1.end;

(21):
    operator = token.token;

(22):
    if operator = "*"
        if EXP_C.tipo = Float || EXP_D2.tipo = Float
            if EXP_C.tipo = Float
                movss xmm0, [M+EXP_C.end] ; alocando valor em end. de expArgsC a registrador
            else
                mov eax, [M+EXP_C.end] ; alocando valor em end. de expArgsC a registrador
                cvtsi2ss xmm0, eax ; int32 para float

            if EXP_D2.tipo = Float     
                movss xmm1, [M+EXP_D2.end] ; alocando valor em end. de expArgsD2 a registrador
            else
                mov ebx, [M+EXP_D2.end] ; alocando valor em end. de expArgsD2 a registrador
                cvtsi2ss xmm1, ebx ; int32 para float

            mulss xmm0, xmm1 ; xmm0 * xmm1
            EXP_C.end = novoTemp(EXP_C.tipo);
            movss [M+EXP_C.end], xmm0 ; aloca resultado da mult. em endereco de expArgsC

            EXP_C.tipo = Float;
        else
            EXP_C.tipo = Integer;
            mov eax, [M+EXP_C.end] ; alocando valor em end. de expArgsC a registrador                   
            mov ebx, [M+EXP_D2.end] ; alocando valor em end. de expArgsD2 a registrador
            imul ebx ; eax * ebx
            EXP_C.end = novoTemp(EXP_C.tipo);
            mov [M+EXP_C.end], eax ; aloca resultado da mult. em endereco de expArgsC
    
    if operator = /
        if EXP_C.tipo = Float
            movss xmm0, [M+EXP_C.end] ; alocando valor em end. de expArgsC a registrador
        else
            mov eax, [M+EXP_C.end] ; alocando valor em end. de expArgsC a registrador
            cvtsi2ss xmm0, eax ; int32 para float

        if EXP_D2.tipo = Float     
            movss xmm1, [M+EXP_D2.end] ; alocando valor em end. de expArgsD2 a registrador
        else
            mov ebx, [M+EXP_D2.end] ; alocando valor em end. de expArgsD2 a registrador
            cvtsi2ss xmm1, ebx ; int32 para float
        
        EXP_C.tipo = Float;
        divss xmm0, xmm1 ; xmm0 / xmm1
        EXP_C.end = novoTemp(EXP_C.tipo);
        movss [M+EXP_C.end], xmm0 ; aloca resultado da divisao em endereco de expArgsC

    if operator = div
        mov eax, [M+EXP_C.end] ; alocando valor em end. de expArgsC a registrador
        mov ebx, [M+EXP_D2.end] ; alocando valor em end. de expArgsD2 a registrador
        cdq ; limpa o rdx
        idiv ebx ; eax div ebx eax:
        EXP_C.end = novoTemp(EXP_C.tipo);
        mov [M+EXP_C.end], eax ; aloca quociente da div. em endereco de expArgsC
    
    if operator = mod
        mov eax, [M+EXP_C.end] ; alocando valor em end. de expArgsC a registrador
        mov ebx, [M+EXP_D2.end] ; alocando valor em end. de expArgsD2 a registrador
        cdq ; limpa o rdx
        idiv ebx ; eax div ebx eax:
        EXP_C.end = novoTemp(EXP_C.tipo);
        mov [M+EXP_C.end], edx ; aloca o resto da div. em endereco de expArgsC        
    
    if operator = &&
        mov eax, [M+EXP_C.end] ; alocando valor em end. de expArgsC a registrador
        mov ebx, [M+EXP_D2.end] ; alocando valor em end. de expArgsD2 a registrador        
        imul ebx ; eax AND ebx
        EXP_C.end = novoTemp(EXP_C.tipo, 0);
        mov [M+EXP_C.end], eax ; aloca resultado do AND em endereco de expArgsC

(23):
    if has_minus
        EXP_B.end := novoTemp(EXP_C1.tipo);
        if EXP_C1.tipo = Integer
            mov eax, [M+EXP_C1.end] ; alocando valor em end. de expArgsC1 a registrador
            neg eax ; negando valor de registrador
            mov [M+EXP_B.end], eax ; alocando valor negado em end. de expArgsB
        else
            movss xmm0, [M+EXP_C1.end] ; alocando valor em end. de expArgsC1 a registrador 
            mov rbx, -1 ; alocando -1 em rbx
            cvtsi2ss xmm1, rbx ; -1 (int64) para float   
            mulss xmm0, xmm1 ; xmm * -1
            movss [M+EXP_B.end], xmm0 ; alocando valor negado em end. de expArgsB
    else
        EXP_B.end := EXP_C1.end;

(24):
    if operator = +
        if EXP_C1.tipo = Float or EXP_C2.tipo = Float
        
            if EXP_B.tipo = Float
                movss xmm0, [M+EXP_B.end] ; alocando valor em end. de expArgsB a registrador
            else
                mov eax, [M+EXP_B.end] ; alocando valor em end. de expArgsB a registrador
                cvtsi2ss xmm0, eax ; int32 para float

            if EXP_C2.tipo = Float     
                movss xmm1, [M+EXP_C2.end] ; alocando valor em end. de expArgsC2 a registrador
            else
                mov ebx, [M+EXP_C2.end] ; alocando valor em end. de expArgsC2 a registrador
                cvtsi2ss xmm1, ebx ; int32 para float

            addss xmm0, xmm1 ; xmm0+xmm1
            EXP_B.end := novoTemp(EXP_B.tipo);
            movss [M+EXP_B.end], xmm0 ; aloca resultado da soma em endereco de expArgsB

            EXP_B.tipo = Float
        else
            EXP_B.tipo = Integer

            mov eax, [M+EXP_B.end] ; alocando valor em end. de expArgsB a registrador
            mov ebx, [M+EXP_C2.end] ; alocando valor em end. de expArgsC2 a registrador        
            add ebx ; eax + ebx
            EXP_B.end = novoTemp(EXP_B.tipo);
            mov [M+EXP_B.end], eax ; aloca resultado da soma em endereco de expArgsB

    if operator = -
        if EXP_C1.tipo = Float or EXP_C2.tipo = Float
        
            if EXP_B.tipo = Float
                movss xmm0, [M+EXP_B.end] ; alocando valor em end. de expArgsB a registrador
            else
                mov eax, [M+EXP_B.end] ; alocando valor em end. de expArgsB a registrador
                cvtsi2ss xmm0, eax ; int32 para float

            if EXP_C2.tipo = Float     
                movss xmm1, [M+EXP_C2.end] ; alocando valor em end. de expArgsC2 a registrador
            else
                mov ebx, [M+EXP_C2.end] ; alocando valor em end. de expArgsC2 a registrador
                cvtsi2ss xmm1, ebx ; int32 para float

            subss xmm0, xmm1 ; xmm0-xmm1
            EXP_B.end := novoTemp(EXP_B.tipo);
            movss [M+EXP_B.end], xmm0 ; aloca resultado da subtração em endereco de expArgsB

            EXP_B.tipo = Float
        else
            EXP_B.tipo = Integer

            mov eax, [M+EXP_B.end] ; alocando valor em end. de expArgsB a registrador
            mov ebx, [M+EXP_C2.end] ; alocando valor em end. de expArgsC2 a registrador        
            sub ebx ; eax - ebx
            EXP_B.end = novoTemp(EXP_B.tipo);
            mov [M+EXP_B.end], eax ; aloca resultado da sub em endereco de expArgsB

    if operator = "||"
        mov eax, [M+EXP_B.end] ; alocando valor em end. de expArgsB a registrador
        mov ebx, [M+EXP_C2.end] ; alocando valor em end. de expArgsC2 a registrador        
        mov ecx, 2 ; alocando valor 2 a ecx
        add eax, ebx ; eax = eax + ebx
        cdq ; limpa o rdx 
        idiv ecx ; dividindo eax por 2
        add eax, edx ; somando quociente e resto da divisao (Or logico)
        EXP_B.end = novoTemp(EXP_B.tipo, 0);
        mov [M+EXP_B.end], eax ; aloca resultado do OR em endereco de expArgsB

(25):
    EXP_A.tipo := EXP_B1.tipo;
    EXP_A.end := EXP_B1.end;

(26):
    if EXP_B1.tipo = Integer and EXP_B2.tipo = Integer
        mov eax, [M+EXP_A] ; alocando valor em end. de expArgsA a registrador
        mov ebx, [M+EXP_B2] ; alocando valor em end. de expArgsB2 a registrador
        cmp eax, ebx ; comparando eax com ebx
        
    
    else if EXP_B1.tipo = Float or EXP_B2.tipo = Float
        if EXP_A = Float
            movss xmm0, [M+EXP_A] ; alocando valor em end. de expArgsA para registrador
        else
            mov eax, [M+EXP_A] ; alocando valor em end. de expArgsA para registrador
            cvtsi2ss xmm0, eax ; int32 para float
        if EXP_B2 = Float
            movss xmm1, [M+EXP_B2] ; alocando valor em end. de expArgsB2 para registrador
        else
            mov ebx, [M+EXP_B2] ; alocando valor em end. de expArgsA para registrador
            cvtsi2ss xmm1, ebx ; int32 para float

        comiss xmm0, xmm1 ; comparando xmm0 com xmm1
    
    else if EXP_B1.type = Char
        mov al, [M+EXP_A] ; alocando valor em end. de expArgsA a registrador
        mov bl, [M+EXP_B2] ; alocando valor em end. de expArgsB2 a registrador
        cmp al, bl ; comparando al com bl  

(27):
    if !(EXP_B1.tipo = String)    
        rotTrue := setRot();

        if operator = =
            je rotTrue 
        if operator = !=
            jne rotTrue
        if operator = <
            jl rotTrue
        if operator = >
            jg rotTrue
        if operator = <=
            jle rotTrue
        if operator = >=
            jge rotTrue

    else
        rotTrue := setRot();
        rotFalse := setRot();
        rotLoopStr := setRot();
        mov rsi, M+EXP_A.end ; passa o endereco da string A pra rax\n
        mov rdi, M+EXP_B.end ; passa o endereco da string B pra rbx
        rotLoopStr:
        mov al, [rsi] ; pega o caractere na posicao rax+eax da string A
        mov bl, [rdi] ; pega o caractere na posicao rax+eax da string B
        cmp al, bl ; comparando al com bl
        jne rotFalse ; char nao eh igual, fim
        add rsi, 1 ; incrementa o contador
        add rdi, 1 ; incrementa o contador
        cmp al, 0 ; fim da strA?
        je rotTrue ; se sim, passa pro check final
        jmp rotLoopStr; se nao, continua loop

    rotEnd := setRot();
    if EXP_B1.tipo = String
        rotFalse:
    
    mov eax, 0; teste false
    jmp rotEnd ; jmp pra RotFim

    rotTrue:
        mov eax, 1 ; teste true
    
    EXP_A.tipo = Boolean;
    EXP_A.end := novoTemp(EXP_A.tipo, 4);
    mov [M+EXP_A.end], eax ;aloca o resultado em EXP_A

(28):
    tempCounter = 0;

(29):
    is_index := True
    tipoAtrib := Char

(30):
    if is_index
        destAddr := "M+id.simbolo.end;
        tipoAtrib := id.simbolo.tipo;
    else
        mov rax, 0 ; zerando o rax
        mov eax, [M+EXP_A1.end] ; alocando valor em end. de expArgsA1 a registrador (indice)
        add rax, M+id.simbolo.end ; indice + posicao inicial do string

        destAddr := "rax";

(31):
    if id.simbolo.tipo = Float AND EXP_A2.tipo = Integer
        mov eax, [M+EXP_A2.end] ; alocando valor em end. de expArgsA2 a registrador
        cvtsi2ss xmm0, eax ; int32 para float
        movss [M+EXP_A2.end], xmm0 ; alocando valor de xmm0 a end. de expArgsA2

(32):
    if tipoAtrib = Char
        mov bl, [M+EXP_A2.end]
        mov [destAddr], bl

    if tipoAtrib = Integer
        mov eax, [M+EXP_A2.end]
        mov [destAddr], eax

    if tipoAtrib = Float
        mov eax, [M+EXP_A2.end]
        movss [destAddr], xmm0

    if tipoAtrib = String
        rotLoopStr := setRot();
        mov rsi, destAddr ; passa o endereco da string A pra rax
        mov rdi, M+EXP_A2.end ; passa o endereco da string B pra rbx
        rotLoopStr:
        mov al, [rdi] ; pega o caractere na posicao rdi+i da string B
        mov [rsi], al ; coloca o char na string A
        add rsi, 1 ; incrementa o contador
        add rdi, 1 ; incrementa o contador
        cmp al, 0 ; fim da strB?
        jne rotLoopStr; se nao, continua loop

(33):
    tempCounter := 0;
    rotBegin := setRot();
    rotEnd := setRot();

    rotBegin:

(34):
    EXP_F.tipo := EXP_A1.tipo;
    EXP_F.end := EXP_A1.end;

(35):
    mov eax, [M+EXP_A3.end] ; alocando valor em end. de expArgsA3 a registrador
    cmp eax, 1 ; verifica se expressao eh verdadeira
    jne rotEnd ; caso false, desvia para RotFim

(36):
    jmp rotBegin
    rotEnd:

(37):
    tempCounter := 0;

(38):
    rotFalse := setRot();
    rotEnd := setRot();

    mov eax, [M+EXP_A4.end] ; alocando valor em end. de expArgsA4 a registrador
    cmp eax, 1 ; verifica se expressao eh verdadeira
    jne rotFalse ; caso falso, desvia para RotFalso

(39):
    has_else := True;
    jmp rotEnd
    rotFalse:

(40):
    rotEnd:

(41):
    has_else := False;

(42):
    if !has_else
        rotFalse:

(43):
    rotFimStr := setRot();
    rotBufStr := setRot();
    rot_a := setRot();
    rot_b := setRot();
    rot_c := setRot();
    rot_d := setRot();
    rot_e := setRot();
    rot_f := setRot();
    rot_g := setRot();
    rot_h := setRot();

    if id.simbolo.tipo = String
        buffer_read := id.simbolo.end;
    if id.simbolo.tipo = Integer
        buffer_Read := novoTemp(String, 11);
    if id.simbolo.tipo = Float
        buffer_read = novoTemp(String, 14);
    
    mov rsi, M+buffer_read
    mov  rdx, 100h  ;tamanho do buffer
    mov  rax, 0 ;chamada para leitura
    mov  rdi, 0 ;leitura do teclado
    syscall

(44):
    if id.simbolo.tipo = String
        add rax, M+(buffer_read - 1) ; endereço do ultimo caractere lido
        mov rbx, rax ; armazena o endereço em rbx
        mov al, [rbx] ; passa o ultimo caractere lido para al
        cmp al, 10 ; verifica se o ultimo char era linebreak
        je RotrotFimStr ; verifica se ainda ha chars no buffer - String
        RotBufStr:   ; rot de ler buffer de readln(String)
        mov rax, 0 ; chama a leitura
        mov rdi, 0 ; leitor da entrada
        mov rsi, M+buffer_read;
        mov rdx, 1 ; le 1 byte e passa pro rdx
        syscall
        mov al, [M+buffer_read] ; carrega o caractere lido em al
        cmp al, 10 ; verifica se o char eh linebreak;
        jne rotBufStr
        rotFimStr:
        mov al, 0 ; carrega o caractere final de string no al
        mov [rbx], al ; carrega o caractere no endereço de rbx

    if id.simbolo.tipo = Integer
        mov eax, 0     ;acumulador
        mov ebx, 0     ;caractere 
        mov ecx, 10    ;base 10 
        mov dx, 1      ;sinal 
        mov rsi, M+buffer_read       ;end. buffer 
        mov bl, [rsi]     ;carrega caractere 
        cmp bl, '-'       ;sinal - ? 
        jne rot_a       ;se dif -, salta 
        mov dx, -1              ;senão, armazena - 
        add rsi, 1           ;inc. ponteiro string 
        mov bl, [rsi]     ;carrega caractere \n

        rot_a: 
        push dx      ;empilha sinal 
        mov  edx, 0     ;reg. multiplicação \n

        rot_b: 
        cmp  bl, 0Ah    ;verifica fim string 
        je rot_c      ;salta se fim string 
        imul ecx     ;mult. eax por 10 
        sub bl, '0'    ;converte caractere 
        add eax, ebx    ;soma valor caractere 
        add rsi, 1     ;incrementa base 
        mov bl, [rsi]     ;carrega caractere 
        jmp rot_b     ;loop \n

        rot_c: 
        pop cx      ;desempilha sinal 
        cmp cx, 0 
        jg Rotrot_d 
        neg eax      ;mult. sinal \n

        rot_d: \n
        mov [M+id.simbolo.end], eax ; move pro endereço do simbolo
    
    if id.simbolo.tipo = Float
        mov rax, 0     ;acumul. parte int.
        subss xmm0,xmm0      ;acumul. parte frac.
        mov rbx, 0              ;caractere 
        mov rcx, 10               ;base 10
        cvtsi2ss xmm3,rcx    ;base 10
        movss xmm2,xmm3   ;potência de 10
        mov rdx, 1              ;sinal
        mov rsi, M+ buffer_read      ;end. buffer
        mov bl, [rsi]     ;carrega caractere
        cmp bl, '-'       ;sinal - ?
        jne rot_e        ;se dif -, salta
        mov rdx, -1               ;senão, armazena -
        add rsi, 1           ;inc. ponteiro string
        mov bl, [rsi]     ;carrega caractere 

        rot_e:
        push rdx     ;empilha sinal
        mov  rdx, 0     ;reg. multiplicação 

        rot_f:
        cmp  bl, 0Ah    ;verifica fim string
        je rot_g      ;salta se fim string
        cmp  bl, '.'    ;senão verifica ponto
        je rot_h      ;salta se ponto
        imul ecx     ;mult. eax por 10
        sub bl, '0'    ;converte caractere
        add eax, ebx    ;soma valor caractere
        add rsi, 1     ;incrementa base
        mov bl, [rsi]     ;carrega caractere
        jmp rot_f    ;loop 

        rot_h:
          ;calcula parte fracionária em xmm0 

        add rsi, 1           ;inc. ponteiro string
        mov bl, [rsi]     ;carrega caractere
        cmp  bl, 0Ah    ;*verifica fim string
        je rot_g     ;salta se fim string
        sub bl, '0'    ;converte caractere
        cvtsi2ss xmm1,rbx    ;conv real
        divss xmm1,xmm2   ;transf. casa decimal
        addss xmm0,xmm1   ;soma acumul.
        mulss xmm2,xmm3   ;atualiza potência
        jmp rot_h     ;loop 

        rot_g:
        cvtsi2ss xmm1,rax    ;conv parte inteira
        addss xmm0,xmm1   ;soma parte frac.
        pop rcx      ;desempilha sinal
        cvtsi2ss xmm1,rcx    ;conv sinal
        mulss xmm0,xmm1   ;mult. sinal 

        movss [M+id.simbolo.end], xmm0 

(45): (esta é uma função chamada tanto em EXP_A1 quanto EXP_A2, por conveniencia chamarei de a que está nesse contexto de EXP_A)

    rot := setRot();

    if EXP_A.tipo = Integer
        rot_a := setRot();
        rot_b := setRot();
        rot_c := setRot();
        mov eax, [M+EXP_A.end] ; inteiro a ser convertido
        mov rsi, M+tempCounter; end. string ou temp.
        mov rcx, 0 ; contador pilha
        mov rdx, 0 ; tam. string convertido
        push rdx ; 
        cmp eax, 0 ; verifica sinal
        jge rot_a ; salta se numero positivo
        mov bl, '-' ; senao, escreve sinal –
        mov [rsi], bl ; 
        pop rdx ; 
        mov rdx, 1 ; 
        push rdx ; 
        add rsi, 1 ; incrementa indice
        neg eax ; toma modulo do numero\n

        rot_a:
        mov ebx, 10 ; divisor\n

        rot_b:
        add rcx, 1 ; incrementa contador
        cdq ; estende edx:eax p/ div.
        idiv ebx ; divide edx;eax por ebx
        push dx ; empilha valor do resto
        cmp eax, 0 ; verifica se quoc. eh 0
        jne rot_b ; se nao eh 0, continua\n
        mov rdx,rcx ; atualiza tam. string\n
        ; desempilha os valores e escreve o string\n

        rot_c:
        pop ax ; desempilha valor
        add al, '0' ; transforma em caractere
        mov [rsi], al ; escreve caractere
        add rsi, 1 ; incrementa base
        sub rcx, 1 ; decrementa contador
        cmp rcx, 0 ; verifica pilha vazia
        jne rot_c ; se nao pilha vazia, loop\n
        pop rcx ; 
        add rdx, rcx ; 
        mov rsi, M+tempCounter; passando pro rsi o endereço inicial da string 

        ; executa interrupcao de saida
        novoTemp(EXP_A.tipo, 0);
    
    else if EXP_A.tipo = Float
        rot_a = setRot();
        rot_b = setRot();
        rot_c = setRot();
        rot_d = setRot();
        rot_e = setRot();   
        movss xmm0, [M+EXP_A.tipo] ; real a ser convertido
        mov rsi, M+tempCounter; end. temporario
        mov rcx, 0 ; contador pilha
        mov rdi, 6 ; precisao 6 casas compart
        mov rbx, 10 ; divisor
        cvtsi2ss xmm2, rbx ; divisor real
        subss xmm1, xmm1 ; zera registrador
        comiss xmm0, xmm1 ; verifica sinal
        jae rot_a ; salta (rot_a) se numero positivo
        mov dl, '-' ; senao, escreve sinal –
        mov [rsi], dl
        mov rdx, -1 ; carrega -1 em RDX
        cvtsi2ss xmm1, rdx ; converte para real
        mulss xmm0, xmm1 ; Toma modulo
        add rsi, 1 ; incrementa indice\n
        
        rot_a:
        roundss xmm1, xmm0, 0b0011 ; parte inteira xmm1
        subss xmm0, xmm1 ; parte fracionaria xmm0
        cvtss2si rax, xmm1 ; convertido para int\n
        ;converte parte inteira que esta em rax\n
        
        rot_b:
        add rcx, 1 ; incrementa contador
        cdq ; estende edx:eax p/ div.
        idiv ebx ; divide edx;eax por ebx
        push dx ; empilha valor do resto
        cmp eax, 0 ; verifica se quoc. eh 0
        jne rot_b ; se nao eh 0, continua, else rot_b\n
        sub rdi, rcx ; decrementa precisao\n
        ; desempilha valores e escreve parte int\n
        
        rot_c:
        pop dx ; desempilha valor
        add dl, '0' ; transforma em caractere
        mov [rsi], dl ; escreve caractere
        add rsi, 1 ; incrementa base
        sub rcx, 1 ; decrementa contador
        cmp rcx, 0 ; verifica pilha vazia
        jne rot_c ; se nao pilha vazia, loop, else rot_c\n
        mov dl, '.' ; escreve ponto decimal
        mov [rsi], dl
        add rsi, 1 ; incrementa base\n
        ; converte parte fracionaria que esta em xmm0\n
        
        rot_d:
        cmp rdi, 0 ; verifica precisao
        jle rot_e ; terminou precisao ?, else rot_e
        mulss xmm0,xmm2  ; desloca para esquerda
        roundss xmm1,xmm0,0b0011 ; parte inteira xmm1
        subss xmm0,xmm1    ; atualiza xmm0
        cvtss2si rdx, xmm1 ; convertido para int
        add dl, '0' ; transforma em caractere
        mov [rsi], dl ; escreve caractere
        add rsi, 1 ; incrementa base
        sub rdi, 1 ; decrementa precisao
        jmp rot_d\n
        ; impressao\n
        
        rot_e:
        mov dl, 0 ; fim string, opcional
        mov [rsi], dl ; escreve caractere
        mov rdx, rsi ; calc tam str convertido
        mov rbx, M+tempCounter
        sub rdx, rbx ; tam=rsi-M-buffer.end
        mov rsi, M+tempCounter; endereco do buffer\n
        
        novoTemp(EXP_A.tipo, 0);

        ; executa interrupcao de saida. rsi e rdx ja foram calculados entao usar so as instrucoes para a chamada do kernel.

    else if EXP_A.tipo = String
        mov rsi, M+(EXP_A.tipo) ; registrador recebe endereco da string
        mov rdx, rsi ; rdx = rsi
        rot: 
        mov al, [rdx] ; registrador recebe primeiro caractere da string 
        add rdx, 1 ; incrementa rdx
        cmp al, 0 ; al == 0 ? se True, fim da string
        jne rot
        sub rdx, M+ (EXP_A.tipo) ; removendo offset (byte 0) do endereco
    
    else if EXP_A.tipo = Char
        mov rsi, M+(EXP_A.tipo) ; registrador recebe endereco da string
        mov rdx, 1 ; tamanho 1 pra char        

(46):
    mov rax, 1 ; chamada para saida
    mov rdi, 1 ; saida para tela
    syscall

(47):
    has_ln := False;

(48):
    has_ln := True;

(49):
    linebreak := novoTemp(String, 1);

    mov dl, 10 ; passa linebreak pro dl 
    mov [M+linebreak], dl ; passa o linebreak para dl
    mov rax, 1 ; chamada para saida
    mov rdi, 1 ; saida para tela
    mov rsi, M+linebreak ; saida para tela
    mov rdx, 1
    syscall

(50):
    EXP_E.tipo := Integer;

(51):
    EXP_E.tipo := Float;

(52):
    if EXP_E.tipo = Integer AND EXP_A.tipo = Float
        EXP_E.end := novoTemp(EXP_E.tipo);
        movss xmm0, [M+EXP_A.end] ; alocando valor em end. de expArgsA a registrador
        cvtss2si rax, xmm0 ; convertendo float para int64
        mov [M+EXP_E.end], eax ; alocando valor de eax a end. de expArgsE

    if EXP_E.tipo = Float AND EXP_E.tipo = Integer
        EXP_E.end := novoTemp(EXP_E.tipo)
        mov eax, [M+EXP_A.end] ; alocando valor em end. de expArgsA a registrador
        cvtsi2ss xmm0, eax ; convertendo int32 para float
        movss [M+EXP_E.end], xmm0 ; alocando valor de xmm0 a end. de expArgsE

(53):
    EXP_F.tipo := id.simbolo.tipo;
    EXP_F.end := id.simbolo.end;

(54):
    EXP_F.tipo := Char
    EXP_F.end := novoTemp(Char, 0);

    mov rax, 0 ; zerando rax;
    mov eax, [M+EXP_A2.end] ; alocando valor em end. de expArgsA2 a registrador (indice);
    add rax, M+id.simbolo.end ; indice + posicao inicial do string;
    mov bl, [rax] ; alocando valor em rax a registrador bl;
    mov [M+EXP_F.end], bl ; alocando conteudo de bl em end. de expArgsF;
    