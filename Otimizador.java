/* 
    *   Trabalho Prático - Compiladores 2021/2
    *   GRUPO 9
    *   Bernardo Cerqueira de Lima      586568
    *   Henrique Dornas Mendes          651252
    *   Matheus Teixeira Alves          636132
*/



import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class Otimizador {

    static ArrayList<String> getTokens(){
        List<String> tokenList = Arrays.asList("rsi", "rdi", "rax", "rbx", "rcx", "rdx",
                                "eax", "ebx", "ecx", "edx", "al", "bl", "cl", "dl", "ax", "bx", "cx", "dx");
        ArrayList<String> tokens = new ArrayList<String>();
        tokens.addAll(tokenList);

        return tokens;
    }

    static String matchRegisterSize(String reg){
        List<String> list64 = Arrays.asList("rsi", "rdi", "rax", "rbx", "rcx", "rdx");
        List<String> list32 = Arrays.asList("eax", "ebx", "ecx", "edx");
        List<String> list16 = Arrays.asList("al", "bl", "cl", "dl");
        List<String> list8 = Arrays.asList("ax", "bx", "cx", "dx");
        
        if(list64.contains(reg)) return "64";
        else if(list32.contains(reg)) return "32";
        else if(list16.contains(reg)) return "16";
        else if(list8.contains(reg)) return "8";
        else return "0";

    }

    static String matchElements(ArrayList<String> prevLine, ArrayList<String> currLine){
        String newLine = "";
        //ArrayList<String> lineOperator = new ArrayList<String>();
        String reg1Size, reg2Size;
        String reg1, reg2;
        String dest, source;

        reg1 = prevLine.get(1);
        dest = prevLine.get(0);
        reg2 = currLine.get(0);
        source = currLine.get(1);
        reg1Size = matchRegisterSize(reg1);
        reg2Size = matchRegisterSize(reg1);


        if(reg1Size.equals(reg2Size)&&(dest.equals(source))&&
            (!(reg1Size.equals("0")||reg2Size.equals("0")))){
            
            if(reg1.equals(reg2)){ // mesmos registradores, deleta as linhas
                newLine = "case2";
            } else{ // mesmo tamanho, faz a "ponte"
                newLine = "\tmov "+reg2+", "+reg1+" ; LOAD/STORE OTIMIZADO PEEPHOLE";
            } 
        }

        return newLine;
    }

    public static ArrayList<String> getOptimizedLines() throws Exception{
        BufferedReader br = new BufferedReader(new FileReader("peephole_test.asm"));
        String line;
        int lineIndex = 0;
        String previousLine, newLine = "";
        



        Pattern mov = Pattern.compile("\\bmov\\b");
        Matcher m;
        
        ArrayList<String> lineOperator = new ArrayList<String>();
        ArrayList<String> currLineElements = new ArrayList<String>();
        ArrayList<String> previousLineElements = new ArrayList<String>();

        try{
            while((line = br.readLine()) != null){
                currLineElements.clear();
                newLine = "";

                if(line.charAt(0) == '\t'){
                    m = mov.matcher(line);
                    if (m.find()){
                        currLineElements = new ArrayList<String>(Arrays.asList(line.split(",")));
                        currLineElements.set(0, currLineElements.get(0).replaceAll("\\bmov\\b", "").replaceAll("\\s+",""));
                        currLineElements.set(1, currLineElements.get(1).replaceAll("\\;(.*)", "").replaceAll("\\s+",""));
                        //currLineElements.add(String.valueOf(lineIndex));
                    }
                }

                lineOperator.add(line);

                //Aqui tenho os elementos das duas linhas prévias
                if(!(currLineElements.isEmpty()||previousLineElements.isEmpty())){
                    newLine = matchElements(previousLineElements, currLineElements);
                    if(!newLine.isEmpty()){
                        lineOperator.remove(lineOperator.size() - 1);
                        if(!newLine.equals("case2")){
                            lineOperator.remove(lineOperator.size() - 2);
                            lineOperator.add(newLine);                            
                        } else{
                            lineOperator.add("\t ; LINHA REDUNDANTE REMOVIDA PEEPHOLE");
                        }
                    }
                }
                previousLineElements.clear();

                if(!currLineElements.isEmpty()){
                    previousLineElements = new ArrayList<>(currLineElements);
                }

                previousLine = line;

                //lineIndex++;                             
            }
        } catch (final IOException exception) {
            exception.printStackTrace();
        }

        return lineOperator;
    }

    
    public static void main(String[] args) throws Exception {
        ArrayList<String> optimizedLines = new ArrayList<String>();
        optimizedLines = getOptimizedLines();
        //Escrever de volta no arquivo.
        System.out.print("s");
    }
}