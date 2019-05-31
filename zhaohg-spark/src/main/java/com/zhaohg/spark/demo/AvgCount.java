package com.zhaohg.spark.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by zhaohg on 2017/3/3.
 */
class AvgCount implements Serializable {
    static Function2<AvgCount, Integer, AvgCount>  addAndCount = (a, x) -> {
        a.total += x;
        a.num += 1;
        return a;
    };
    static Function2<AvgCount, AvgCount, AvgCount> combine     = (a, b) -> {
        a.total += b.total;
        a.num += b.num;
        return a;
    };
    public int                                     total;
    public int                                     num;

    public AvgCount(int total, int num) {
        this.total = total;
        this.num = num;
    }

    public static void main(String args[]) {
        SparkConf conf = new SparkConf().setMaster("local").setAppName("my app");
        JavaSparkContext sc = new JavaSparkContext(conf);
        AvgCount intial = new AvgCount(0, 0);
        JavaRDD<Integer> rdd = sc.parallelize(Arrays.asList(1, 2, 3, 4, 5, 6));
        AvgCount result = rdd.aggregate(intial, addAndCount, combine);
        System.out.println(result.avg());
    }

    public double avg() {
        return total / (double) num;
    }
}