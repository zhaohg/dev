package com.zhaohg.lambda.lambda2;

public class TestConverter {

    //访问对象字段与静态变量
    static int outerStaticNum;
    int outerNum;

    public static void main(String[] args) {
        //函数式接口  访问局部变量
        Converter<String, Integer> converter1 = (from) -> Integer.valueOf(from);
        Integer converted1 = converter1.convert("123");
        System.out.println("TestConverter1 = " + converted1);    // 123

        Converter<String, Integer> converter2 = Integer::valueOf;
        Integer converted2 = converter2.convert("123");
        System.out.println("TestConverter2 = " + converted2);   // 123

        final int num1 = 1;
        Converter<Integer, String> converter3 = (from) -> String.valueOf(from + num1);
        String converted3 = converter3.convert(2);     // 3
        System.out.println("TestConverter3 = " + converted3);   // 123

        int num2 = 1;
        Converter<Integer, String> converter4 = (from) -> String.valueOf(from + num2);
        String converted4 = converter4.convert(2);     // 3 在lambda表达式中试图修改num同样是不允许的
        System.out.println("TestConverter3 = " + converted4);   //

    }

    void testScopes() {
        Converter<Integer, String> stringConverter1 = (from) -> {
            outerNum = 23;
            return String.valueOf(from);
        };
        Converter<Integer, String> stringConverter2 = (from) -> {
            outerStaticNum = 72;
            return String.valueOf(from);
        };
    }
}
