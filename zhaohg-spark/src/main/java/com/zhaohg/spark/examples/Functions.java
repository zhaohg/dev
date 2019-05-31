package com.zhaohg.spark.examples;

import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by zhaohg on 2017/3/6.
 */

public class Functions {
    public static Function<String, Customer> PARSE_CUSTOMER_LINE = (Function<String, Customer>)
            customerLine -> Customer.parseFromCustomerLine(customerLine);

    public static Function<Customer, Long> GET_CUSTOMER_ETIME = (Function<Customer, Long>)
            customer -> new Long(customer.getTimeElapsed());

    public static PairFunction<Customer, String, Long> GET_CUSTOMER_AMOUNT = (PairFunction<Customer, String, Long>)
            customer -> new Tuple2<>(customer.getFirstName() + " " + customer.getLastName(), customer.getAmount());

    public static Function2<Long, Long, Long> SUM_REDUCER = (Function2<Long, Long, Long>) (a, b) -> a + b;

    public static Function<Tuple2<String, Long>, Boolean> FILTER_AMOUNT_GREATER_10000   = (Function<Tuple2<String, Long>, Boolean>)
            tuple -> tuple._2() >= 10000;
    public static Comparator<Long>                        LONG_NATURAL_ORDER_COMPARATOR = new LongComparator();
    public static Function<String, Customer>              PARSE_LOG_LINE                = (Function<String, Customer>)
            customerLine -> Customer.parseFromCustomerLine(customerLine);

    public static class LongComparator implements Comparator<Long>, Serializable {
        @Override
        public int compare(Long a, Long b) {
            if (a > b)
                return 1;
            if (a.equals(b))
                return 0;
            return -1;
        }
    }

}