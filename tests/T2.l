/* Exemplo de Programa na linguagem L */

int i<-0,j,exp,n,l<-1;

while (i<4) {
    write("Digite um expoente positivo: ");
    readln(exp);
    j<-1;
    n<-i;
    while j<=exp-1 {
        n<-n*i;
        j<-j+l;
    }
  writeln(i,"**",exp,"=",n);
  i<-1+i;
}  
