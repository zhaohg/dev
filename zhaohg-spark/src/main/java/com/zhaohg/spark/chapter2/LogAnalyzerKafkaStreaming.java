package com.zhaohg.spark.chapter2;

import kafka.serializer.StringDecoder;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author ppl
 */
public class LogAnalyzerKafkaStreaming {
    // Stats will be computed for the last window length of time.
    @SuppressWarnings("unused")
    private static final Duration WINDOW_LENGTH  = new Duration(30 * 1000);
    // Stats will be computed every slide interval time.
    private static final Duration SLIDE_INTERVAL = new Duration(10 * 1000);

    public static void main(String[] args) throws InterruptedException {
        SparkConf conf = new SparkConf().setAppName("Log Analyzer Kafka Streaming");
        JavaStreamingContext jssc = new JavaStreamingContext(conf, SLIDE_INTERVAL);

        // 命令行参数检查
        if (args.length < 2) {
            System.out.println("Usage: LogAnalyzerKafkaStreaming <brokers> <topics>\n"
                    + "  <brokers> is a list of one or more Kafka brokers\n"
                    + "  <topics> is a list of one or more kafka topics to consume from\n\n");
            System.exit(1);
        }

        String brokers = args[0];
        String topics = args[1];

        HashSet<String> topicsSet = new HashSet<String>(Arrays.asList(topics.split(",")));
        HashMap<String, String> kafkaParams = new HashMap<String, String>();
        kafkaParams.put("metadata.broker.list", brokers);

        // 创建kafka Stream
        JavaPairInputDStream<String, String> messages = KafkaUtils.createDirectStream(jssc, String.class, String.class,
                StringDecoder.class, StringDecoder.class, kafkaParams, topicsSet);

        // 获取行信息
        JavaDStream<String> lines = messages.map(Tuple2::_2);

        // Apache Access Logs DStream 处理.
        JavaDStream<ApacheAccessLog> accessLogDStream = lines.map(Functions.PARSE_LOG_LINE).cache();

        accessLogDStream.foreachRDD(accessLogs -> {
            if (accessLogs.count() == 0) {
                System.out.println("No access logs in this time interval");
            }

            // *** Note that this is code copied verbatim from
            // LogAnalyzer.java.

            // Calculate statistics based on the content size.
            JavaRDD<Long> contentSizes = accessLogs.map(Functions.GET_CONTENT_SIZE).cache();
            System.out.print("Content Size Avg: " + contentSizes.reduce(Functions.SUM_REDUCER) / contentSizes.count());
            System.out.print(", Min: " + contentSizes.min(Functions.LONG_NATURAL_ORDER_COMPARATOR));
            System.out.println(", Max: " + contentSizes.max(Functions.LONG_NATURAL_ORDER_COMPARATOR));

            // Compute Response Code to Count.
            JavaPairRDD<Integer, Long> javaPairRDD = accessLogs.mapToPair(Functions.GET_RESPONSE_CODE);
            List<Tuple2<Integer, Long>> responseCodeToCount = javaPairRDD.reduceByKey(Functions.SUM_REDUCER).take(100);
            System.out.println("Response code counts: " + responseCodeToCount);

            // Any IPAddress that has accessed the server more than
            // 10 times.

            List<String> ipAddresses = accessLogs.mapToPair(Functions.GET_IP_ADDRESS)
                    .reduceByKey(Functions.SUM_REDUCER).filter(Functions.FILTER_GREATER_10)
                    .map(Functions.GET_TUPLE_FIRST).take(100);
            System.out.println("IPAddresses > 10 times: " + ipAddresses);

            // Top Endpoints.
            List<Tuple2<String, Long>> topEndpoints = accessLogs.mapToPair(Functions.GET_ENDPOINT)
                    .reduceByKey(Functions.SUM_REDUCER)
                    .top(10, new Functions.ValueComparator<>(Functions.LONG_NATURAL_ORDER_COMPARATOR));
            System.out.println("Top Endpoints: " + topEndpoints);

        });

        // Start the streaming server.
        jssc.start(); // Start the computation
        jssc.awaitTermination(); // Wait for the computation to terminate
    }
}
