package com.mlwarmonitor.app;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
/**
 * Created by Administrator on 08.01.16.
 */
public class VirusLibrary {
    public VirusLibrary(String pathToLibrary) {
        this(new File(pathToLibrary));
    }
    public VirusLibrary(File file) {
        mainFile = file;
    }
    File mainFile;
    boolean ready = false;
    private int numberofSegment;
    SegmentCode[] allVirus;
    public void LoadLibrary() throws FileNotFoundException {
        Scanner in = new Scanner(mainFile);
        numberofSegment = in.nextInt();
        allVirus = new SegmentCode[numberofSegment];
        for (int j = 0; j < numberofSegment; j++) {
            String name = in.next();
            int len = in.nextInt();
            byte[] mas = new byte[len];
            for (int i = 0; i < len; i++) {
                mas[i] = (byte) (in.nextInt()-128);
            }
            allVirus[j] = new SegmentCode(mas, name);
        }
        ready = true;
    }

    public void updateLibrary() {
        ready = false;
        try {
            this.LoadLibrary();
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    static public boolean KMP(byte[] mas1, byte[] mas2) {
        boolean ans = false;
        if (mas1.length > mas2.length) return ans;
        byte[] all = new byte[mas1.length + mas2.length+1];
        int id = 0;
        for (; id < mas1.length; id++)
            all[id] = mas1[id];
        all[id]=0;
        id++;
        for (; id < mas1.length+mas2.length+1; id++)
            all[id] = mas2[id-mas1.length-1];

       /* for(int i=0;i<all.length;i++){
            System.out.print(all[i]+" ");
        }
        System.out.println();*/
        int[] pref = new int[all.length];
        for(int i=0;i<pref.length;i++) pref[i]=0;
        for(int i=1;i<all.length;i++){
            int j=pref[i-1];
            while (j>0&&(all[i]!=all[j] || i==mas1.length|| j==mas1.length))
                j=pref[j-1];
            if(all[i]==all[j] && i!=mas1.length && j!=mas1.length) j++;
            pref[i]=j;
        }
        for(int i=0;i<pref.length;i++)
            if(pref[i]==mas1.length) ans=true;
        return ans;
    }
    public boolean check(BinarySearch p){
        boolean ans=false;
        if(!ready) updateLibrary();
        ArrayList<SegmentCode> dangerSegments=new ArrayList<SegmentCode>();
        if(p.isDone()) dangerSegments=p.getAllSegments();
        for(SegmentCode sign:allVirus)
            for(SegmentCode dangerSegment:dangerSegments)
                if(dangerSegment.find(sign)) ans=true;
        return ans;
    }
}
