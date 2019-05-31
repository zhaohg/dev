package com.zhaohg.spark.demo;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhaohg on 2017/3/1.
 */
public class SparkSqlTest {

    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .appName("SparkSqlTest").master("local")
                .getOrCreate();

        JavaSparkContext context = JavaSparkContext.fromSparkContext(spark.sparkContext());

        test(context);
    }

    public static void test(JavaSparkContext spark) {
        JavaRDD<Integer> rdd1 = spark.parallelize(Arrays.asList(1, 2, 3, 4, 5, 6), 2);
        // 测试 map函数
        JavaRDD<Integer> result = rdd1.map(f -> f * 2);
        System.out.println("[测试 map函数]MAP返回结果:" + result.count());
        result.foreach(id -> System.out.println(id + ";"));
        System.out.println("===========================================================");

        // 测试mapPartition函数
        JavaRDD<Integer> rdd2 = spark.parallelize(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9), 3);
        System.out.println("rdd2分区大小:" + rdd2.partitions().size());
        JavaRDD<Integer> result1 = rdd2.mapPartitions(iterator -> {
            List<Integer> list = new ArrayList<>();
            int a = 0;
            while (iterator.hasNext()) {
                a += iterator.next();
            }
            list.add(a);
            return list.iterator();
        }, true);

        System.out.println("result1分区大小:" + result1.partitions());
        System.out.println("[测试mapPartition函数]mapPartition返回结果:" + result1.count());
        result1.foreach(id -> System.out.println("----->" + id));
        result1.foreach(System.out::println);
//        result1.foreach((VoidFunction<Integer>) System.out::println);

        System.out.println("===========================================================");

        //测试mapPartition函数
        JavaRDD<Tuple2<Integer, Integer>> result3 = rdd2.mapPartitions(iterator -> {
            List<Tuple2<Integer, Integer>> list = new ArrayList<>();
            int key = iterator.next();
            while (iterator.hasNext()) {
                int value = iterator.next();
                list.add(new Tuple2<>(key, value));
                key = value;
            }
            return list.iterator();
        });
        System.out.println("[测试mapPartition函数, 另一种写法]返回结果:" + result3.count());
        result3.foreach(System.out::println);
        result3.foreach((VoidFunction<Tuple2<Integer, Integer>>) System.out::println);


        System.out.println("===========================================================");
        //测试mapValues函数
        Tuple2<Integer, Integer> t1 = new Tuple2<>(1, 3);
        Tuple2<Integer, Integer> t2 = new Tuple2<>(1, 2);
        Tuple2<Integer, Integer> t3 = new Tuple2<>(1, 4);
        Tuple2<Integer, Integer> t4 = new Tuple2<>(2, 3);
        List<Tuple2<Integer, Integer>> list = new ArrayList<>();
        list.add(t1);
        list.add(t2);
        list.add(t3);
        list.add(t4);

        JavaPairRDD<Integer, Integer> rddValue = spark.parallelizePairs(list);
        JavaPairRDD<Integer, Integer> resultValue = rddValue.mapValues(v -> v * 2);
        System.out.println("[测试mapValues函数]返回结果:");
        resultValue.foreach(value -> System.out.println(value._1 + "=" + value._2()));
        System.out.println("===========================================================");


        // 测试mapPartitionsWithIndex函数
        JavaRDD<Integer> rddWith = spark.parallelize(Arrays.asList(1, 2, 3, 4, 5), 2);
        JavaRDD<String> rddResult = rddWith.mapPartitionsWithIndex((x, it) -> {
            List<String> midList = new ArrayList<>();
            int a = 0;
            while (it.hasNext()) {
                a += it.next();
            }
            midList.add(x + "|" + a);
            return midList.iterator();
        }, false);
        System.out.println("[测试mapPartitionsWithIndex函数]返回结果:" + rddResult.count());
        System.out.println(rddResult.collect());

        //测试 flatMap函数
        JavaRDD<Integer> rddFlatMap = spark.parallelize(Arrays.asList(1, 2, 3, 4), 2);
        JavaRDD<Integer> rddFlatMapResult = rddFlatMap.flatMap(integer -> {
            List<Integer> listFlat = new ArrayList<>();
            for (int i = 1; i <= integer; i++) {
                listFlat.add(i);
            }
            return listFlat.iterator();
        });
        System.out.println("[测试flatMap函数]返回结果:" + rddFlatMapResult.count());
        System.out.println(rddFlatMapResult.collect());
    }


}
