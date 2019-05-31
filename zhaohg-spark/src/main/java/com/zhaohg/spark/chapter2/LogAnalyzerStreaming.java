package com.zhaohg.spark.chapter2;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.List;

public class LogAnalyzerStreaming {
    // Stats will be computed for the last window length of time.
    private static final Duration WINDOW_LENGTH  = new Duration(30 * 1000);
    // Stats will be computed every slide interval time.
    private static final Duration SLIDE_INTERVAL = new Duration(10 * 1000);

    public static void main(String[] args) throws InterruptedException {
        SparkConf conf = new SparkConf().setAppName("Log Analyzer Streaming");
        JavaSparkContext sc = new JavaSparkContext(conf);

        if (args.length < 2) {
            System.out.println("Must specify an server ip and port.");
            System.exit(-1);
        }

        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);

        @SuppressWarnings("resource")
        JavaStreamingContext jssc = new JavaStreamingContext(sc, SLIDE_INTERVAL);
        // This sets the update window to be every 10 seconds.

        JavaReceiverInputDStream<String> logDataDStream = jssc.socketTextStream(serverIP, serverPort);

        // A DStream of Apache Access Logs.
        JavaDStream<ApacheAccessLog> accessLogDStream = logDataDStream.map(Functions.PARSE_LOG_LINE).cache();

        // Splits the accessLogDStream into a dstream of time windowed rdd's of
        // apache access logs.
        JavaDStream<ApacheAccessLog> windowDStream = accessLogDStream.window(WINDOW_LENGTH, SLIDE_INTERVAL);

        windowDStream.foreachRDD(accessLogs -> {
            if (accessLogs.count() == 0) {
                System.out.println("No access logs in this time interval");
            }

            // *** Note that this is code copied verbatim from
            // LogAnalyzer.java.

            // Calculate statistics based on the content size.
            JavaRDD<Long> contentSizes = accessLogs.map(Functions.GET_CONTENT_SIZE).cache();
            System.out.print("Content Size Avg: "
                    + contentSizes.reduce(Functions.SUM_REDUCER) / contentSizes.count());
            System.out.print(", Min: "
                    + contentSizes.min(Functions.LONG_NATURAL_ORDER_COMPARATOR));
            System.out.println(", Max: "
                    + contentSizes.max(Functions.LONG_NATURAL_ORDER_COMPARATOR));

            // Compute Response Code to Count.
            List<Tuple2<Integer, Long>> responseCodeToCount = accessLogs
                    .mapToPair(Functions.GET_RESPONSE_CODE)
                    .reduceByKey(Functions.SUM_REDUCER).take(100);
            System.out.println("Response code counts: " + responseCodeToCount);

            // Any IPAddress that has accessed the server more than
            // 10 times.

            List<String> ipAddresses = accessLogs
                    .mapToPair(Functions.GET_IP_ADDRESS)
                    .reduceByKey(Functions.SUM_REDUCER)
                    .filter(Functions.FILTER_GREATER_10)
                    .map(Functions.GET_TUPLE_FIRST).take(100);
            System.out.println("IPAddresses > 10 times: " + ipAddresses);

            // Top Endpoints.
            List<Tuple2<String, Long>> topEndpoints = accessLogs
                    .mapToPair(Functions.GET_ENDPOINT)
                    .reduceByKey(Functions.SUM_REDUCER)
                    .top(10, new Functions.ValueComparator<>(Functions.LONG_NATURAL_ORDER_COMPARATOR));
            System.out.println("Top Endpoints: " + topEndpoints);

        });

        // Start the streaming server.
        jssc.start(); // Start the computation
        jssc.awaitTermination(); // Wait for the computation to terminate
    }
}
