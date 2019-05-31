package com.zhaohg.simple;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RandomAsyncCommit {
    private final ConcurrentHashMap<MessageQueue, CachedQueue> mqCachedTable =
            new ConcurrentHashMap<MessageQueue, CachedQueue>();

    public void putMessages(final MessageQueue mq, final List<MessageExt> msgs) {
        CachedQueue cachedQueue = this.mqCachedTable.get(mq);
        if (null == cachedQueue) {
            cachedQueue = new CachedQueue();
            this.mqCachedTable.put(mq, cachedQueue);
        }
        for (MessageExt msg : msgs) {
            cachedQueue.getMsgCachedTable().put(msg.getQueueOffset(), msg);
        }
    }

    public void removeMessage(final MessageQueue mq, long offset) {
        CachedQueue cachedQueue = this.mqCachedTable.get(mq);
        if (null != cachedQueue) {
            cachedQueue.getMsgCachedTable().remove(offset);
        }
    }

    public long commitableOffset(final MessageQueue mq) {
        CachedQueue cachedQueue = this.mqCachedTable.get(mq);
        if (null != cachedQueue) {
            return cachedQueue.getMsgCachedTable().firstKey();
        }

        return -1;
    }
}
