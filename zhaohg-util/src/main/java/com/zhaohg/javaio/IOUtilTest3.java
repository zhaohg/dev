package com.zhaohg.javaio;

import java.io.File;
import java.io.IOException;

public class IOUtilTest3 {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            IOUtil.copyFile(new File("e:\\javaio\\zhaohg.txt"), new File(
                    "e:\\javaio\\zhaohg1.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
