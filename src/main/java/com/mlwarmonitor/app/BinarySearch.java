package com.mlwarmonitor.app;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
/**
 * Created by Administrator on 08.01.16.
 */
public class BinarySearch {
    //сработал ли парсер
    private boolean Done = false;
    // обертка для нашего бинарного файла,которая обеспечивает прямой доступ
    private RandomAccessFile nameOfFile;
    //класс для работы с областями
    BinarySearch(File f) throws FileNotFoundException {
        nameOfFile = new RandomAccessFile(f, "r");
    }
    //количество сегментов
    private long numberOfSegments;
    //попытка парсинга
    public int doIt() throws IOException {
        int ans = 0;
        UnsignedTypes t = new UnsignedTypes(nameOfFile);
        if (this.Done) return 2;
        char ch1, ch2;
        ch1 = t.readChar();
        ch2 = t.readChar();
        if(!(ch1=='M' && ch2=='Z')) return 1; //если не дос заголовок,то это не ПЕ файл
        nameOfFile.seek(0x3c);                //Если это ПЕ файл то по этому адресу должно лежать смещение
        long offsetTOPEFile = t.readDWord();  //считываем смещение
        nameOfFile.seek(offsetTOPEFile);      //перемещаемся к заголовку ПЕ файла
        ch1 = t.readChar();
        ch2 = t.readChar();
        if(!(ch1=='P' && ch2=='E')) return 1;//последняя поверка на ПЕ
        nameOfFile.seek(offsetTOPEFile+6);
        numberOfSegments= t.readWord();
        nameOfFile.seek(offsetTOPEFile+248);
        for(int i=0;i<numberOfSegments;i++)
            readSegment();
        Done=true;
        return ans;
    }
    private ArrayList<SegmentCode> allSegments=new ArrayList<SegmentCode>();
    public ArrayList<SegmentCode> getAllSegments(){
        return allSegments;
    }
    private void readSegment() throws IOException {
        UnsignedTypes t= new UnsignedTypes(nameOfFile);
        String name="";
        char ch;
        for(int i=0;i<8;i++){
            ch=t.readChar();
            if(ch>21 && ch<128)
                name=name+ch;
        }
        t.readDWord();//DWORD VirtualSize;
        t.readDWord();//VirtualAdress;
        long length=t.readDWord();//   DWORD SizeOfRawData;
        long pointer=t.readDWord();    // DWORD PointerToRawData;
        t.readDWord(); //DWORD PointerToRelocations;
        t.readDWord();//DWORD PointerToLinenumbers;
        t.readWord();//WORD NumberOfRelocations;
        t.readWord();//WORD NumberOfLinenumbers;
        long x=t.readDWord();//DWORD Characteristics;
        ArrayList<Long> temp=new ArrayList<Long>();
        while(x>0){
            temp.add(x%16);
            x=x/16;
        }
        //System.out.println(name);
        if(temp.size()<8 || temp.get(1)!=2) return;
        allSegments.add(new SegmentCode(length,pointer,nameOfFile, name));
    }
    public boolean  isDone(){
        return Done;
    }


}
