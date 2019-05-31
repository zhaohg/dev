package com.zhaohg.spark;

import org.apache.spark.Accumulator;
import org.apache.spark.AccumulatorParam;
import org.apache.spark.Partitioner;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.util.AccumulatorV2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Serializable;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * Created by zhaohg on 2018/3/8.
 */
public class SparkActionsTest {
    public static void main(String[] args) {
        //master是Spark、Mesos、或者YARN 集群URL
        SparkConf sparkConf = new SparkConf().setAppName("SparkActionsTest").setMaster("local");
        sparkConf.set("spark.scheduler.mode", "FAIR");
        
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);
        
        ReduceTest(jsc);
        ReduceByKeyTest(jsc);
        AggregateTest(jsc);
        FoldByKeyTest(jsc);
        FoldTest(jsc);
        CountByKeyTest(jsc);
        ForeachTest(jsc);
        ForeachPartitionTest(jsc);
        LookUpTest(jsc);
        SortByTest(jsc);
        SortByKeyTest1(jsc);
        SortByKeyTest2(jsc);
        
        jsc.close();
    }
    
    /**
     * 根据映射函数f，对RDD中的元素进行二元计算（满足交换律和结合律），返回计算结果。
     * reduce函数相当于对RDD中的元素进行reduceLeft函数操作，reduceLeft函数是从列表的左边往右边应用reduce函数；
     * 之后，在driver端对结果进行合并处理，因此，如果分区数量过多或者自定义函数过于复杂，对driver端的负载比较重
     * @param jsc
     */
    public static void ReduceTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 3);
        
        Integer reduceRDD = javaRDD.reduce((v1, v2) -> v1 + v2);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + reduceRDD);
    }
    
    /**
     * 该函数利用映射函数将每个K对应的V进行运算。
     * 其中参数说明如下：
     * - func：映射函数，根据需求自定义；
     * - partitioner：分区函数；
     * - numPartitions：分区数，默认的分区函数是HashPartitioner。
     * <p>
     * reduceByKey()是基于combineByKey()实现的，其中createCombiner只是简单的转化，而mergeValue和mergeCombiners相同，
     * 都是利用用户自定义函数。reduceyByKey() 相当于传统的 MapReduce，整个数据流也与 Hadoop 中的数据流基本一样。
     * 在combineByKey()中在 map 端开启 combine()，因此，reduceyByKey() 默认也在 map 端开启 combine()，
     * 这样在 shuffle 之前先通过 mapPartitions 操作进行 combine，得到 MapPartitionsRDD， 然后 shuffle 得到 ShuffledRDD，
     * 再进行 reduce（通过 aggregate + mapPartitions() 操作来实现）得到 MapPartitionsRDD。
     * @param jsc
     */
    public static void ReduceByKeyTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        
        //转化为K，V格式
        JavaPairRDD<Integer, Integer> javaPairRDD = javaRDD.mapToPair(s -> new Tuple2<>(s, 1));
        JavaPairRDD<Integer, Integer> reduceByKeyRDD = javaPairRDD.reduceByKey((v1, v2) -> v1 + v2);
        System.out.println(reduceByKeyRDD.collect());
        
        //指定numPartitions
        JavaPairRDD<Integer, Integer> reduceByKeyRDD2 = javaPairRDD.reduceByKey((v1, v2) -> v1 + v2, 2);
        System.out.println(reduceByKeyRDD2.collect());
        
        //自定义partition
        JavaPairRDD<Integer, Integer> reduceByKeyRDD4 = javaPairRDD.reduceByKey(new Partitioner() {
            @Override
            public int numPartitions() {
                return 2;
            }
            
            @Override
            public int getPartition(Object o) {
                return (o.toString()).hashCode() % numPartitions();
            }
        }, (v1, v2) -> v1 + v2);
        System.out.println(reduceByKeyRDD4.collect());
    }
    
    /**
     * 该函数用于将K对应V利用函数映射进行折叠、合并处理，其中参数zeroValue是对V进行初始化。
     * 具体参数如下：
     * - zeroValue：初始值；
     * - numPartitions：分区数，默认的分区函数是HashPartitioner；
     * - partitioner：分区函数；
     * - func：映射函数，用户自定义函数。
     * <p>
     * 从foldByKey()实现可以看出，该函数是基于combineByKey()实现的，其中createCombiner只是利用zeroValue对V进行初始化，
     * 而mergeValue和mergeCombiners相同，都是利用用户自定义函数。在这里需要注意如果实现K的V聚合操作，初始设置需要特别注意，不要改变聚合的结果。
     * @param jsc
     */
    public static void FoldByKeyTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7, 1, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        final Random rand = new Random(10);
        JavaPairRDD<Integer, String> javaPairRDD = javaRDD.mapToPair(integer -> new Tuple2<Integer, String>(integer, Integer.toString(rand.nextInt(10))));
        
        JavaPairRDD<Integer, String> foldByKeyRDD = javaPairRDD.foldByKey("X", (v1, v2) -> v1 + ":" + v2);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + foldByKeyRDD.collect());
        
        JavaPairRDD<Integer, String> foldByKeyRDD1 = javaPairRDD.foldByKey("X", 2, (v1, v2) -> v1 + ":" + v2);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + foldByKeyRDD1.collect());
        
        JavaPairRDD<Integer, String> foldByKeyRDD2 = javaPairRDD.foldByKey("X", new Partitioner() {
            @Override
            public int numPartitions() {
                return 3;
            }
            
            @Override
            public int getPartition(Object key) {
                return key.toString().hashCode() % numPartitions();
            }
        }, (v1, v2) -> v1 + ":" + v2);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + foldByKeyRDD2.collect());
    }
    
    /**
     * aggregate合并每个区分的每个元素，然后在对分区结果进行merge处理，这个函数最终返回的类型不需要和RDD中元素类型一致。
     * aggregate函数针对每个分区利用Scala集合操作aggregate，再使用comb()将之前每个分区结果聚合。
     * @param jsc
     */
    public static void AggregateTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 3);
        Integer aggregateRDD = javaRDD.aggregate(2, (v1, v2) -> v1 + v2, (v1, v2) -> v1 + v2);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + aggregateRDD);
    }
    
    
    /**
     * fold是aggregate的简化，将aggregate中的seqOp和combOp使用同一个函数op
     * 先是将zeroValue赋值给jobResult，然后针对每个分区利用op函数与zeroValue进行计算，
     * 再利用op函数将taskResult和jobResult合并计算，同时更新jobResult，最后，将jobResult的结果返回。
     * @param jsc
     */
    public static void FoldTest(JavaSparkContext jsc) {
        List<String> data = Arrays.asList("5", "1", "1", "3", "6", "2", "2");
        JavaRDD<String> javaRDD = jsc.parallelize(data, 5);
        JavaRDD<String> partitionRDD = javaRDD.mapPartitionsWithIndex((v1, v2) -> {
            LinkedList<String> linkedList = new LinkedList<String>();
            while (v2.hasNext()) {
                linkedList.add(v1 + "=" + v2.next());
            }
            return linkedList.iterator();
        }, false);
        
        System.out.println(partitionRDD.collect());
        
        String foldRDD = javaRDD.fold("0", (v1, v2) -> v1 + " - " + v2);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + foldRDD);
    }
    
    /**
     * 先是进行map操作转化为(key,1)键值对，再进行reduce聚合操作，最后利用collect函数将数据加载到driver，并转化为map类型。
     * 注意，从上述分析可以看出，countByKey操作将数据全部加载到driver端的内存，如果数据量比较大，可能出现OOM。
     * 因此，如果key数量比较多，建议进行rdd.mapValues(_ => 1L).reduceByKey(_ + _)，返回RDD[T, Long]。
     * @param jsc
     */
    public static void CountByKeyTest(JavaSparkContext jsc) {
        List<String> data = Arrays.asList("5", "1", "1", "3", "6", "2", "2");
        JavaRDD<String> javaRDD = jsc.parallelize(data, 5);
        
        JavaRDD<String> partitionRDD = javaRDD.mapPartitionsWithIndex((v1, v2) -> {
            LinkedList<String> linkedList = new LinkedList<String>();
            while (v2.hasNext()) {
                linkedList.add(v1 + "=" + v2.next());
            }
            return linkedList.iterator();
        }, false);
        
        System.out.println(partitionRDD.collect());
        JavaPairRDD<String, String> javaPairRDD = javaRDD.mapToPair(s -> new Tuple2<>(s, s));
        System.out.println(javaPairRDD.countByKey());
    }
    
    
    /**
     * 一个函数f适用于所有元素的抽样
     * foreach用于遍历RDD,将函数f应用于每一个元素。
     * @param jsc
     */
    public static void ForeachTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 3);
        javaRDD.foreach((VoidFunction<Integer>) System.out::println);
    }
    
    /**
     * 一个函数f适用于每个分区的抽样
     * foreachPartition和foreach类似，只不过是对每一个分区使用f。
     * @param jsc
     */
    public static void ForeachPartitionTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 3);
        
        //获得分区ID
        JavaRDD<String> partitionRDD = javaRDD.mapPartitionsWithIndex((v1, v2) -> {
            LinkedList<String> linkedList = new LinkedList<>();
            while (v2.hasNext()) {
                linkedList.add(v1 + "=" + v2.next());
            }
            return linkedList.iterator();
        }, false);
        System.out.println(partitionRDD.collect());
        javaRDD.foreachPartition(integerIterator -> {
            System.out.println("___________begin_______________");
            while (integerIterator.hasNext()) System.out.print(integerIterator.next() + "      ");
            System.out.println("\n___________end_________________");
        });
    }
    
    /**
     * lookup用于(K,V)类型的RDD,指定K值，返回RDD中该K对应的所有V值。
     * 如果partitioner不为空，计算key得到对应的partition，在从该partition中获得key对应的所有value；
     * 如果partitioner为空，则通过filter过滤掉其他不等于key的值，然后将其value输出。
     * @param jsc
     */
    public static void LookUpTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 3);
        JavaPairRDD<Integer, Integer> javaPairRDD = javaRDD.mapToPair(new PairFunction<Integer, Integer, Integer>() {
            int i = 0;
            
            @Override
            public Tuple2<Integer, Integer> call(Integer integer) throws Exception {
                i++;
                return new Tuple2<>(integer, i + integer);
            }
        });
        System.out.println(javaPairRDD.collect());
        System.out.println("lookup------------" + javaPairRDD.lookup(4));
    }
    
    /**
     * sortBy根据给定的f函数将RDD中的元素进行排序
     * sortBy函数的实现依赖于sortByKey函数。该函数接受三个参数，第一参数是一个函数，该函数带有泛型参数T，
     * 返回类型与RDD中的元素类型一致，主要是用keyBy函数的map转化，将每个元素转化为tuples类型的元素；
     * 第二个参数是ascending，该参数是可选参数，主要用于RDD中的元素的排序方式，默认是true，是升序；
     * 第三个参数是numPartitions，该参数也是可选参数，主要使用对排序后的RDD进行分区，默认的分区个数与排序前一致是partitions.length
     * @param jsc
     */
    public static void SortByTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 3);
        final Random random = new Random(100);
        //对RDD进行转换，每个元素有两部分组成
        JavaRDD<String> javaRDD1 = javaRDD.map(v1 -> v1.toString() + "_" + random.nextInt(100));
        System.out.println(javaRDD1.collect());
        //按RDD中每个元素的第二部分进行排序
        JavaRDD<String> resultRDD = javaRDD1.sortBy(v1 -> v1.split("_")[1], false, 3);
        System.out.println("result--------------" + resultRDD.collect());
    }
    
    /**
     * takeOrdered函数用于从RDD中，按照默认（升序）或指定排序规则，返回前num个元素。
     * 从源码分析可以看出，利用mapPartitions在每个分区里面进行分区排序，每个分区局部排序只返回num个元素，
     * 这里注意返回的mapRDDs的元素是BoundedPriorityQueue优先队列，再针对mapRDDs进行reduce函数操作，转化为数组进行全局排序
     * @param jsc
     */
    public static void TakeOrderedTest(JavaSparkContext jsc) {
        //注意comparator需要序列化
        class TakeOrderedComparator implements Serializable, Comparator<Integer> {
            @Override
            public int compare(Integer o1, Integer o2) {
                return -o1.compareTo(o2);
            }
        }
        List<Integer> data = Arrays.asList(5, 1, 0, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 3);
        System.out.println("takeOrdered-----1-------------" + javaRDD.takeOrdered(2));
        List<Integer> list = javaRDD.takeOrdered(2, new TakeOrderedComparator());
        System.out.println("takeOrdered----2--------------" + list);
    }
    
    /**
     * takeSample函数返回一个数组，在数据集中随机采样 num 个元素组成。
     * takeSample函数类似于sample函数，该函数接受三个参数，第一个参数withReplacement ，
     * 表示采样是否放回，true表示有放回的采样，false表示无放回采样；第二个参数num，表示返回的采样数据的个数，
     * 这个也是takeSample函数和sample函数的区别；第三个参数seed，表示用于指定的随机数生成器种子。另外，takeSample函数先是计算fraction，
     * 也就是采样比例，然后调用sample函数进行采样，并对采样后的数据进行collect()，最后调用take函数返回num个元素。
     * 注意，如果采样个数大于RDD的元素个数，且选择的无放回采样，则返回RDD的元素的个数。
     * @param jsc
     */
    public static void TakeSampleTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(5, 1, 0, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 3);
        System.out.println("takeSample-----1-------------" + javaRDD.takeSample(true, 2));
        System.out.println("takeSample-----2-------------" + javaRDD.takeSample(true, 2, 100));
        //返回20个元素
        System.out.println("takeSample-----3-------------" + javaRDD.takeSample(true, 20, 100));
        //返回7个元素
        System.out.println("takeSample-----4-------------" + javaRDD.takeSample(false, 20, 100));
    }
    
    /**
     * 集合的元素的抽样一个多级树模式
     * treeAggregate函数先是对每个分区利用Scala的aggregate函数进行局部聚合的操作；
     * 同时，依据depth参数计算scale，如果当分区数量过多时，则按i%curNumPartitions进行key值计算，再按key进行重新分区合并计算；
     * 最后，在进行reduce聚合操作。这样可以通过调解深度来减少reduce的开销。
     * @param jsc
     */
    public static void TreeAggregateTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 3);
        //转化操作
        JavaRDD<String> javaRDD1 = javaRDD.map(v1 -> Integer.toString(v1));
        
        String result1 = javaRDD1.treeAggregate("0", (v1, v2) -> {
            System.out.println(v1 + "=seq=" + v2);
            return v1 + "=seq=" + v2;
        }, (Function2<String, String, String>) (v1, v2) -> {
            System.out.println(v1 + "<=comb=>" + v2);
            return v1 + "<=comb=>" + v2;
        });
        System.out.println(result1);
    }
    
    
    /**
     * 减少抽样的元素在一个多层次的树模式 与treeAggregate类似，只不过是seqOp和combOp相同的treeAggregate
     * treeReduce函数先是针对每个分区利用scala的reduceLeft函数进行计算；
     * 最后，在将局部合并的RDD进行treeAggregate计算，这里的seqOp和combOp一样，初值为空。
     * 在实际应用中，可以用treeReduce来代替reduce，主要是用于单个reduce操作开销比较大，而treeReduce可以通过调整深度来控制每次reduce的规模
     * @param jsc
     */
    public static void TreeReduceTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 5);
        JavaRDD<String> javaRDD1 = javaRDD.map(v1 -> Integer.toString(v1));
        String result = javaRDD1.treeReduce((v1, v2) -> {
            System.out.println(v1 + "=" + v2);
            return v1 + "=" + v2;
        });
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + result);
    }
    
    /**
     * sortByKey() 将 RDD[(K, V)] 中的 records 按 key 排序，ascending = true 表示升序，false 表示降序。
     * 目前 sortByKey() 的数据依赖很简单，先使用 shuffle 将 records 聚集在一起（放到对应的 partition 里面），
     * 然后将 partition 内的所有 records 按 key 排序，最后得到的 MapPartitionsRDD 中的 records 就有序了。
     * 目前 sortByKey() 先使用 Array 来保存 partition 中所有的 records，再排序。
     * @param jsc
     */
    public static void SortByKeyTest1(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        final Random random = new Random(100);
        JavaPairRDD<Integer, Integer> javaPairRDD = javaRDD.mapToPair(integer -> new Tuple2<>(integer, random.nextInt(10)));
        
        JavaPairRDD<Integer, Integer> sortByKeyRDD = javaPairRDD.sortByKey();
        System.out.println(sortByKeyRDD.collect());
    }
    
    /**
     * sortByKey实现二次排序
     * 最近在项目中遇到二次排序的需求，和平常开发Spark的application一样，开始查看API，编码，调试，验证结果。
     * 由于之前对spark的API使用过，知道API中的sortByKey()可以自定义排序规则，通过实现自定义的排序规则来实现二次排序。
     * 这里为了说明问题，举了一个简单的例子,key是由两部分组成的，我们这里按key的第一部分的降序排，key的第二部分升序排，具体如下：
     * @param jsc
     */
    public static void SortByKeyTest2(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        final Random random = new Random(100);
        
        JavaPairRDD javaPairRDD = javaRDD.mapToPair(integer ->
                new Tuple2<>(Integer.toString(integer) + " " + random.nextInt(10), random.nextInt(10)));
        
        /**JavaPairRDD<String, Integer> sortByKeyRDD = javaPairRDD.sortByKey(new Comparator<String>() {
        @Override public int compare(String o1, String o2) {
        String[] o1s = o1.split(" ");
        String[] o2s = o2.split(" ");
        if (o1s[0].compareTo(o2s[0]) == 0)
        return o1s[1].compareTo(o2s[1]);
        else
        return -o1s[0].compareTo(o2s[0]);
        }
        });*/
         /*上面编码从语法上没有什么问题，可是运行报错，
         其实在OrderedRDDFunctions类中有个变量ordering它是隐形的：private val com.zhaohg.ordering = implicitly[Ordering[K]]。
         他就是默认的排序规则，我们自己重写的comp就修改了默认的排序规则。到这里还是没有发现问题，
         但是发现类OrderedRDDFunctions extends Logging with Serializable，又回到上面的报错信息，扫描到“serializable”！！！
         因此，返回上述代码，查看Comparator interface实现，发现原来是它没有extend Serializable，
         故只需创建一个 serializable的comparator 就可以：public interface SerializableComparator<T> extends Comparator<T>, Serializable { }*/
        
        
        class Comp implements Comparator<String>, Serializable {
            @Override
            public int compare(String o1, String o2) {
                String[] o1s = o1.split(" ");
                String[] o2s = o2.split(" ");
                if (o1s[0].compareTo(o2s[0]) == 0)
                    return o1s[1].compareTo(o2s[1]);
                else
                    return -o1s[0].compareTo(o2s[0]);
            }
        }
        JavaPairRDD sortByKeyRDD = javaPairRDD.sortByKey(new Comp());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + sortByKeyRDD.collect());
        /**
         * 总结下，在spark的Java API中，如果需要使用Comparator接口，须注意是否需要序列化，
         * 如sortByKey(),repartitionAndSortWithinPartitions()等都是需要序列化的
         */
    }
    
    
    /**
     * saveAsTextFile用于将RDD以文本文件的格式存储到文件系统中
     * ，saveAsTextFile函数是依赖于saveAsHadoopFile函数，由于saveAsHadoopFile函数接受PairRDD，
     * 所以在saveAsTextFile函数中利用rddToPairRDDFunctions函数转化为(NullWritable,Text)类型的RDD，
     * 然后通过saveAsHadoopFile函数实现相应的写操作。
     * @param jsc
     */
    public static void SaveAsTextFileTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 5);
        javaRDD.saveAsTextFile("out/tmp1");
    }
    
    /**
     * saveAsObjectFile用于将RDD中的元素序列化成对象，存储到文件中。
     * saveAsObjectFile函数是依赖于saveAsSequenceFile函数实现的，将RDD转化为类型为
     * @param jsc
     */
    public static void SavaAsObjectFileTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 5);
        javaRDD.saveAsObjectFile("out/tmp2");
    }
    
    /**
     * 广播变量允许程序员将一个只读的变量缓存在每台机器上，而不用在任务之间传递变量。广播变量可被用于有效地给每个节点一个大输入数据集的副本。
     * Spark还尝试使用高效地广播算法来分发变量，进而减少通信的开销。 Spark的动作通过一系列的步骤执行，这些步骤由分布式的洗牌操作分开。
     * Spark自动地广播每个步骤每个任务需要的通用数据。这些广播数据被序列化地缓存，在运行任务之前被反序列化出来。
     * 这意味着当我们需要在多个阶段的任务之间使用相同的数据，或者以反序列化形式缓存数据是十分重要的时候，显式地创建广播变量才有用。
     * @param jsc
     */
    public static void BroadcastTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 5);
        final Broadcast<List<Integer>> broadcast = jsc.broadcast(data);
        JavaRDD<Integer> result = javaRDD.map(new Function<Integer, Integer>() {
            List<Integer> iList = broadcast.value();
            
            @Override
            public Integer call(Integer v1) throws Exception {
                Integer isum = 0;
                for (Integer i : iList)
                    isum += i;
                return v1 + isum;
            }
        });
        System.out.println(result.collect());
    }
    
    /**
     * 累加器是仅仅被相关操作累加的变量，因此可以在并行中被有效地支持。它可以被用来实现计数器和sum。
     * Spark原生地只支持数字类型的累加器，开发者可以添加新类型的支持。如果创建累加器时指定了名字，可以在Spark的UI界面看到。
     * 这有利于理解每个执行阶段的进程（对于Python还不支持）。 累加器通过对一个初始化了的变量v调用SparkContext.accumulator(v)来创建。
     * 在集群上运行的任务可以通过add或者”+=”方法在累加器上进行累加操作。但是，它们不能读取它的值。只有驱动程序能够读取它的值，通过累加器的value方法
     * @param jsc
     */
    public static void AccumulatorTest(JavaSparkContext jsc) {
        class VectorAccumulatorParam implements AccumulatorParam<Vector> {
            @Override
            //合并两个累加器的值。
            //参数r1是一个累加数据集合
            //参数r2是另一个累加数据集合
            public Vector addInPlace(Vector r1, Vector r2) {
                r1.addAll(r2);
                return r1;
            }
            
            @Override
            //初始值
            public Vector zero(Vector initialValue) {
                return initialValue;
            }
            
            @Override
            //添加额外的数据到累加值中
            //参数t1是当前累加器的值
            //参数t2是被添加到累加器的值
            public Vector addAccumulator(Vector t1, Vector t2) {
                t1.addAll(t2);
                return t1;
            }
        }
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 5);
        
        final Accumulator<Integer> accumulator = jsc.accumulator(0);
        Vector initialValue = new Vector();
        for (int i = 6; i < 9; i++)
            initialValue.add(i);
        //自定义累加器
        final Accumulator accumulator1 = jsc.accumulator(initialValue, new VectorAccumulatorParam());
        JavaRDD<Integer> result = javaRDD.map((Function<Integer, Integer>) v1 -> {
            accumulator.add(1);
            Vector term = new Vector();
            term.add(v1);
            accumulator1.add(term);
            return v1;
        });
        System.out.println(result.collect());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~" + accumulator.value());
        System.out.println("~~~~~~~~~~~~~~~~~~~~~" + accumulator1.value());
    }
    
    public static void AccumulatorV2Test(JavaSparkContext jsc) {
        class MyAccumulatorV2 extends AccumulatorV2 {
            
            private Logger log = LoggerFactory.getLogger("MyAccumulatorV2");
            
            @Override
            public boolean isZero() {
                return false;
            }
            
            @Override
            public AccumulatorV2 copy() {
                return null;
            }
            
            @Override
            public void reset() {
            
            }
            
            @Override
            public void add(Object v) {
            
            }
            
            @Override
            public void merge(AccumulatorV2 other) {
            
            }
            
            @Override
            public Object value() {
                return null;
            }
        }
        
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 5);
        
        
    }
    
}
