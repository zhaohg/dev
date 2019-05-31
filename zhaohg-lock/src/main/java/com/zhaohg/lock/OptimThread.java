package com.zhaohg.lock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by zhaohg on 2018-11-27.
 * 1、这里线程对文件写入的操作，就相当于修改表中的一条数据。
 * 2、多个线程需要对这个文件写入时，同时只能有一个线程写入该文件。
 * 3、同时只能有一个线程写入文件，就相当于与表的行锁，同一时间只允许一个线程操作。
 * 4、线程初始化时，持有相同的版本号，写入文件操作完成后，然后变更版本号。
 * 5、其他线程再次操作时，比较版本号，版本号较低的不在进行写入操作，或者抛出异常，或者再次执行该操作，这样就防止产生脏数据了。
 */
public class OptimThread extends Thread {
    
    // 文件版本号
    public int    version;
    // 文件
    public String file;
    
    public OptimThread(String name, int version, String file) {
        this.setName(name);
        this.version = version;
        this.file = file;
    }
    
    /**
     * 写入数据
     * @param file
     * @param text
     */
    public static void write(String file, String text) {
        try {
            FileWriter fw = new FileWriter(file, false);
            fw.write(text + "\r\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 读取数据
     * @param file
     * @return
     */
    public static String read(String file) {
        StringBuilder sb = new StringBuilder();
        try {
            File rFile = new File(file);
            if (!rFile.exists()) {
                rFile.createNewFile();
            }
            FileReader fr = new FileReader(rFile);
            BufferedReader br = new BufferedReader(fr);
            String r = null;
            while ((r = br.readLine()) != null) {
                sb.append(r).append("\r\n");
            }
            br.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
    
    /**
     * @param content
     */
    public static void println(String content) {
        System.out.println(content);
    }
    
    public void run() {
        // 1. 读取文件
        String text = read(file);
        println("线程" + getName() + "，文件版本号为：" + OptimLockMain.getVersion());
        println("线程" + getName() + "，版本号为：" + getVersion());
        // 2. 写入文件
        if (OptimLockMain.getVersion() == getVersion()) {
            println("线程" + getName() + "，版本号为：" + version + "，正在执行");
            // 文件操作，这里用synchronized就相当于文件锁
            // 如果是数据库，相当于表锁或者行锁
            synchronized (OptimThread.class) {
                if (OptimLockMain.getVersion() == this.version) {
                    // 写入操作
                    write(file, text);
                    println("线程" + getName() + "，版本号为：" + version + "，正在执行" + " 写入.....");
                    // 更新文件版本号
//                    OptimLockMain.updateVersion();
                    println("线程" + getName() + "，版本号更新为：" + OptimLockMain.updateVersion());
                    return;
                }
            }
        }
        // 3. 版本号不正确的线程，需要重新读取，重新执行
        println("线程" + getName() + "，文件版本号为：" + OptimLockMain.getVersion());
        println("线程" + getName() + "，版本号为：" + getVersion());
        System.err.println("线程" + getName() + "，需要重新执行。");
    }
    
    /**
     * @return
     */
    private int getVersion() {
        return this.version;
    }
    
}
