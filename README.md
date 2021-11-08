# L Language Compiler

> L Language Compiler for an imperative language made in our Compilers course.

## TODO

Features:
🎉FEATURE COMPLETE🎉

Documentação:
🎉COMPLETE🎉

Bugs:
🎉BUG FREE (POR ENQUANTO)🎉

## 💻 Rodando o compilador.

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