package com.zhaohg.zookeeper.demo;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class DeleteNodeASync implements Watcher {

    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        zooKeeper = new ZooKeeper("192.168.1.105:2181", 5000, new DeleteNodeASync());
        System.out.println(zooKeeper.getState().toString());


        Thread.sleep(Integer.MAX_VALUE);


    }

    private void doSomething(WatchedEvent event) {

        zooKeeper.delete("/node_6", -1, new IVoidCallback(), null);
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

    static class IVoidCallback implements AsyncCallback.VoidCallback {

        @Override
        public void processResult(int rc, String path, Object ctx) {
            StringBuilder sb = new StringBuilder();
            sb.append("rc=" + rc).append("\n");
            sb.append("path" + path).append("\n");
            sb.append("ctx=" + ctx).append("\n");
            System.out.println(sb.toString());

        }

    }

}
