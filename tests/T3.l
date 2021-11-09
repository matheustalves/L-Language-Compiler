/* Exemplo de Programa na linguagem L */

int n1,n2,naux,i;
const MAX=40;

i<-1;
n1<-1; n2<-1;
float aureo;
while (i<MAX){
    naux<-n2;
    n2<-n2+n1;
    n1<-naux;
    aureo<-n1/n2;
    writeln(n1,"/",n2,"=",aureo);
    i<-i+1;
}
