package com.zhaohg.javaio;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class BrAndBwOrPwDemo {
    public static void main(String[] args) throws IOException {
        FileInputStream fileInputStream = new FileInputStream("e:\\javaio\\zhaohg.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        //对文件进行读写操作
        BufferedReader br = new BufferedReader(inputStreamReader);
        /*BufferedWriter bw = new BufferedWriter(
				new OutputStreamWriter(
						new FileOutputStream("e:\\javaio\\zhaohg3.txt")));*/
        PrintWriter pw = new PrintWriter("e:\\javaio\\zhaohg4.txt");
        //PrintWriter pw1 = new PrintWriter(outputStream,boolean autoFlush);
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);//一次读一行，并不能识别换行
			/*bw.write(line);
			//单独写出换行操作
			bw.newLine();//换行操作
			bw.flush();*/
            pw.println(line);
            pw.flush();
        }
        br.close();
        //bw.close();
        pw.close();
    }
}
