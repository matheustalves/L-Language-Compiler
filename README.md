# L Language Compiler

> L Language Compiler for an imperative language made in our Compilers course.

## TODO

Features:
🎉FEATURE COMPLETE🎉

Documentação:
- [ ] Escrever o ET de codegen

Melhorias:
- [ ] Remover os ifs desnecessários, deixando pro checkToken consumir o token e reportar o erro
- [ ] Otimização peephole

Bugs:
- [ ] Retorno estranho no write de loop_str.

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