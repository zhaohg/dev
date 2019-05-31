package com.zhaohg.spark.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.Optional;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by zhaohg on 2017/3/3.
 */
public class SparkStreamingTest {
    //    # TERMINAL 1:
//    # Running Netcat
//    $ nc -lk 9999
//    hello world
    private static final Pattern SPACE = Pattern.compile(" ");

    public static void main(String[] args) throws InterruptedException {
        SparkConf conf = new SparkConf().setAppName("SparkStreamingTest").setMaster("local");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, new Duration(10000));
//        JavaStreamingContext ssc = new JavaStreamingContext(conf, Durations.seconds(1));
//        JavaStreamingContext jssc = new JavaStreamingContext("local","SparkStreamingTest", new Duration(10000));

        //todo jssc.checkpoint(".");//使用updateStateByKey()函数需要设置checkpoint
        //打开本地的端口9999
        JavaReceiverInputDStream<String> lines = jssc.socketTextStream("localhost", 9999);
        //按行输入，以空格分隔
        JavaDStream<String> words = lines.flatMap(line -> Arrays.asList(SPACE.split(line)).iterator());
        //每个单词形成pair，如（word，1）
        JavaPairDStream<String, Integer> pairs = words.mapToPair(word -> new Tuple2<>(word, 1));
        //统计并更新每个单词的历史出现次数
        JavaPairDStream<String, Integer> counts = pairs.updateStateByKey(
                (v1, v2) -> {
                    Integer newSum = v2.or(0);
                    for (Integer i : v1) {
                        newSum += i;
                    }
                    return Optional.of(newSum);
                }
        );

        counts.print();
        jssc.start();
        jssc.awaitTermination();

    }
}
