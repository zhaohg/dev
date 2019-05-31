package com.zhaohg.lambda.lambda2;


public class Person {

    String firstName;

    String lastName;

    Person() {
    }

    Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static void main(String[] args) {//方法与构造函数引用
        PersonFactory<Person> personFactory = Person::new;
        Person person = personFactory.create("Peter", "Parker");

        System.out.println("firstName = " + person.firstName + "   lastName = " + person.lastName);


    }
}