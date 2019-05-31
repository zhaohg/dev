//package com.zhaohg.transaction;
//
//import org.apache.rocketmq.client.exception.MQClientException;
//import org.apache.rocketmq.client.producer.SendResult;
//import org.apache.rocketmq.client.producer.TransactionListener;
//import org.apache.rocketmq.client.producer.TransactionMQProducer;
//import org.apache.rocketmq.common.message.Message;
//import org.apache.rocketmq.remoting.common.RemotingHelper;
//
//import java.io.UnsupportedEncodingException;
//
//public class TransactionProducer {
//    public static void main(String[] args) throws MQClientException, InterruptedException {
//        TransactionListener transactionCheckListener = new TransactionListenerImpl();
//        TransactionMQProducer producer = new TransactionMQProducer("please_rename_unique_group_name");
//        producer.setCheckThreadPoolMinSize(2);// 事务回查最小并发数
//        producer.setCheckThreadPoolMaxSize(2);// 事务回查最大并发数
//        producer.setCheckRequestHoldMax(2000);// 队列数
//        producer.setTransactionListener(transactionCheckListener);
//        producer.start();
//
//        String[] tags = new String[]{"TagA", "TagB", "TagC", "TagD", "TagE"};
//        TransactionExecuterImpl tranExecuter = new TransactionExecuterImpl();
//        for (int i = 0; i < 100; i++) {
//            try {
//                Message msg = new Message("TopicTest", tags[i % tags.length], "KEY" + i,
//                        ("Hello RocketMQ " + i).getBytes(RemotingHelper.DEFAULT_CHARSET));
//                SendResult sendResult = producer.sendMessageInTransaction(msg, tranExecuter, null);
//                System.out.printf("%s%n", sendResult);
//
//                Thread.sleep(10);
//            } catch (MQClientException | UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//
//        for (int i = 0; i < 100000; i++) {
//            Thread.sleep(1000);
//        }
//        producer.shutdown();
//    }
//}
