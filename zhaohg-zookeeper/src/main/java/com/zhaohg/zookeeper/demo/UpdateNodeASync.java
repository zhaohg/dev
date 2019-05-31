package com.zhaohg.zookeeper.demo;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

public class UpdateNodeASync implements Watcher {

    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        zooKeeper = new ZooKeeper("192.168.1.105:2181", 5000, new UpdateNodeASync());
        System.out.println(zooKeeper.getState().toString());


        Thread.sleep(Integer.MAX_VALUE);


    }

    private void doSomething(WatchedEvent event) {

        zooKeeper.setData("/node_6", "234".getBytes(), -1, new IStatCallback(), null);

    }

    @Override
    public void process(WatchedEvent event) {
        // TODO Auto-generated method stub

        if (event.getState() == KeeperState.SyncConnected) {
            if (event.getType() == EventType.None && null == event.getPath()) {
                doSomething(event);
            }
        }
    }

    static class IStatCallback implements AsyncCallback.StatCallback {

        @Override
        public void processResult(int rc, String path, Object ctx, Stat stat) {
            StringBuilder sb = new StringBuilder();
            sb.append("rc=" + rc).append("\n");
            sb.append("path" + path).append("\n");
            sb.append("ctx=" + ctx).append("\n");
            sb.append("Stat=" + stat).append("\n");
            System.out.println(sb.toString());

        }

    }

}
