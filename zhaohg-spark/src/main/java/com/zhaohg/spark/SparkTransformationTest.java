package com.zhaohg.spark;

import org.apache.spark.Partitioner;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import scala.Tuple2;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by zhaohg on 2017/3/8.
 */
public class SparkTransformationTest {
    
    public static void main(String[] args) {
        //master是Spark、Mesos、或者YARN 集群URL
        SparkConf sparkConf = new SparkConf().setAppName("SparkTransformationTest").setMaster("local");
        sparkConf.set("spark.scheduler.mode", "FAIR");
        
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);
        
        MapPartitionsTest(jsc);
        MapPartitionsWithIndexTest(jsc);
        SampleTest(jsc);
        RandomSplitTest(jsc);
        UnionTest(jsc);
        IntersectionTest(jsc);
        CoalesceTest(jsc);
        RepartitionTest(jsc);
        CartesianTest(jsc);
        DistinctTest(jsc);
        AggregateTest(jsc);
        AggregateByKeyTest(jsc);
        CogroupTest(jsc);
        JoinTest(jsc);
        FullOuterJoinTest(jsc);
        LeftOuterJoin(jsc);
        RightOuterJoin(jsc);
        RepartitionAndSortWithinPartitionsTest(jsc);
        CombineByKeyTest(jsc);
        GroupByKeyTest(jsc);
        ZipPartitionsTest(jsc);
        ZipTest(jsc);
        jsc.close();
    }
    
    
    /**
     * mapPartitions函数会对每个分区依次调用分区函数处理，然后将处理的结果(若干个Iterator)生成新的RDDs。
     * mapPartitions与map类似，但是如果在映射的过程中需要频繁创建额外的对象，使用mapPartitions要比map高效的过。比如，将RDD中的所有数据通过JDBC连接写入数据库，如果使用map函数，可能要为每一个元素都创建一个connection，这样开销很大，如果使用mapPartitions，那么只需要针对每一个分区建立一个connection。
     * <p>
     * 第一个函数是基于第二个函数实现的，使用的是preservesPartitioning为false。而第二个函数我们可以指定preservesPartitioning，preservesPartitioning表示是否保留父RDD的partitioner分区信息；FlatMapFunction中的Iterator是这个rdd的一个分区的所有element组成的Iterator。
     * @param jsc
     */
    public static void MapPartitionsTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7);
        //RDD有两个分区
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 2);
        //计算每个分区的合计
        JavaRDD<Integer> mapPartitionsRDD = javaRDD.mapPartitions((FlatMapFunction<Iterator<Integer>, Integer>) s -> {
            int isum = 0;
            while (s.hasNext())
                isum += s.next();
            LinkedList<Integer> linkedList = new LinkedList<>();
            linkedList.add(isum);
            return linkedList.iterator();
        });
        
        System.out.println("mapPartitionsRDD~~~~~~~~~~~~~~~~~~~~~~" + mapPartitionsRDD.collect());
    }
    
    /**
     * mapPartitionsWithIndex与mapPartition基本相同，只是在处理函数的参数是一个二元元组，元组的第一个元素是当前处理的分区的index，
     * 元组的第二个元素是当前处理的分区元素组成的Iterator。
     * 其实mapPartition已经获得了当前处理的分区的index，只是没有传入分区处理函数，而mapPartition将其传入分区处理函数。
     * @param jsc
     */
    public static void MapPartitionsWithIndexTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7);
        //RDD有两个分区
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 2);
        //分区index、元素值、元素编号输出
        JavaRDD<String> mapPartitionsWithIndexRDD = javaRDD.mapPartitionsWithIndex((Function2<Integer, Iterator<Integer>, Iterator<String>>) (v1, v2) -> {
            LinkedList<String> linkedList = new LinkedList<>();
            int i = 0;
            while (v2.hasNext())
                linkedList.add(Integer.toString(v1) + "|" + v2.next().toString() + Integer.toString(i++));
            return linkedList.iterator();
        }, false);
        
        System.out.println("mapPartitionsWithIndexRDD~~~~~~~~~~~~~~~~~~~~~~" + mapPartitionsWithIndexRDD.collect());
    }
    
    
    /**
     * 第一函数是基于第二个实现的，在第一个函数中seed为Utils.random.nextLong；
     * 其中，withReplacement是建立不同的采样器；fraction为采样比例；seed为随机生成器的种子。
     * sample函数中，首先对fraction进行验证；再次建立PartitionwiseSampledRDD，依据withReplacement的值分别建立柏松采样器或伯努利采样器。
     * @param jsc
     */
    public static void SampleTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        //false   是伯努利分布(元素可以多次采样);0.2采样比例;100随机数生成器的种子
        JavaRDD<Integer> sampleRDD = javaRDD.sample(false, 0.2, 100);
        System.out.println("sampleRDD~~~~~~~~~~~~~~~~~~~~~~~~~~" + sampleRDD.collect());
        //true  是柏松分布;0.2采样比例;100随机数生成器的种子
        JavaRDD<Integer> sampleRDD1 = javaRDD.sample(false, 0.2, 100);
        System.out.println("sampleRDD1~~~~~~~~~~~~~~~~~~~~~~~~~~" + sampleRDD1.collect());
    }
    
    /**
     * 依据所提供的权重对该RDD进行随机划分
     * randomSPlit先是对权重数组进行0-1正则化；再利用randomSampleWithRange函数，对RDD进行划分；
     * 而在该函数中调用mapPartitionsWithIndex（上一节有具体说明），建立伯努利采样器对RDD进行划分。
     * @param jsc
     */
    public static void RandomSplitTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        double[] weights = {0.1, 0.2, 0.7};
        //依据所提供的权重对该RDD进行随机划分
        JavaRDD<Integer>[] randomSplitRDDs = javaRDD.randomSplit(weights);
        System.out.println("randomSplitRDDs of size~~~~~~~~~~~~~~" + randomSplitRDDs.length);
        int i = 0;
        for (JavaRDD<Integer> item : randomSplitRDDs)
            System.out.println(i++ + " randomSplitRDDs of item~~" + item.collect());
    }
    
    /**
     * union() 将两个 RDD 简单合并在一起，不改变 partition 里面的数据。
     * RangeDependency 实际上也是 1:1，只是为了访问 union() 后的 RDD 中的 partition 方便，保留了原始 RDD 的 range 边界。
     * @param jsc
     */
    public static void UnionTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        JavaRDD<Integer> unionRDD = javaRDD.union(javaRDD);
        System.out.println("unionRDD~~~~~~~~~~~~~~~~~~~~~~" + unionRDD.collect());
    }
    
    /**
     * 先使用 map() 将 RDD[T] 转变成 RDD[(T, null)]，这里的 T 只要不是 Array 等集合类型即可。接着，进行 a.cogroup(b)（后面会详细介绍cogroup）。
     * 之后再使用 filter() 过滤掉 [iter(groupA()), iter(groupB())] 中 groupA 或 groupB 为空的 records，得到 FilteredRDD。
     * 最后，使用 keys() 只保留 key 即可，得到 MappedRDD
     * @param jsc
     */
    public static void IntersectionTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        JavaRDD<Integer> intersectionRDD = javaRDD.intersection(javaRDD);
        System.out.println(intersectionRDD.collect());
    }
    
    /**
     * 当shuffle=false时，由于不进行shuffle，问题就变成parent RDD中哪些partition可以合并在一起，
     * 合并的过程依据设置的numPartitons中的元素个数进行合并处理。当shuffle=true时，进行shuffle操作，原理很简单，
     * 先是对partition中record进行k-v转换，其中key是由 (new Random(index)).nextInt(numPartitions)+1计算得到，
     * value为record，index 是该 partition 的索引，numPartitions 是 CoalescedRDD 中的 partition 个数，
     * 然后 shuffle 后得到 ShuffledRDD， 可以得到均分的 records，再经过复杂算法来建立 ShuffledRDD 和 CoalescedRDD 之间的数据联系，
     * 最后过滤掉 key，得到 coalesce 后的结果 MappedRDD。
     * @param jsc
     */
    public static void CoalesceTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        // shuffle默认是false
        JavaRDD<Integer> coalesceRDD = javaRDD.coalesce(2);
        System.out.println(coalesceRDD);
        
        JavaRDD<Integer> coalesceRDD1 = javaRDD.coalesce(2, true);
        System.out.println(coalesceRDD1);
    }
    
    
    /**
     * coalesce() 可以将 parent RDD 的 partition 个数进行调整，比如从 5 个减少到 3 个，或者从 5 个增加到 10 个。
     * 需要注意的是当 shuffle = false 的时候，是不能增加 partition 个数的（即不能从 5 个变为 10 个）。
     * 特别需要说明的是，如果使用repartition对RDD的partition数目进行缩减操作，可以使用coalesce函数，将shuffle设置为false，
     * 避免shuffle过程，提高效率。
     * repartition等价于 coalesce(numPartitions, shuffle = true)
     * @param jsc
     */
    public static void RepartitionTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        //等价于 coalesce(numPartitions, shuffle = true)
        JavaRDD<Integer> repartitionRDD = javaRDD.repartition(2);
        System.out.println(repartitionRDD);
    }
    
    /**
     * Cartesian 对两个 RDD 做笛卡尔集，生成的 CartesianRDD 中 partition 个数 = partitionNum(RDD a) * partitionNum(RDD b)。
     * 从getDependencies分析可知，这里的依赖关系与前面的不太一样，CartesianRDD中每个partition依赖两个parent RDD，
     * 而且其中每个 partition 完全依赖(NarrowDependency) RDD a 中一个 partition，同时又完全依赖(NarrowDependency) RDD b 中另一个 partition。
     * 具体如下CartesianRDD 中的 partiton i 依赖于(RDD a).List(i / numPartitionsInRDDb) 和 (RDD b).List(i %numPartitionsInRDDb)。
     * @param jsc
     */
    public static void CartesianTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        
        JavaPairRDD<Integer, Integer> cartesianRDD = javaRDD.cartesian(javaRDD);
        System.out.println(cartesianRDD.collect());
    }
    
    /**
     * 第一个函数是基于第二函数实现的，只是numPartitions默认为partitions.length，partitions为parent RDD的分区。
     * distinct() 功能是 deduplicate RDD 中的所有的重复数据。由于重复数据可能分散在不同的 partition 里面，
     * 因此需要 shuffle 来进行 aggregate 后再去重。然而，shuffle 要求数据类型是
     * @param jsc
     */
    public static void DistinctTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7, 1, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        
        JavaRDD<Integer> distinctRDD1 = javaRDD.distinct();
        System.out.println(distinctRDD1.collect());
        JavaRDD<Integer> distinctRDD2 = javaRDD.distinct(2);
        System.out.println(distinctRDD2.collect());
    }
    
    /**
     * aggregate函数将每个分区里面的元素进行聚合，然后用combine函数将每个分区的结果和初始值(zeroValue)进行combine操作。
     * 这个函数最终返回U的类型不需要和RDD的T中元素类型一致。 这样，我们需要一个函数将T中元素合并到U中，另一个函数将两个U进行合并。
     * 其中，参数1是初值元素；参数2是seq函数是与初值进行比较；参数3是comb函数是进行合并 。
     * 注意：如果没有指定分区，aggregate是计算每个分区的，空值则用初始值替换。
     * @param jsc
     */
    public static void AggregateTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 3);
        Integer aggregateValue = javaRDD.aggregate(3, (v1, v2) -> {
            System.out.println("seq~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + v1 + "," + v2);
            return Math.max(v1, v2);
        }, new Function2<Integer, Integer, Integer>() {
            int i = 0;
            
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                System.out.println("comb~~~~~~~~~i~~~~~~~~~~~~~~~~~~~" + i++);
                System.out.println("comb~~~~~~~~~v1~~~~~~~~~~~~~~~~~~~" + v1);
                System.out.println("comb~~~~~~~~~v2~~~~~~~~~~~~~~~~~~~" + v2);
                return v1 + v2;
            }
        });
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + aggregateValue);
    }
    
    /**
     * aggregateByKey函数对PairRDD中相同Key的值进行聚合操作，在聚合过程中同样使用了一个中立的初始值。和aggregate函数类似，aggregateByKey返回值的类型不需要和RDD中value的类型一致。因为aggregateByKey是对相同Key中的值进行聚合操作，所以aggregateByKey函数最终返回的类型还是Pair RDD，对应的结果是Key和聚合好的值；而aggregate函数直接是返回非RDD的结果，这点需要注意。在实现过程中，定义了三个aggregateByKey函数原型，但最终调用的aggregateByKey函数都一致。其中，参数zeroValue代表做比较的初始值；参数partitioner代表分区函数；参数seq代表与初始值比较的函数；参数comb是进行合并的方法。
     * @param jsc
     */
    public static void AggregateByKeyTest(JavaSparkContext jsc) {
        //将这个测试程序拿文字做一下描述就是：在data数据集中，按key将value进行分组合并，
        //合并时在seq函数与指定的初始值进行比较，保留大的值；然后在comb中来处理合并的方式。
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        int numPartitions = 4;
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        final Random random = new Random(100);
        JavaPairRDD<Integer, Integer> javaPairRDD = javaRDD.mapToPair(integer -> new Tuple2<>(integer, random.nextInt(10)));
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + javaPairRDD.collect());
        
        JavaPairRDD<Integer, Integer> aggregateByKeyRDD = javaPairRDD.aggregateByKey(3, (v1, v2) -> {
            System.out.println("seq~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + v1 + "," + v2);
            return Math.max(v1, v2);
        }, new Function2<Integer, Integer, Integer>() {
            int i = 0;
            
            @Override
            public Integer call(Integer v1, Integer v2) throws Exception {
                System.out.println("comb~~~~~~~~~i~~~~~~~~~~~~~~~~~~~" + i++);
                System.out.println("comb~~~~~~~~~v1~~~~~~~~~~~~~~~~~~~" + v1);
                System.out.println("comb~~~~~~~~~v2~~~~~~~~~~~~~~~~~~~" + v2);
                return v1 + v2;
            }
        });
        System.out.println("aggregateByKeyRDD.partitions().size()~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + aggregateByKeyRDD.partitions().size());
        System.out.println("aggregateByKeyRDD~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + aggregateByKeyRDD.collect());
    }
    
    /**
     * cogroup() 的计算结果放在 CoGroupedRDD 中哪个 partition 是由用户设置的 partitioner 确定的（默认是 HashPartitioner）。
     * CoGroupedRDD 依赖的所有 RDD 放进数组 rdds[RDD] 中。再次，foreach i，
     * 如果 CoGroupedRDD 和 rdds(i) 对应的 RDD 是 OneToOneDependency 关系，那么 Dependecy[i] = new OneToOneDependency(rdd)，
     * 否则 = new ShuffleDependency(rdd)。最后，返回与每个 parent RDD 的依赖关系数组 deps[Dependency]。
     * Dependency 类中的 getParents(partition id) 负责给出某个 partition 按照该 dependency 所依赖的 parent RDD 中的 partitions: List[Int]。
     * getPartitions() 负责给出 RDD 中有多少个 partition，以及每个 partition 如何序列化。
     * @param jsc
     */
    public static void CogroupTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7, 1, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        
        JavaPairRDD<Integer, Integer> javaPairRDD = javaRDD.mapToPair(integer -> new Tuple2<>(integer, 1));
        
        //与 groupByKey() 不同，cogroup() 要 aggregate 两个或两个以上的 RDD。
        JavaPairRDD<Integer, Tuple2<Iterable<Integer>, Iterable<Integer>>> cogroupRDD = javaPairRDD.cogroup(javaPairRDD);
        System.out.println(cogroupRDD.collect());
        
        JavaPairRDD<Integer, Tuple2<Iterable<Integer>, Iterable<Integer>>> cogroupRDD3 = javaPairRDD.cogroup(javaPairRDD, new Partitioner() {
            @Override
            public int numPartitions() {
                return 2;
            }
            
            @Override
            public int getPartition(Object key) {
                return (key.toString()).hashCode() % numPartitions();
            }
        });
        System.out.println(cogroupRDD3);
    }
    
    /**
     * Return an RDD containing all pairs of elements with matching keys in `this` and `other`.
     * Each pair of elements will be returned as a (k, (v1, v2)) tuple, where (k, v1) is in `this` and (k, v2) is in `other`.
     * Performs a hash join across the cluster.
     * join() 将两个 RDD[(K, V)] 按照 SQL 中的 join 方式聚合在一起。与 intersection() 类似，首先进行 cogroup()， 得到
     * @param jsc
     */
    public static void JoinTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7);
        final Random random = new Random();
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        JavaPairRDD<Integer, Integer> javaPairRDD = javaRDD.mapToPair(integer -> new Tuple2<>(integer, random.nextInt(10)));
        
        JavaPairRDD<Integer, Tuple2<Integer, Integer>> joinRDD = javaPairRDD.join(javaPairRDD);
        System.out.println(joinRDD.collect());
        
        JavaPairRDD<Integer, Tuple2<Integer, Integer>> joinRDD2 = javaPairRDD.join(javaPairRDD, 2);
        System.out.println(joinRDD2.collect());
        
        JavaPairRDD<Integer, Tuple2<Integer, Integer>> joinRDD3 = javaPairRDD.join(javaPairRDD, new Partitioner() {
            @Override
            public int numPartitions() {
                return 2;
            }
            
            @Override
            public int getPartition(Object key) {
                return (key.toString()).hashCode() % numPartitions();
            }
        });
        System.out.println(joinRDD3.collect());
    }
    
    
    /**
     * Perform a full outer join of `this` and `other`. For each element (k, v) in `this`,
     * the resulting RDD will either contain all pairs (k, (Some(v), Some(w))) for w in `other`,
     * or the pair (k, (Some(v), None)) if no elements in `other` have key k. Similarly,
     * for each element (k, w) in `other`, the resulting RDD will either contain all pairs (k, (Some(v), Some(w)))
     * for v in `this`, or the pair (k, (None, Some(w))) if no elements in `this` have key k.
     * Uses the given Partitioner to partition the output RDD.
     * fullOuterJoin() 与 join() 类似，首先进行 cogroup()，得到 <K, (Iterable[V1], Iterable[V2])> 类型的 MappedValuesRDD，
     * 然后对 Iterable[V1] 和 Iterable[V2] 做笛卡尔集，注意在V1，V2中添加了None，并将集合 flat() 化。
     * @param jsc
     */
    public static void FullOuterJoinTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7);
        final Random random = new Random();
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        JavaPairRDD<Integer, Integer> javaPairRDD = javaRDD.mapToPair(integer -> new Tuple2<>(integer, random.nextInt(10)));

//        //全关联
//        JavaPairRDD<Integer, Tuple2<Optional<Integer>, Optional<Integer>>> fullJoinRDD = javaPairRDD.fullOuterJoin(javaPairRDD);
//        System.out.println(fullJoinRDD);
//
//        JavaPairRDD<Integer, Tuple2<Optional<Integer>, Optional<Integer>>> fullJoinRDD1 = javaPairRDD.fullOuterJoin(javaPairRDD, 2);
//        System.out.println(fullJoinRDD1);
//
//        JavaPairRDD<Integer, Tuple2<Optional<Integer>, Optional<Integer>>> fullJoinRDD2 = javaPairRDD.fullOuterJoin(javaPairRDD, new Partitioner() {
//            @Override
//            public int numPartitions() {
//                return 2;
//            }
//
//            @Override
//            public int getPartition(com.zhaohg.Object key) {
//                return (key.toString()).hashCode() % numPartitions();
//            }
//        });
//        System.out.println(fullJoinRDD2);
    }
    
    /**
     * leftOuterJoin() 与 fullOuterJoin() 类似，首先进行 cogroup()，
     * 得到 <K, (Iterable[V1], Iterable[V2])>类型的 MappedValuesRDD，
     * 然后对 Iterable[V1] 和 Iterable[V2] 做笛卡尔集，注意在V1中添加了None，并将集合 flat() 化。
     * @param jsc
     */
    public static void LeftOuterJoin(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7);
        final Random random = new Random();
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        JavaPairRDD<Integer, Integer> javaPairRDD = javaRDD.mapToPair(integer -> new Tuple2<>(integer, random.nextInt(10)));
        
        //左关联
//        JavaPairRDD<Integer,Tuple2<Integer,Optional<Integer>>> leftJoinRDD = javaPairRDD.leftOuterJoin(javaPairRDD);
//        System.out.println(leftJoinRDD);
//
//        JavaPairRDD<Integer,Tuple2<Integer,Optional<Integer>>> leftJoinRDD1 = javaPairRDD.leftOuterJoin(javaPairRDD,2);
//        System.out.println(leftJoinRDD1);
//
//        JavaPairRDD<Integer,Tuple2<Integer,Optional<Integer>>> leftJoinRDD2 = javaPairRDD.leftOuterJoin(javaPairRDD, new Partitioner() {
//            @Override
//            public int numPartitions() {        return 2;    }
//            @Override
//            public int getPartition(com.zhaohg.Object key) { return (key.toString()).hashCode()%numPartitions();
//            }
//        });
//        System.out.println(leftJoinRDD2);
    }
    
    /**
     * @param jsc
     */
    public static void RightOuterJoin(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7);
        final Random random = new Random();
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        JavaPairRDD<Integer, Integer> javaPairRDD = javaRDD.mapToPair(integer -> new Tuple2<>(integer, random.nextInt(10)));
        
        //右关联
//        JavaPairRDD<Integer,Tuple2<Optional<Integer>,Integer>> rightJoinRDD = javaPairRDD.rightOuterJoin(javaPairRDD);
//        System.out.println(rightJoinRDD);
//
//        JavaPairRDD<Integer,Tuple2<Optional<Integer>,Integer>> rightJoinRDD1 = javaPairRDD.rightOuterJoin(javaPairRDD,2);
//        System.out.println(rightJoinRDD1);
//
//        JavaPairRDD<Integer,Tuple2<Optional<Integer>,Integer>> rightJoinRDD2 = javaPairRDD.rightOuterJoin(javaPairRDD, new Partitioner() {
//            @Override
//            public int numPartitions() {        return 2;    }
//            @Override
//            public int getPartition(com.zhaohg.Object key) { return (key.toString()).hashCode()%numPartitions();    }
//        });
//        System.out.println(rightJoinRDD2);
    }
    
    /**
     * 该方法依据partitioner对RDD进行分区，并且在每个结果分区中按key进行排序；
     * 通过对比sortByKey发现，这种方式比先分区，然后在每个分区中进行排序效率高，这是因为它可以将排序融入到shuffle阶段。
     * @param jsc
     */
    public static void RepartitionAndSortWithinPartitionsTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        final Random random = new Random();
        JavaPairRDD<Integer, Integer> javaPairRDD = javaRDD.mapToPair(integer -> new Tuple2<>(integer, random.nextInt(10)));
        
        JavaPairRDD<Integer, Integer> RepartitionAndSortWithPartitionsRDD = javaPairRDD.repartitionAndSortWithinPartitions(new Partitioner() {
            @Override
            public int numPartitions() {
                return 2;
            }
            
            @Override
            public int getPartition(Object key) {
                return key.toString().hashCode() % numPartitions();
            }
        });
        System.out.println(RepartitionAndSortWithPartitionsRDD.collect());
    }
    
    /**
     * 该函数是用于将RDD[k,v]转化为RDD[k,c]，其中类型v和类型c可以相同也可以不同。
     * 其中的参数如下：
     * - createCombiner：该函数用于将输入参数RDD[k,v]的类型V转化为输出参数RDD[k,c]中类型C;
     * - mergeValue：合并函数，用于将输入中的类型C的值和类型V的值进行合并，得到类型C，输入参数是（C，V），输出是C；
     * - mergeCombiners：合并函数，用于将两个类型C的值合并成一个类型C，输入参数是（C，C），输出是C；
     * - numPartitions：默认HashPartitioner中partition的个数；
     * - partitioner：分区函数，默认是HashPartitionner；
     * - mapSideCombine：该函数用于判断是否需要在map进行combine操作，类似于MapReduce中的combine，默认是 true
     * <p>
     * combineByKey()的实现是一边进行aggregate，一边进行compute() 的基础操作。
     * 假设一组具有相同 K 的 <K, V> records 正在一个个流向 combineByKey()，createCombiner
     * 将第一个 record 的 value 初始化为 c （比如，c = value），然后从第二个 record 开始，
     * 来一个 record 就使用 mergeValue(c, record.value) 来更新 c，比如想要对这些 records 的所有 values 做 sum，
     * 那么使用 c = c + record.value。等到 records 全部被 mergeValue()，得到结果 c。
     * 假设还有一组 records（key 与前面那组的 key 均相同）一个个到来，combineByKey() 使用前面的方法不断计算得到 c’。
     * 现在如果要求这两组 records 总的 combineByKey() 后的结果，那么可以使用 final c = mergeCombiners(c, c') 来计算；
     * 然后依据partitioner进行不同分区合并。
     * @param jsc
     */
    public static void CombineByKeyTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7, 1, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        //转化为pairRDD
        JavaPairRDD<Integer, Integer> javaPairRDD = javaRDD.mapToPair(integer -> new Tuple2<>(integer, 1));
        
        JavaPairRDD<Integer, String> combineByKeyRDD = javaPairRDD.combineByKey(
                v1 -> v1 + " :createCombiner: ",
                (v1, v2) -> v1 + " :mergeValue: " + v2,
                (v1, v2) -> v1 + " :mergeCombiners: " + v2);
        System.out.println("result~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + combineByKeyRDD.collect());
    }
    
    /**
     * groupByKey()是基于combineByKey()实现的， 只是将 Key 相同的 records 聚合在一起，一个简单的 shuffle 过程就可以完成。
     * ShuffledRDD 中的 compute() 只负责将属于每个 partition 的数据 fetch 过来，之后使用 mapPartitions() 操作进行 aggregate，
     * 生成 MapPartitionsRDD，到这里 groupByKey() 已经结束。最后为了统一返回值接口，将 value 中的 ArrayBuffer[] 数据结构抽象化成 Iterable[]。
     * groupByKey() 没有在 map 端进行 combine（mapSideCombine = false），
     * 这样设计是因为map 端 combine 只会省掉 partition 里面重复 key 占用的空间；
     * 但是，当重复 key 特别多时，可以考虑开启 combine。
     * @param jsc
     */
    public static void GroupByKeyTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(1, 2, 4, 3, 5, 6, 7);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data);
        //转为k，v格式
        JavaPairRDD<Integer, Integer> javaPairRDD = javaRDD.mapToPair(integer -> new Tuple2<>(integer, 1));
        
        JavaPairRDD<Integer, Iterable<Integer>> groupByKeyRDD = javaPairRDD.groupByKey(2);
        System.out.println(groupByKeyRDD.collect());
        
        //自定义partition
        JavaPairRDD<Integer, Iterable<Integer>> groupByKeyRDD3 = javaPairRDD.groupByKey(new Partitioner() {
            //partition各数
            @Override
            public int numPartitions() {
                return 10;
            }
            
            //partition方式
            @Override
            public int getPartition(Object o) {
                return (o.toString()).hashCode() % numPartitions();
            }
        });
        System.out.println(groupByKeyRDD3.collect());
    }
    
    /**
     * 该函数将两个分区RDD按照partition进行合并，形成一个新的RDD
     * zipPartitions函数生成ZippedPartitionsRDD2，该RDD继承ZippedPartitionsBaseRDD，
     * 在ZippedPartitionsBaseRDD中的getPartitions方法中判断需要组合的RDD是否具有相同的分区数，
     * 但是该RDD实现中并没有要求每个partitioner内的元素数量相同
     * @param jsc
     */
    public static void ZipPartitionsTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 3);
        List<Integer> data1 = Arrays.asList(3, 2, 12, 5, 6, 1);
        JavaRDD<Integer> javaRDD1 = jsc.parallelize(data1, 3);
        JavaRDD<String> zipPartitionsRDD = javaRDD.zipPartitions(javaRDD1, (integerIterator, integerIterator2) -> {
            LinkedList<String> linkedList = new LinkedList<>();
            while (integerIterator.hasNext() && integerIterator2.hasNext())
                linkedList.add(integerIterator.next().toString() + "_" + integerIterator2.next().toString());
            return linkedList.iterator();
        });
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + zipPartitionsRDD.collect());
    }
    
    /**
     * 该函数用于将两个RDD进行组合，组合成一个key/value形式的RDD
     * zip函数是基于zipPartitions实现的，其中preservesPartitioning为false，
     * preservesPartitioning表示是否保留父RDD的partitioner分区；
     * 另外，两个RDD的partition数量及元数的数量都是相同的，否则会抛出异常
     * @param jsc
     */
    public static void ZipTest(JavaSparkContext jsc) {
        List<Integer> data = Arrays.asList(5, 1, 1, 4, 4, 2, 2);
        JavaRDD<Integer> javaRDD = jsc.parallelize(data, 3);
        List<Integer> data1 = Arrays.asList(3, 2, 12, 5, 6, 1, 7);
        JavaRDD<Integer> javaRDD1 = jsc.parallelize(data1);
        JavaPairRDD<Integer, Integer> zipRDD = javaRDD.zip(javaRDD1);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" + zipRDD.collect());
    }
    
}
