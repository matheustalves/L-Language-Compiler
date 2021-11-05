# L Language Compiler

> L Language Compiler for an imperative language made in our Compilers course.

## TODO

- Atribuição de Strings (char por char)
- write (p/ int e float)
- read
- Comparação de Strings (com loop)
- Consertar M + rax no acesso a posicao de string
- Consertar rax -> xmm no EXP_B minus (float)
- Consertar endereços de string "comidos" em writes seguidos

## ☕ Rodando o compilador.

Com um arquivo .in, digite no terminal:

```
javac Compilador.java -encoding UTF-8 && java Compilador < <arquivo>.in
```

Caso receba o aviso:
```
x linhas compiladas.
```

Seu código foi gerado em `arq.asm`. 
