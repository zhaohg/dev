package com.zhaohg.javaio;

import java.io.File;
import java.io.IOException;

public class IOUtilTest4 {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            long start = System.currentTimeMillis();
            /*IOUtil.copyFileByByte(new File("e:\\javaio\\1.mp3"), new File(
					"e:\\javaio\\2.mp3"));*/  //两万多毫秒
			/*IOUtil.copyFileByBuffer(new File("e:\\javaio\\1.mp3"), new File(
					"e:\\javaio\\3.mp3"));//一万多毫秒*/
            IOUtil.copyFile(new File("e:\\javaio\\1.mp3"), new File(
                    "e:\\javaio\\4.mp3"));//7毫秒
            long end = System.currentTimeMillis();
            System.out.println(end - start);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
