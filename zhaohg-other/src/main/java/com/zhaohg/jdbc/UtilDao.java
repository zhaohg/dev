package com.zhaohg.jdbc;

import java.io.IOException;
import java.util.Properties;

/**
 * 数据连接类
 * @author zhaohg
 */

public class UtilDao {
    static Properties properties = null;

    public UtilDao(String rw) {
        //读取属性文件
        properties = new Properties();
        java.io.InputStream in = null;
        if (rw.equals("R")) {
            in = this.getClass().getResourceAsStream("mysql_reder.properties");
        } else if (rw.equals("W")) {
            in = this.getClass().getResourceAsStream("mysql_write.properties");
        }
        try {
            properties.load(in);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException {
        UtilDao uW = new UtilDao("W");
        UtilDao uR = new UtilDao("R");
        Connection connR = uR.getConn();  //connectionsToHostsMap()
        Connection connW = uW.getConn();  //connectionsToHostsMap()
        connW.setAutoCommit(false);  //自动提交为False
        connR.setAutoCommit(false);  //自动提交为False
        String inSql = "insert into city(sname) values('复制')";
        String sql = "select * from city where sname = '复制'";
        Statement sW = connW.createStatement();
        Statement sR = connR.createStatement();
        try {
            sW.execute(inSql);
            connW.commit();
        } catch (Exception e) {
            connW.rollback();
            e.printStackTrace();
        }
        ResultSet r = sR.executeQuery(sql);
        int l = 0;
        while (r.next()) {
            System.out.println(r.getString("sname") + " " + r.getString("Id") + "    第：" + l + "条");
            l++;
        }
        r.close();
        sW.close();
        sR.close();
        connW.close();
        connR.close();
    }

    public Connection getConn() {
        Connection connection = null;
        try {
            Class.forName(properties.getProperty("DBDriver"));
            connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("name"), properties.getProperty("pass"));
        } catch (Exception err) {
            System.out.println("连接ConDB-->getCon()____JDBC错误!");
            err.printStackTrace();
            return null;
        }
        return connection;
    }
}