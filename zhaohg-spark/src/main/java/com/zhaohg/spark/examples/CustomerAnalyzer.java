package com.zhaohg.spark.examples;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by zhaohg on 2017/3/6.
 */
public class CustomerAnalyzer {

    public static void main(String[] args) {
        // Create a Spark Context.
        SparkConf conf = new SparkConf().setAppName("Customer Analyzer");
        JavaSparkContext sc = new JavaSparkContext(conf);

        for (String arg : args) {
            System.out.println("arg: " + arg);
        }
        // Load the text file into Spark.
        if (args.length != 4) {
            System.out.println("Must specify an input file.");
            System.out
                    .println("[Usage]: "
                            + CustomerAnalyzer.class.getSimpleName()
                            + ".jar [input_file_path] [top_customer_output_file_path] [top_time_elapsed_customer_output_file_path] [min_max_average_time_elapsed_customer_output_file_path]");
            System.exit(-1);
        }

        String logFile = args[0];
        String outputFileTopCustomers = args[1];
        String outputFileTopCustomersTimeElapsed = args[2];
        String outputFileAverageCustomersTimeElapsed = args[3];

        Configuration hadoopConf = new Configuration();
        FileSystem hdfs = null;

        try {
            hdfs = FileSystem.get(new java.net.URI("hdfs://sandbox.hortonworks.com:8020"), hadoopConf);
            Path output1 = new Path(outputFileTopCustomers);
            Path output2 = new Path(outputFileTopCustomersTimeElapsed);
            // Path output3 = new Path(outputFileAverageCustomersTimeElapsed);
            if (hdfs.exists(output1))
                hdfs.delete(output1, true);
            if (hdfs.exists(output2))
                hdfs.delete(output2, true);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        JavaRDD<String> logLines = sc.textFile(logFile);

        // Convert the customer data lines to Customer objects and com.zhaohg.cache them
        // since multiple transformations and actions will be called on that
        // data.
        JavaRDD<Customer> customers = logLines.map(
                Functions.PARSE_CUSTOMER_LINE).cache();

        /********************************************************
         * 1. Get top Gold customers with Total Amount >= 10000**
         * ******************************************************/
        JavaPairRDD<String, Long> topTenCustomersRDD = customers
                .mapToPair(Functions.GET_CUSTOMER_AMOUNT)
                .reduceByKey(Functions.SUM_REDUCER)
                .filter(Functions.FILTER_AMOUNT_GREATER_10000).sortByKey(true);
        System.out.println("Top Customers: ");

        List<Tuple2<String, Long>> topTenCustomers = topTenCustomersRDD.collect();
        for (Tuple2<String, Long> tuple : topTenCustomers) {
            System.out.println(tuple);
        }
        // write to file
        topTenCustomersRDD.saveAsTextFile(outputFileTopCustomers);

        /*********************************************************************************
         * 2. Find top 10 Customers who spent time for our site, sorted by Times
         * elapsed**
         * *******************************************************************************/

        List<Tuple2<Integer, String>> topTenCustomerTimeElapsedList = customers
                .mapToPair(new PairFunction<Customer, String, Integer>() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public Tuple2<String, Integer> call(Customer customer)
                            throws Exception {
                        return new Tuple2<String, Integer>(customer
                                .getFirstName() + " " + customer.getLastName(),
                                customer.getTimeElapsed());
                    }
                })
                .reduceByKey(new Function2<Integer, Integer, Integer>() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public Integer call(Integer v1, Integer v2)
                            throws Exception {
                        return v1 + v2;
                    }
                })
                .mapToPair(
                        new PairFunction<Tuple2<String, Integer>, Integer, String>() {
                            private static final long serialVersionUID = 305463067298325558L;

                            @Override
                            public Tuple2<Integer, String> call(
                                    Tuple2<String, Integer> t) throws Exception {
                                return new Tuple2<Integer, String>(t._2, t._1);
                            }
                        }).sortByKey(false).take(10);

        JavaPairRDD<String, Integer> topTenCustomerTimeElapsedRDD = sc
                .parallelizePairs(topTenCustomerTimeElapsedList)
                .mapToPair(
                        new PairFunction<Tuple2<Integer, String>, String, Integer>() {
                            private static final long serialVersionUID = 1751116365084634849L;

                            @Override
                            public Tuple2<String, Integer> call(
                                    Tuple2<Integer, String> t) throws Exception {
                                return new Tuple2<String, Integer>(t._2, t._1);
                            }
                        });

        System.out.println("Top 10 Time Elapsed Customers : ");

        List<Tuple2<String, Integer>> topTenTimeElapsedCustomers = topTenCustomerTimeElapsedRDD
                .take(10);
        for (Tuple2<String, Integer> tuple : topTenTimeElapsedCustomers) {
            System.out.println(tuple);
        }
        // write to file
        topTenCustomerTimeElapsedRDD
                .saveAsTextFile(outputFileTopCustomersTimeElapsed);

        /*********************************************************************************
         * 3. Find Average MIN MAX of Customers' spent time on our site**
         * *******************************************************************************/
        JavaRDD<Long> elapsedTime = customers.map(Functions.GET_CUSTOMER_ETIME)
                .cache();
        System.out
                .println(String
                        .format("Average Elapsed Time By Customers on the WebPage: %s, Min: %s, Max: %s",
                                elapsedTime.reduce(Functions.SUM_REDUCER)
                                        / elapsedTime.count(),
                                elapsedTime
                                        .min(Functions.LONG_NATURAL_ORDER_COMPARATOR),
                                elapsedTime
                                        .max(Functions.LONG_NATURAL_ORDER_COMPARATOR)));

        Path file = new Path(outputFileAverageCustomersTimeElapsed);
        try {
            if (hdfs.exists(file)) {
                hdfs.delete(file, true);
            }
            OutputStream os = hdfs.create(file);
            BufferedWriter br = new BufferedWriter(new OutputStreamWriter(os,
                    "UTF-8"));
            br.write("\nAverage Time Elapsed By customers "
                    + (elapsedTime.reduce(Functions.SUM_REDUCER) / elapsedTime
                    .count()) + " sec");
            br.write("\nMin Time Elapsed "
                    + (elapsedTime.min(Functions.LONG_NATURAL_ORDER_COMPARATOR))
                    + " sec");
            br.write("\nMax Time Elapsed "
                    + (elapsedTime.max(Functions.LONG_NATURAL_ORDER_COMPARATOR))
                    + " sec");
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sc.close();
    }

}