package com.mlwarmonitor.app;
import java.io.IOException;
import java.io.RandomAccessFile;
/**
 * Created by Administrator on 08.01.16.
 */
public class UnsignedTypes {
    private RandomAccessFile nameOfFile;
    //конструктор
    UnsignedTypes(RandomAccessFile f) {
        nameOfFile = f;
    }
    // Статическая функция которая на вход
    // принимает знаковую переменную(любого типа)
    //а на выходе возращает беззнаковый long
    //если ваш компилятор ругается не забудьте
    // превести вашу переменную к типу long явно
    // UnsignedTypes.convert((long)yourvariable)
    static public long convert(long signedvariable){
        long ans=0;
        for(int i=0;i<64;i++){
            ans+=(int)Math.pow(2,i)*(Math.abs(signedvariable % 2));
            signedvariable>>=1;
        }
        return ans;
    }
    //функция чтения WORD(unsigned short)
    public long readWord() throws IOException {
        byte[] mas = new byte[2];
        nameOfFile.readFully(mas,0,2);
        return read(mas,2);
    }
    //функция чтения DWORD(unsigned int)
    public long readDWord() throws IOException {
        byte[] mas = new byte[4];
        nameOfFile.readFully(mas,0,4);
        return read(mas, 4);
    }
    //функция используеая для работы двух верхних функций
    private static long read(byte mas[],int numberOfByte) {
        long ans = 0;
        for (int i = 0; i <numberOfByte; i++) {
            for (int j = 0; j < 8; j++) {
                //System.out.print(Math.abs(mas[i] % 2));
                ans+=(int)Math.pow(2,j+i*8)*(Math.abs(mas[i] % 2));
                mas[i] >>= 1;
            }
            //System.out.print(" ");
        }
        return ans;
    }
    //функция чтения 1байтового Char из бинарного файла
    public char readChar() throws IOException {
        char ans =0;
        byte b=nameOfFile.readByte();
        ans=(char) b;
        return ans;
    }
}
