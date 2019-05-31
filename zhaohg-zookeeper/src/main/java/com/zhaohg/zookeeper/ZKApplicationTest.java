package com.zhaohg.zookeeper;

/**
 * Created by zhaohg on 2017/3/4.
 */

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class ZKApplicationTest implements Watcher {
    private static final    int            SESSION_TIMEOUT = 3000;
    private volatile static boolean        shutdown;
    private                 ZooKeeper      zk;
    private                 CountDownLatch connectedSignal = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        final ZKApplicationTest createGroup = new ZKApplicationTest();
        String groupName = "zoo" + ThreadLocalRandom.current().nextInt();
        createGroup.connect("127.0.0.1");
        createGroup.create(groupName);
        createGroup.close();
    }

    public void connect(String hosts) throws IOException, InterruptedException {
        zk = new ZooKeeper(hosts, SESSION_TIMEOUT, this);
        connectedSignal.await();
    }

    @Override
    public void process(WatchedEvent event) { // Watcher interface
        if (event.getState() == Event.KeeperState.SyncConnected) {
            connectedSignal.countDown();
        }
    }

    public void create(String groupName) throws KeeperException, InterruptedException {
        String path = "/" + groupName;
        //The znode will be deleted upon the session is closed.
        String createdPath = zk.create(path, null/*data*/, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("Created " + createdPath);
    }

    public void close() throws InterruptedException {
        zk.close();
    }
}