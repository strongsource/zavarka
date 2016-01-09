package com.mlwarmonitor.app;
import java.io.IOException;
import java.io.RandomAccessFile;
/**
 * Created by Administrator on 08.01.16.
 */
public class SegmentCode {
    //имя сегмента кода
    private String name;
    private boolean ready;
    //размер сегмента кода
    private long sizeofSegment;
    //байт массив сегмента кода
    private byte[] mas;
    private long pointer;
    private RandomAccessFile nameOfFile;
    //конструктор сегмента кода от размера его,смещение, RandomAccessFile
    //и название сегмента
    public SegmentCode(long length, long pointer, RandomAccessFile nameOfFile, String name) throws IOException {
        this.name = name;
        this.sizeofSegment = length;
        this.pointer = pointer;
        this.nameOfFile = nameOfFile;
        ready = false;
    }
    public SegmentCode(byte[] mas,String name) {
        this.name=name;
        this.mas=mas;
        this.sizeofSegment=mas.length;
        ready=true;
    }
    private void makeSegment() throws IOException {
        if(ready) return;
        nameOfFile.seek(pointer);
        mas = new byte[(int) sizeofSegment];
        nameOfFile.read(mas);
    }
    public boolean  find(SegmentCode sign){
        boolean ans=false;
        try {
            ans=this.find(sign.getmas());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ans;
    }
    public byte[] getmas(){
        return mas;
    }
    private boolean find(byte[] mass) throws IOException {
        if (!ready) makeSegment();
        return VirusLibrary.KMP(mass,mas);
    }
    public String toString(){
        if(!ready) try {
            makeSegment();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String bla= "Название сегмента="+name+" Размер сегмента"+sizeofSegment+"\n";
        return sizeofSegment+" ";
    }
}
