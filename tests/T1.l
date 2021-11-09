/* Exemplo de Programa na linguagem L */

int i; char max,c1,c2<-0x00,c3<-0x00;

write("Digite um caractere: ");
readln(c1);
write("Digite um caractere: ");
readln(c2);
write("Digite um caractere: ");
readln(c3);


writeln(c1, c2, c3);
if (c1 > c2) {
    if (c1 > c3) max<-c1; 
    else max<-c3;
  } else if (c2 > c3) max<-c2; else max<-c1;
writeln("O maior caractere e' ",max);
