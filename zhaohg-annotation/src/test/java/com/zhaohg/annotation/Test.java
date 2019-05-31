package com.zhaohg.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 註解
 */
public class Test {

    //解析注解
    public static void main(String[] args) {

        try {
            //1、使用类加载器加载类
            Class c = Class.forName("Child");
            //2、找到类上的注解
            boolean isexist = c.isAnnotationPresent(Description.class);
            if (isexist) {
                //3、拿到注解实例
                Description d = (Description) c.getAnnotation(Description.class);
                System.out.println(d.anthor());
            }
            //4、找到方法上的注解
            Method[] ms = c.getMethods();
            for (Method method : ms) {
                //5、找到方法上的注解
                boolean ismexist = method.isAnnotationPresent(Description.class);
                if (ismexist) {
                    Description d = method.getAnnotation(Description.class);
                    System.out.println(d.anthor());
                }
            }
            //另一种注解解析方法
            for (Method method : ms) {
                Annotation[] as = method.getAnnotations();
                for (Annotation annotation : as) {
                    if (annotation instanceof Description) {
                        Description d = (Description) annotation;
                        System.out.println(d.anthor());
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    public void sing() {
        Person person = new Child();
        person.sing();
    }

}
