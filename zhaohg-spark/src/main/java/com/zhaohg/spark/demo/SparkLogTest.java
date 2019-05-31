package com.zhaohg.spark.demo;

import org.apache.commons.lang.StringUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.storage.StorageLevel;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by zhaohg on 2017/3/1.
 */
public class SparkLogTest {
    private static final Pattern SPACE = Pattern.compile(" ");

    //map, filter, flatMap, sample, groupByKey, reduceByKey, union, join, cogroup, mapValues,sort,partionBy等多种操作类型，
    //Count, collect, reduce, lookup, save等多种actions

    public static void main(String[] args) {
        //master是Spark、Mesos、或者YARN 集群URL
        SparkConf sparkConf = new SparkConf().setAppName("SparkLogTest").setMaster("local");
        sparkConf.set("spark.scheduler.mode", "FAIR");

        JavaSparkContext jsc = new JavaSparkContext(sparkConf);


        jsc.close();
    }

    public static void JavaWordCountTest(JavaSparkContext spark) {

        JavaRDD<String> lines = spark.textFile("zhaohg-spark/src/main/resources/derby.log");

        JavaRDD<String> words = lines.flatMap(s -> Arrays.asList(SPACE.split(s)).iterator());

        JavaPairRDD<String, Integer> ones = words.mapToPair(s -> new Tuple2<>(s, 1));

        JavaPairRDD<String, Integer> counts = ones.reduceByKey((i1, i2) -> i1 + i2);

        List<Tuple2<String, Integer>> output = counts.collect();
        for (Tuple2<?, ?> tuple : output) {
            System.out.println(tuple._1() + ": " + tuple._2());
        }
    }

    public static void basicsTest(JavaSparkContext spark) {
        JavaRDD<String> lines = spark.textFile("zhaohg-spark/src/main/resources/derby.log");
        JavaRDD<Integer> lineLengths = lines.map(String::length);
        int totalLength = lineLengths.reduce((a, b) -> a + b);
        System.out.println("总长度：" + totalLength);
        System.out.println("存入缓存");
        lineLengths.persist(StorageLevel.MEMORY_ONLY());
    }

    /**
     * @param spark
     */
    public static void chengTest(JavaSparkContext spark) {
        /** 分发对象集合，这里以list为例 */
        JavaRDD<String> temp1 = spark.textFile("zhaohg-spark/src/main/resources/derby.log");
        JavaRDD<String> temp2 = spark.parallelize(Arrays.asList("a", "b", "c"));

        /** RDD操作 用java实现过滤器转化操作 */
        JavaRDD<String> lines = spark.parallelize(Arrays.asList("error:a", "error:b", "error:c", "error:d", "hadppy ending!"));
        //将RDD对象lines中有error的表项过滤出来，放在RDD对象errorLines中
        JavaRDD<String> errorLines = lines.filter(v1 -> v1.contains("error"));
        List<String> errorList = errorLines.collect(); //遍历过滤出来的列表项
        for (String line : errorList) System.out.println(line);

        JavaRDD<String> warningLines = lines.filter(v1 -> v1.contains("warning"));
        /**合并操作将两个RDD数据集合并为一个RDD数据集*/
        JavaRDD<String> unionLines = errorLines.union(warningLines);
        /**输出了RDD数据集unionLines的前 获取RDD数据集中的全部元素 .collect() 返回值 List<T>*/
        for (String line : unionLines.collect()) System.out.println(line);

        /**获取RDD数据集中的部分或者全部元素 获取RDD数据集中的部分元素 .take(int num)  返回值List<T> 获取RDD数据集中的前num项*/
        for (String line : unionLines.take(2)) System.out.println(line);


        JavaRDD<String> input = spark.parallelize(Arrays.asList("how are you", "I am ok", "do you love me"));
        /**将文本行的单词过滤出来，并将所有的单词保存在RDD数据集words中*/
        JavaRDD<String> words = input.flatMap(s -> Arrays.asList(SPACE.split(s)).iterator());
        words.collect().forEach(s -> System.out.println("--0--->" + s));
        JavaPairRDD<String, Integer> counts = words.mapToPair(s -> new Tuple2<>(s, 1));
        counts.collect().forEach(s -> System.out.println("--1--->" + s));
        JavaPairRDD<String, Integer> results = counts.reduceByKey((v1, v2) -> v1 + v2);
        results.collect().forEach(s -> System.out.println("--2--->" + s));
        Map<String, Integer> newMap = results.collectAsMap();

        JavaPairRDD<String, Integer> sortByKey = results.sortByKey(true);
        sortByKey.foreach(s -> System.out.println("sortByKey => " + s));

        class ContainError implements Function<String, Boolean> {
            public Boolean call(String v1) throws Exception {
                return v1.contains("error");
            }
        }

        JavaRDD<String> errorLines2 = lines.filter(new ContainError());
        for (String line : errorLines2.collect()) System.out.println(line);


        /** map()计算RDD中各值的平方*/
        JavaRDD<Integer> rdd = spark.parallelize(Arrays.asList(1, 2, 3, 4));
        JavaRDD<Integer> result = rdd.map(v1 -> v1 * v1);
        System.out.println(StringUtils.join(result.collect(), ","));

        /** filter() 去除RDD集合中值为1的元素*/
        JavaRDD<Integer> filterResults = rdd.filter(v1 -> v1 != 1);
        System.out.println(StringUtils.join(filterResults.collect(), ","));


//        RDD1.distinct()	生成一个只包含不同元素的新RDD。需要数据混洗。
//        RDD1.union(RDD2)	返回一个包含两个RDD中所有元素的RDD
//        RDD1.intersection(RDD2)	只返回两个RDD中都有的元素
//        RDD1.substr(RDD2)	返回一个只存在于第一个RDD而不存在于第二个RDD中的所有元素组成的RDD。需要数据混洗。
//        RDD1.cartesian(RDD2)	返回两个RDD数据集的笛卡尔集

        /**生成RDD集合{1,2} 和{1,2}的笛卡尔集*/
        JavaRDD<Integer> rdd1 = spark.parallelize(Arrays.asList(1, 2));
        JavaRDD<Integer> rdd2 = spark.parallelize(Arrays.asList(1, 2));
        JavaPairRDD<Integer, Integer> newRdd = rdd1.cartesian(rdd2);
        for (Tuple2<Integer, Integer> tuple : newRdd.collect()) System.out.println(tuple._1() + "->" + tuple._2());


        /**使用reduce(),可以很方便地计算出RDD中所有元素的总和，元素的个数，以及其他类型的聚合操作*/
        JavaRDD<Integer> rdd3 = spark.parallelize(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Integer sum1 = rdd3.reduce((v1, v2) -> v1 + v2);
        System.out.println(sum1.intValue());

        /**计算RDD数据集中所有元素的和：zeroValue=0;//求和时，初始值为0。*/
        JavaRDD<Integer> rdd4 = spark.parallelize(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        Integer sum2 = rdd4.fold(0, (v1, v2) -> v1 + v2);
        System.out.println(sum2);

        /** 计算RDD数据集中所有元素的积：zeroValue=1；求积时，初始值为1*/
        Integer result1 = rdd4.fold(1, (v1, v2) -> v1 * v2);
        System.out.println(result1);


        /**RDD数据集持久化在内存中*/
        JavaRDD<Integer> rdd5 = spark.parallelize(Arrays.asList(1, 2, 3, 4, 5));
        rdd5.persist(StorageLevel.MEMORY_ONLY());
        System.out.println(rdd5.count());
        System.out.println(StringUtils.join(rdd.collect(), ','));

        /**unpersist() RDD数据集持久化的RDD从缓存中移除*/
        JavaRDD<Integer> unpersist = rdd5.unpersist();
        unpersist.collect().forEach(s -> System.out.println("unpersist===>" + s));

        JavaDoubleRDD result6 = rdd5.mapToDouble(s -> (double) s * s);
        System.out.println(result6.max());


    }

    /**
     * glom 函数将每一个分区设置成一个数组
     * @param spark
     */
    public static void glomTest(JavaSparkContext spark) {
        // 测试glom函数
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        JavaRDD<Integer> rdd1 = spark.parallelize(list, 2);
        JavaRDD<List<Integer>> result = rdd1.glom();

        System.out.println("[测试 glom函数]返回结果:" + result.collect());
    }


    public static void subtractTest(JavaSparkContext spark) {
        // 测试subtract函数
        JavaRDD<Integer> rdd1 = spark.parallelize(Arrays.asList(1, 2, 3, 4, 5, 6), 2);
        JavaRDD<Integer> rdd2 = spark.parallelize(Arrays.asList(5, 6), 2);
        JavaRDD<Integer> result = rdd1.subtract(rdd2, 4);// 第二个参数表示结果RDD的分区数

        System.out.println("[测试 subtract 函数]返回结果:" + result.collect());
        System.out.println("[测试 subtract 函数]返回结果:" + result.partitions().size());
    }


    public static void sampleTest(JavaSparkContext spark) {
        // 测试sample、takeSample函数
        JavaRDD<Integer> rdd1 = spark.parallelize(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        // true 表示有放回去的抽样
        // false 表示没有放回去的抽样
        // 第二个参数为采样率 在 0->1 之间
        JavaRDD<Integer> result = rdd1.sample(false, 0.4);
        System.out.println("[测试 sample 函数]返回结果:" + result.collect());

        // 第一个参数和sample函数是相同的，第二个参数表示采样的个数
        List<Integer> result1 = rdd1.takeSample(false, 3);
        System.out.println("[测试 takeSample 函数]返回结果:");
        result1.forEach(System.out::print);
    }
}

