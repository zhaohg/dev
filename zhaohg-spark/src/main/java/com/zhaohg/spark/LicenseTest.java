package com.zhaohg.spark;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

public class LicenseTest {
    
    public static void main(String[] args) {
        
        SparkConf sparkConf = new SparkConf().setAppName("LicenseTest").setMaster("local");
        sparkConf.set("spark.scheduler.license.logger", "license");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

//        StatisticsUser user = new  StatisticsUser();
//        user.StatisticsUser(jsc);
        JavaRDD<String> lines = jsc.textFile("zhaohg-spark/src/main/resources/license.log").cache();
        
        JavaRDD<String> mapRdd = lines.map(s -> {
            JSONObject object = JSON.parseObject(s);
            return "uuid_" + object.getString("uid") + "," +
                    "bundleId_" + object.getString("bundleId") + "," +
                    "feature_" + object.getString("feature").replace(",", "|") + "," +
                    "packageName_" + object.getString("packageName") + "," +
                    "time_" + object.getString("time") + "," +
                    "type_" + object.getString("type");
        });

//        List<String> list1 = mapRdd.collect();
//        for (String str : list1) System.out.println(str);
        
        JavaRDD<String> filterRdd = mapRdd.filter(s -> s.contains("UA_"));
        
        JavaRDD<String> flatRdd = filterRdd.flatMap(s -> Arrays.asList(s.split(",")).iterator());
        flatRdd.saveAsTextFile("out/demo3");
        JavaPairRDD<String, Integer> javaPairRDD = flatRdd.mapToPair(s -> new Tuple2<>(s, 1));
        
        JavaPairRDD<String, Integer> reduceByKeyRDD = javaPairRDD.reduceByKey((v1, v2) -> v1 + v2);
        
        List<Tuple2<String, Integer>> list2 = reduceByKeyRDD.collect();
        for (Tuple2 val : list2) System.out.println(val._1() + "----" + val._2());

//        JavaRDD<String> flatMap = mapRdd.flatMap(s -> Arrays.asList(s.split(",")).iterator());
//        System.out.println(flatMap.collect());
        
        jsc.stop();
    }
    
}