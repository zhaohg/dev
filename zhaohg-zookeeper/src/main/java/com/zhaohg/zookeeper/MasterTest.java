package com.zhaohg.zookeeper;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

/**
 * Created by zhaohg on 2017/3/4.
 */
public class MasterTest implements Watcher {

    ZooKeeper zk;
    String    hostPort;

    MasterTest(String hostPort) {
        this.hostPort = hostPort;
    }

    public static void main(String args[]) throws Exception {
        MasterTest m = new MasterTest("127.0.0.1");
        m.startZK();
        Thread.sleep(60 * 1000); //sleep 60s
    }

    void startZK() throws IOException {
        System.out.println("Start to create the ZooKeeper instance");
        zk = new ZooKeeper(hostPort, 15000, this);
        System.out.println("Finish to create the ZooKeeper instance");
    }

    public void process(WatchedEvent e) {
        System.out.println(e);
    }
}

