package com.zhaohg.lambda.lambda2;

import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by zhaohg on 2017/2/4.
 */
public class Lambda {

    public static void main(String[] args) throws Exception {

        // Predicates 断言，一般用来判断是否满足某条件
        Predicate<String> predicate = (s) -> s.length() > 0;

        predicate.test("foo");              // true
        predicate.negate().test("foo");     // false

        Predicate<Boolean> nonNull = Objects::nonNull;
        Predicate<Boolean> isNull = Objects::isNull;

        Predicate<String> isEmpty = String::isEmpty;
        Predicate<String> isNotEmpty = isEmpty.negate();

        // Functions 接收T对象，返回R对象
        Function<String, Integer> toInteger = Integer::valueOf;
        Function<String, String> backToString = toInteger.andThen(String::valueOf);//在toInteger加入apply结束后执行的方法，生成新的Function

        backToString.apply("123");     // "123"

        // Suppliers 类似工厂，调用时会返回一个指定类型的对象
        Supplier<Person> personSupplier = Person::new;
        personSupplier.get();   // new Person

        // Consumers 接收一个参数，无返回值
        Consumer<Person> greeter = (p) -> System.out.println("Hello, " + p.firstName);
        greeter.accept(new Person("Luke", "Skywalker"));

        // Comparators
        Comparator<Person> comparator = (p1, p2) -> p1.firstName.compareTo(p2.firstName);

        Person p1 = new Person("John", "Doe");
        Person p2 = new Person("Alice", "Wonderland");

        comparator.compare(p1, p2);             // > 0
        comparator.reversed().compare(p1, p2);  // < 0

        // Runnables
        Runnable runnable = () -> System.out.println(UUID.randomUUID());
        runnable.run();

        // Callables
        Callable<UUID> callable = UUID::randomUUID;
        callable.call();

        //UnaryOperator 执行一元操作（与、或、非）
        //BinaryOperator 接收两个参数，返回一个值

    }

    @FunctionalInterface
    interface Fun {
        void foo();
    }

}