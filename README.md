# L Language Compiler

> L Language Compiler for an imperative language made in our Compilers course.

## TODO

Features:
- [ ] Atribuição de Strings (char por char)
- [ ] read(int|float)
- [ ] comando float(num)
- [ ] Comparação de Strings (com loop)

Bugs:
- [ ] Consertar M + rax no acesso a posicao de string
- [ ] Consertar endereços de string "comidos" em writes seguidos

## ☕ Rodando o compilador.

Com um arquivo .in, digite no terminal:

```
javac Compilador.java -encoding UTF-8 && java Compilador < <arquivo>.in
```

Caso receba o aviso:
```
x linhas compiladas.
```

Seu código foi gerado em `arq.asm`. Para executá-lo, siga as seguintes instruções (requer NASM):
```
nasm arq.asm -g -w-zeroing -f elf64 -o arq.o
ld arq.o -o arq
./arq
```