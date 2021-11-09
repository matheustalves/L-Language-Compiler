/* Exemplo de Programa na linguagem L */

int n,i1,i2,res;
float resfloat;
float f1,f2;
char op;

n<-1;
while (n<6) {
    n<-n+1;
  write("Digite o primeiro numero inteiro: ");
  readln(i1);
  write("Digite o primeiro numero inteiro: ");
  readln(i2);
  write("Digite o operador (+,-,*,/,d,m): ");
  readln(op);
  if ((op='d') || (op = '/') || (op='m' )) && (i2=0) writeln("Divisao por zero");
  else {
      
  if (op='+') res<-i1+i2;
  else if (op='-') res<-i1-i2;
  else if (op='*') res<-i1*i2;
  else if (op='/') resfloat<-i1/i2;
  else if op='d' res<-i1 div i2;
  else if op='m' res<-i1 mod i2;
  
  write("Resultado: ",res);
  }
}

n<-1;
while (n<6) {
    n<-n+1;
    write("Digite o primeiro numero float: ");
    readln(f1);
    write("Digite o primeiro numero float: ");
    readln(f2);
    write("Digite o operador (+,-,*,/,d,m): ");
    if op='+' write("Resultado: ",f1+f2);
    else if op='-' write("Resultado: ",f1-f2);
    else if op='*' write("Resultado: ",f1*f2);
    else if op='/' write("Resultado: ",f1/f2);
    else if op='d' write("Resultado: ",int(f1) div int(f2));
    else if op='m' write("Resultado: ",int(f1) mod int(f2));
  }
