package com.zhaohg.zookeeper.demo;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;


public class CreateSession implements Watcher {

    private static ZooKeeper zookeeper;

    public static void main(String[] args) throws IOException, InterruptedException {
        zookeeper = new ZooKeeper("192.168.1.105:2181", 5000, new CreateSession());
        System.out.println(zookeeper.getState());

        Thread.sleep(Integer.MAX_VALUE);
    }

    private void doSomething() {

        System.out.println("do something");
    }

    @Override
    public void process(WatchedEvent event) {
        // TODO Auto-generated method stub
        System.out.println("接收到事件：" + event);
        if (event.getState() == KeeperState.SyncConnected) {

            if (event.getType() == EventType.None && null == event.getPath()) {
                doSomething();
            }
        }
    }

}
