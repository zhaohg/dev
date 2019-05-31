package com.zhaohg.spark.chapter2;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Comparator;

public class Functions {
    public static Function2<Long, Long, Long>                  SUM_REDUCER                   = (a, b) -> a + b;
    public static Comparator<Long>                             LONG_NATURAL_ORDER_COMPARATOR = new LongComparator();
    public static Function<String, ApacheAccessLog>            PARSE_LOG_LINE                = ApacheAccessLog::parseFromLogLine;
    public static Function<ApacheAccessLog, Long>              GET_CONTENT_SIZE              = ApacheAccessLog::getContentSize;
    public static PairFunction<ApacheAccessLog, Integer, Long> GET_RESPONSE_CODE             = log -> new Tuple2<>(log.getResponseCode(), 1L);
    public static PairFunction<ApacheAccessLog, String, Long>  GET_IP_ADDRESS                = log -> new Tuple2<>(log.getIpAddress(), 1L);
    public static Function<Tuple2<String, Long>, Boolean>      FILTER_GREATER_10             = tuple -> tuple._2() > 10;
    public static Function<Tuple2<String, Long>, String>       GET_TUPLE_FIRST               = Tuple2::_1;
    public static PairFunction<ApacheAccessLog, String, Long>  GET_ENDPOINT                  = log -> new Tuple2<>(log.getEndpoint(), 1L);
    
    public static class ValueComparator<K, V> implements Comparator<Tuple2<K, V>>, Serializable {
        private Comparator<V> comparator;
        
        public ValueComparator(Comparator<V> comparator) {
            this.comparator = comparator;
        }
        
        public int compare(Tuple2<K, V> o1, Tuple2<K, V> o2) {
            return comparator.compare(o1._2(), o2._2());
        }
    }
    
    public static class LongComparator implements Comparator<Long>, Serializable {
        public int compare(Long a, Long b) {
            if (a > b)
                return 1;
            if (a.equals(b))
                return 0;
            return -1;
        }
    }
}
