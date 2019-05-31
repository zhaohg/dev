package com.zhaohg.spark.demo;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;

/**
 * Created by zhaohg on 2017/3/7.
 */

public class WordCountTest {

    public static void main(String[] args) {

        SparkConf conf = new SparkConf();
        conf.setMaster("local");
        conf.setAppName("test");

        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> lines = sc.textFile("/Users/zhaohg/license.log");
        JavaRDD<String> words = lines.flatMap(s -> Arrays.asList(s.split(",")).iterator());


        JavaPairRDD<String, Integer> word = words.mapToPair(s -> new Tuple2<>(s, 1));


        JavaPairRDD<String, Integer> counts = word.reduceByKey((s1, s2) -> s1 + s2);

        counts.foreach(wordCount -> System.out.println(wordCount._1 + " : " + wordCount._2));

        //将计算结果文件输出到文件系统
        counts.saveAsTextFile("/Users/zhaohg/output");

        //关闭SparkContext容器，结束本次作业
        sc.close();
    }


}