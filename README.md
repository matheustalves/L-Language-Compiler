# L Language Compiler

> L Language Compiler for an imperative language made in our Compilers course.

## TODO

Features:
- [ ] Atribuição de Strings (char por char)
- [ ] comando float(num)
- [ ] Implementar regra semântica a1 e a2, não pode imprimir Boolean (confirmarei com Alexei)

Bugs:
- [ ] Consertar M + rax no acesso a posicao de string
- [ ] Comparação com negativos tá ignorando o sinal
- [ ] Erro na codegen de && com boolean
- [ ] Floating point exception em bateria_comp.in em testes que funcionam independentemente, deve ter a ver com limpar o rax, similar ao erro do idiv que tava dando hoje

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