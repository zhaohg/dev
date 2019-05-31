package com.zhaohg.javaio;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FrAndFwDemo {
    public static void main(String[] args) throws IOException {
        FileReader fr = new FileReader("e:\\javaio\\zhaohg.txt");
        FileWriter fw = new FileWriter("e:\\javaio\\zhaohg2.txt");
        //FileWriter fw = new FileWriter("e:\\javaio\\zhaohg2.txt",true);
        char[] buffer = new char[2056];
        int c;
        while ((c = fr.read(buffer, 0, buffer.length)) != -1) {
            fw.write(buffer, 0, c);
            fw.flush();
        }
        fr.close();
        fw.close();
    }

}
