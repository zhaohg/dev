package com.zhaohg.spark.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.regression.GeneralizedLinearModel;
import org.apache.spark.mllib.regression.LabeledPoint;
import org.apache.spark.mllib.regression.LassoModel;
import org.apache.spark.mllib.regression.LassoWithSGD;
import org.apache.spark.mllib.regression.LinearRegressionModel;
import org.apache.spark.mllib.regression.LinearRegressionWithSGD;
import org.apache.spark.mllib.regression.RidgeRegressionModel;
import org.apache.spark.mllib.regression.RidgeRegressionWithSGD;
import scala.Tuple2;

import java.util.Arrays;

/**
 * Created by zhaohg on 2017/3/3.
 */
public class SparkMlLibTest {

    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf()
                .setAppName("SparkMlLibTest")
                .setMaster("local[2]");

        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        JavaRDD<String> data = sc.textFile("zhaohg-spark/src/main/resources/derby.log");
        JavaRDD<LabeledPoint> parsedData = data.map(line -> {
            String[] parts = line.split(",");
            double[] ds = Arrays.stream(parts[1].split(" "))
                    .mapToDouble(Double::parseDouble)
                    .toArray();
            return new LabeledPoint(Double.parseDouble(parts[0]), Vectors.dense(ds));
        }).cache();

        int numIterations = 100; //迭代次数
        LinearRegressionModel model = LinearRegressionWithSGD.train(parsedData.rdd(), numIterations);
        RidgeRegressionModel model1 = RidgeRegressionWithSGD.train(parsedData.rdd(), numIterations);
        LassoModel model2 = LassoWithSGD.train(parsedData.rdd(), numIterations);

        print(parsedData, model);
        print(parsedData, model1);
        print(parsedData, model2);

        //预测一条新数据方法
        double[] d = new double[]{1.0, 1.0, 2.0, 1.0, 3.0, -1.0, 1.0, -2.0};
        Vector v = Vectors.dense(d);
        System.out.println("1->" + model.predict(v));
        System.out.println("2->" + model1.predict(v));
        System.out.println("3->" + model2.predict(v));
    }

    public static void print(JavaRDD<LabeledPoint> parsedData, GeneralizedLinearModel model) {
        JavaPairRDD<Double, Double> valuesAndPreds = parsedData.mapToPair(point -> {
            double prediction = model.predict(point.features()); //用模型预测训练数据
            return new Tuple2<>(point.label(), prediction);
        });

        Double MSE = valuesAndPreds.mapToDouble((Tuple2<Double, Double> t) -> Math.pow(t._1() - t._2(), 2)).mean(); //计算预测值与实际值差值的平方值的均值
        System.out.println(model.getClass().getName() + " training Mean Squared Error = " + MSE);
    }
}
