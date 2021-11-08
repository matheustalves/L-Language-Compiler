global _start ; Ponto inicial do programa
_start: ; Inicio do programa
section .data ; sessao de dados
M: ; rotulo de inicio da sessao de dados
	resb 10000h ; reserva de temporarios
section .text ; sessao de codigo
section .data
	db "--- MEGA TESTE ---", 0 ; declarando valor na area de dados
section .text
	mov rsi, M+65536 ; registrador recebe endereco da string
	mov rdx, rsi ; rdx = rsi
Rot0: 
	mov [rdx], al ; registrador recebe primeiro caractere da string 
	mov bl, [rdx] ; dasdasdsad
    add bl, al ; dasdasd
	mov [end], rax ; registrador recebe primeiro caractere da string 
	mov rax, [end] ; dasdasdsad
	add [rdx], al ; registrador recebe primeiro caractere da string 
	sub bl, [rdx] ; dasdasdsad
	mov [rax], dx ; registrador recebe primeiro caractere da string 
	mov ax, [rdx] ; dasdasdsad
	mov rdx, M+ ; registrador recebe primeiro caractere da string 
	mov M+, rdx ; dasdasdsad
	mov rdx, [M+1] ; registrador recebe primeiro caractere da string 
	mov [M+2], rdx ; dasdasdsad
	mov rdx, [M+3] ; registrador recebe primeiro caractere da string 
	mov [M+3], rdx ; dasdasdsad
    syscall ; dasdas
    
