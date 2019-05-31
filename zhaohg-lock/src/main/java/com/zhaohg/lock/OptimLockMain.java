package com.zhaohg.lock;

/**
 * Created by zhaohg on 2018-11-27.
 */
public class OptimLockMain {
    
    // 文件版本号
    static int    version = 1;
    // 操作文件
    static String file    = "/Users/zhaohg/lock.txt";
    
    /**
     * 获取版本号
     * @return
     */
    public static int getVersion() {
        return version;
    }
    
    /**
     * 更新版本号
     */
    public static int updateVersion() {
        version += 1;
        return version;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        for (int i = 1; i <= 10; i++) {
            new OptimThread(String.valueOf(i), getVersion(), file).start();
        }
    }
    
}