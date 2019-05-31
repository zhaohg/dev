package com.zhaohg.jvm;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * Created by zhaohg on 2017/2/7.
 */
public class FinalizationDemo {
    public static void main(String[] args) throws UnsupportedEncodingException {
        Cake c1 = new Cake(1);
        Cake c2 = new Cake(2);
        Cake c3 = new Cake(3);
        
        c2 = c3 = null;
        System.gc(); //调用Java垃圾收集器
        
        
        // 使用基本编码
        String base64encodedString = Base64.getEncoder().encodeToString("http://net.chinabyte.com/hot/319/14057319.shtml".getBytes());
        System.out.println("Base64 比那么字符串 (基本) : " + base64encodedString);
        
        // 解码
        byte[] base64decodedBytes = Base64.getDecoder().decode(base64encodedString);
        System.out.println("原始字符串: " + new String(base64decodedBytes));
        
        base64encodedString = Base64.getUrlEncoder().encodeToString("http://net.chinabyte.com/hot/319/14057319.shtml".getBytes());
        System.out.println("Base64 编码字符串 (URL) :" + base64encodedString);
    }
}

class Cake extends Object {
    private int id;
    
    public Cake(int id) {
        this.id = id;
        System.out.println("Cake com.zhaohg.Object " + id + "is created");
    }
    
    protected void finalize() throws java.lang.Throwable {
        super.finalize();
        System.out.println("Cake com.zhaohg.Object " + id + "is disposed");
    }
}