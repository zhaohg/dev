package com.zhaohg.flyweight;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Vector;

/**
 * 享元模式（Flyweight）
 * <p>
 * 享元模式的主要目的是实现对象的共享，即共享池，当系统中对象多的时候可以减少内存的开销，通常与工厂模式一起使用
 */
public class ConnectionPool {

    private static ConnectionPool instance = null;
    Connection conn = null;
    private Vector<Connection> pool;
    /*公有属性*/
    private String             url             = "jdbc:mysql://localhost:3306/demo3";
    private String             username        = "root";
    private String             password        = "root";
    private String             driverClassName = "com.mysql.jdbc.Driver";
    private int                poolSize        = 100;

    /*构造方法，做一些初始化工作*/
    private ConnectionPool() {
        pool = new Vector<>(poolSize);

        for (int i = 0; i < poolSize; i++) {
            try {
                Class.forName(driverClassName);
                conn = DriverManager.getConnection(url, username, password);
                pool.add(conn);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /* 返回连接到连接池 */
    public synchronized void release() {
        pool.add(conn);
    }

    /* 返回连接池中的一个数据库连接 */
    public synchronized Connection getConnection() {
        if (pool.size() > 0) {
            Connection conn = pool.get(0);
            pool.remove(conn);
            return conn;
        } else {
            return null;
        }
    }
}
