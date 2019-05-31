package com.zhaohg.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * ZooKeeper Java Api 使用样例<br>
 * @author
 */
public class ZookeeperTest implements Watcher {

    private static final int       SESSION_TIMEOUT   = 10000;
    private static final String    CONNECTION_STRING = "localhost:2181";
    private static final String    ZK_PATH           = "/zhaohg";
    private              ZooKeeper zookeeper         = null;

    private CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) {

        ZookeeperTest test = new ZookeeperTest();
        test.createConnection(CONNECTION_STRING, SESSION_TIMEOUT);
        if (test.createPath(ZK_PATH, "我是节点初始内容")) {
            System.out.println();
            System.out.println("数据内容: " + test.readData(ZK_PATH) + "\n");
            test.writeData(ZK_PATH, "更新后的数据");
            System.out.println("数据内容: " + test.readData(ZK_PATH) + "\n");
            test.deleteNode(ZK_PATH);
        }

        test.releaseConnection();
    }

    /**
     * 收到来自Server的Watcher通知后的处理。
     */
    @Override
    public void process(WatchedEvent event) {
        System.out.println("收到事件通知：" + event.getState() + "\n");
        if (KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }

    /**
     * 创建ZK连接
     * @param connectString ZK服务器地址列表
     * @param sessionTimeout Session超时时间
     */
    public void createConnection(String connectString, int sessionTimeout) {
        this.releaseConnection();
        try {
            zookeeper = new ZooKeeper(connectString, sessionTimeout, this);
            connectedSemaphore.await();
        } catch (InterruptedException e) {
            System.out.println("连接创建失败，发生 InterruptedException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("连接创建失败，发生 IOException");
            e.printStackTrace();
        }
    }

    /**
     * 读取指定节点数据内容
     * @param path 节点path
     * @return
     */
    public String readData(String path) {
        try {
            System.out.println("获取数据成功，path：" + path);
            return new String(this.zookeeper.getData(path, false, null));
        } catch (KeeperException e) {
            System.out.println("读取数据失败，发生KeeperException，path: " + path);
            e.printStackTrace();
            return "";
        } catch (InterruptedException e) {
            System.out.println("读取数据失败，发生 InterruptedException，path: " + path);
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 更新指定节点数据内容
     * @param path 节点path
     * @param data 数据内容
     * @return
     */
    public boolean writeData(String path, String data) {
        try {
            System.out.println("更新数据成功，path：" + path + ", stat: " +
                    this.zookeeper.setData(path, data.getBytes(), -1));
        } catch (KeeperException e) {
            System.out.println("更新数据失败，发生KeeperException，path: " + path);
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("更新数据失败，发生 InterruptedException，path: " + path);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除指定节点
     * @param path 节点path
     */
    public void deleteNode(String path) {
        try {
            this.zookeeper.delete(path, -1);
            System.out.println("删除节点成功，path：" + path);
        } catch (KeeperException e) {
            System.out.println("删除节点失败，发生KeeperException，path: " + path);
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("删除节点失败，发生 InterruptedException，path: " + path);
            e.printStackTrace();
        }
    }

    /**
     * 创建节点
     * @param path 节点path
     * @param data 初始数据内容
     * @return
     */
    public boolean createPath(String path, String data) {
        try {
            String zkPath = this.zookeeper.create(path, data.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
            System.out.println("节点创建成功, Path: " + zkPath + ", content: " + data);
        } catch (KeeperException e) {
            System.out.println("节点创建失败，发生KeeperException");
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("节点创建失败，发生 InterruptedException");
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 关闭ZK连接
     */
    public void releaseConnection() {
        if (this.zookeeper != null) {
            try {
                this.zookeeper.close();
            } catch (InterruptedException e) {
                // ignore
                e.printStackTrace();
            }
        }
    }

}
