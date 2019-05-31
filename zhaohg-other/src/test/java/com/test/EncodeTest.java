package com.test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

public class EncodeTest {

    public static void main(String[] args) {

        test();
    }

    private static void test() {
        String dir = "D:/fbb/myWorkSpace_DW07/FinalDB/";
        String fileName = "FinalDB2013313-jzy-v001glasscom3243ac0f-f7fb-4517-9102-b5f1934536f7";
        try {
            String encoding = codeString(dir, fileName);
            System.out.println("encoding:" + encoding);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断文件的编码格式
     * @param fileName :file
     * @return 文件编码格式
     * @throws Exception
     */
    public static String codeString(String dir, String fileName) throws Exception {

        File file = new File(dir + fileName);
        if (file == null || !file.exists()) {
            System.out.println("文件不存在..." + file.getAbsolutePath());
            return null;
        }

        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
        int p = (bin.read() << 8) + bin.read();
        String code = null;
        //其中的 0xefbb、0xfffe、0xfeff、0x5c75这些都是这个文件的前面两个字节的16进制数
        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            case 0x5c75:
                code = "ANSI|ASCII";
                break;
            default:
                code = "GBK";
        }

        return code;
    }
}
