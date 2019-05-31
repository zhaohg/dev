package com.zhaohg.prototype;

/**
 * 原型模式（Prototype）
 * <p>
 * 原型模式虽然是创建型的模式，但是与工程模式没有关系，从名字即可看出，该模式的思想就是将一个对象作为原型，
 * 对其进行复制、克隆，产生一个和原对象类似的新对象。本小结会通过对象的复制，进行讲解。在Java中，复制对象是通过clone()实现的
 */
public class Test {

    public static void main(String[] args) {
        Prototype obj = new Prototype();

        try {
            Prototype obj2 = (Prototype) obj.clone();

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

    }

}
