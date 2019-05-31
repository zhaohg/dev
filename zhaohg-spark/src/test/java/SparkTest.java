import org.apache.spark.api.java.JavaSparkContext;

/**
 * spark测试
 * Created by zhaohg on 2017/3/6.
 */

public final class SparkTest {
    
    public static void main(String[] args) throws Exception {
        
        /**
         * 参数说明：
         * 1、URL：spark的Ip地址或是写成：spark：//master:7077;看配置如何配了
         * 2、给该项目起的名字
         * 3、spark的安装路径
         * 4、需要引入的包
         */
        JavaSparkContext ctx = new JavaSparkContext("spark://mac.local:7077", "SparkTest",
                "~/spark-2.1.0",
                "lib/spark-assembly-1.0.2-hadoop2.2.0.jar");
        System.out.println(ctx.appName());
    }
}