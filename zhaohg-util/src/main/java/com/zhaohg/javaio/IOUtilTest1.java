package com.zhaohg.javaio;

import java.io.IOException;

public class IOUtilTest1 {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            IOUtil.printHex("e:\\javaio\\FileUtils.java");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
