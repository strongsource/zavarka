package com.mlwarmonitor.app;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
/**
 * Created by Administrator on 08.01.16.
 */
public class FileScanner {
    public static int numberofFile = 0;
    public static int numberofPEFile = 0;
    public static int numberofNotOpenedFile = 0;
    public static int numberofVirusFile = 0;
    public static ArrayList<File> virusFiles = new ArrayList<File>();
    public static VirusLibrary virusLibrary=new VirusLibrary("C:\\1\\output.txt");
    private static int i=0;
    public static int procent=0;
    public static void scan(String s) {
        numberofFile = 0;
        numberofVirusFile = 0;
        numberofNotOpenedFile = 0;
        numberofPEFile = 0;
        virusFiles.clear();
        i=0;

        run1(new File(s));
        run(new File(s));

    }

    private static void scaning(File f) {

        i++;
        if(numberofFile!=0)
            procent=(i*100)/numberofFile;
        //Main.progressBar.setValue(ScanSystem.procent);

        //Main.label.setText(f.toString());
        try {
            BinarySearch p = new BinarySearch(f);
            if(p.doIt()!=1){
                if (virusLibrary.check(p)){
                    virusFiles.add(f);
                    numberofVirusFile++;
                }
                numberofPEFile++;
            }
        } catch (FileNotFoundException e) {
            numberofNotOpenedFile++;

        } catch (IOException e) {
            numberofNotOpenedFile++;
        }
    }

    public static void run(File file) {
        if (!file.isDirectory()) {
            //numberofFile++;
            scaning(file);
        } else {
            if (file.isDirectory()) {
                String[] temp = file.list();
                if(temp!=null)
                    if(temp.length!=0)
                        for (String s : temp) {
                            //System.out.println(s);
                            run(new File(file+"\\"+s));
                        }
            }
        }
    }
    public static void run1(File file) {
        // System.out.println(file);
        if (!file.isDirectory()) {
            numberofFile++;
            //scaning(file);
        } else {
            if (file.isDirectory()) {
                String[] temp = file.list();
                if(temp!=null)
                    if(temp.length!=0)
                        for (String s : temp) {

                            run1(new File(file + "\\" + s));
                        }
            }
        }
    }
}
