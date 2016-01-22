package com.mlwarmonitor.app;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Signature;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Али Баба on 13.01.2016.
 */
public class SimpleScanner {


    public class SimpleScannerFile{
        private void readUsingFileReader(String fileName) throws IOException {
            File file = new File(fileName);
            FileReader reader = new FileReader(file);
            BufferedReader bro = new BufferedReader(reader);

            String line;
            while((line = bro.readLine()) != null) {
                //out
            }
            bro.close();
            reader.close();
        }

        private void readUsingBufferedReader(String fileName, Charset c) throws IOException {
            File file = new File(fileName);
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fis, c);
            BufferedReader bro = new BufferedReader(reader);

            String line;
            while((line = bro.readLine()) != null) {
                //out
            }
            bro.close();
            reader.close();
        }

        private void readUsingBufferedReader(String fileName) throws IOException {
            File file = new File(fileName);
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(fis);
            BufferedReader bro = new BufferedReader(reader);

            String line;
            while((line = bro.readLine()) != null) {
                //out
            }
            bro.close();
            reader.close();
        }

        private void readUsingScanner(String fileName) throws IOException {
            Path path = Paths.get(fileName);
            Scanner scan = new Scanner(path);

            while(scan.hasNextLine()){
                String line = scan.nextLine();
            }
        }

        private void readUsingFiles(String fileName) throws IOException {
            Path path = Paths.get(fileName);
            byte[] bytes = Files.readAllBytes(path);
            List<String> allLines = Files.readAllLines(path, StandardCharsets.UTF_8);
        }
    }
    public static void scan(String s,String signat) throws IOException{
        File file;

        file = new File(s);
        /*run(new File(s));*/

        if (!file.isDirectory()) {
             //    numberofFile++;
            //scaning(file);
        } else {
            if (file.isDirectory()) {
                String[] temp = file.list();
                if (temp != null)
                    if (temp.length != 0)
                        for (String s1 : temp) {
                            SubStringFinder.SSFScanner(s+"\\"+s1,signat);
                            //run1(new File(file + "\\" + s));
                        }
            }
        }
    }
    static class  SubStringFinder {
        static public String Classify(String str) throws IOException
        {
            boolean flag=false;
            String stri;
            BufferedReader bro = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File("D:\\Virus\\class1.csv"))));
            try {
                StringBuilder sb = new StringBuilder();
                String line = bro.readLine();

                while (line != null) {
                    //sb.append(line);
                    //sb.append(System.lineSeparator());

                        if(line.contains(str)) {
                            int i=line.indexOf(";");
                            stri=line.substring(0,i-1);
                            return stri;
                        }
                       // else System.out.println("ПАТТЕРН НЕ НАЙДЕН"+search.length());


                    line = bro.readLine();
                    //linen++;
                }
                //String everything = sb.toString();
            } finally {
                bro.close();
            }
            return "Неизвестный вирус";
        }
        static public void SSFScanner(String fileName, String sign) throws IOException {
            int linen=1;
            BufferedReader bro = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File(sign))));

            try {
                StringBuilder sb = new StringBuilder();
                String line = bro.readLine();

                while (line != null) {
                    //sb.append(line);
                    //sb.append(System.lineSeparator());
                    Path path = Paths.get(fileName);
                    Scanner scan = new Scanner(path);

                    while(scan.hasNextLine()){
                        String search = scan.nextLine();

                        if(search.contains(line)) {
                            System.out.println("Обнаружен вирус !!!"+"\r\n"+"ЗАРАЖЕН ФАЙЛ: "+path+"В строке №:"+linen+" "+Classify(line));
                        }
                        else System.out.println("ПАТТЕРН НЕ НАЙДЕН");//+search.length());
                    }
                    line = bro.readLine();
                    linen++;
                }
                //String everything = sb.toString();
            } finally {
                bro.close();
            }
            //while((line = bro.readLine()) != null) {
                //out
            //}
        }


    }

}
