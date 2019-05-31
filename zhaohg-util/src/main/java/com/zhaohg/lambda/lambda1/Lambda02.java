package com.zhaohg.lambda.lambda1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * 方法引用通过方法的名字来指向一个方法。
 * 方法引用可以使语言的构造更紧凑简洁，减少冗余代码。
 * 方法引用使用一对冒号(::)。
 */
public class Lambda02 {
    public static void main(String args[]) {

        //方法引用实例
        List names = new ArrayList();
        names.add("Google");
        names.add("Runoob");
        names.add("Taobao");
        names.add("Baidu");
        names.add("Sina");
        names.forEach(System.out::println);//将 System.out::println 方法作为静态方法来引用


        //构造器引用：它的语法是Class::new，或者更一般的Class< T >::new
        final Car car = Car.create(Car::new);
        final List<Car> cars = Arrays.asList(car);

        //静态方法引用：它的语法是Class::static_method
        cars.forEach(Car::collide);

        //特定类的任意对象的方法引用：它的语法是Class::method
        cars.forEach(Car::repair);

        //特定对象的方法引用：它的语法是instance::method
        final Car police = Car.create(Car::new);
        cars.forEach(police::follow);
    }

    public static class Car {
        public static Car create(final Supplier<Car> supplier) {
            return supplier.get();
        }

        public static void collide(final Car car) {
            System.out.println("Collided " + car.toString());
        }

        public void follow(final Car another) {
            System.out.println("Following the " + another.toString());
        }

        public void repair() {
            System.out.println("Repaired " + this.toString());
        }
    }
}