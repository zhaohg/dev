package com.zhaohg.jvm.classloader;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

/**
 * 类加载器
 */
public class ClassLoaderTest {
    /**
     * 通过自定义的类加载器生成类的实例
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    private static Object getInstanceByUserDefinedClassLoader(String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        ClassLoader myLoader = new ClassLoader() {
            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                try {
                    String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
                    InputStream is = getClass().getResourceAsStream(fileName);
                    if (is == null) {
                        return super.loadClass(name);
                    }
                    byte[] b = new byte[is.available()];
                    is.read(b);
                    return defineClass(name, b, 0, b.length);
                } catch (IOException e) {
                    throw new ClassNotFoundException(name);
                }
            }
        };
        return myLoader.loadClass(className).newInstance();
    }

    @Test
    public void testClassLoader() throws Exception {
        Object obj = getInstanceByUserDefinedClassLoader("com.jvm.classloader.ClassLoaderTest");
        System.out.println(obj.getClass().getName());//输出 com.jvm.classloader.ClassLoaderTest
        System.out.println(obj.getClass()); //输出class com.jvm.classloader.ClassLoaderTest
        System.out.println(obj.getClass().getClassLoader().getClass());//输出class com.jvm.classloader.ClassLoaderTest$1

        /**
         * 比较两个类是否“相等”，只有在这两个类是由同一个类加载器加载的前提下才有意义，
         * 否则，即使这两个类来源于同一个Class文件，被同一个虚拟机加载，只要加载它们的类加载器不同，
         * 那这两个类就必定不相等。
         */
        System.out.println(obj instanceof ClassLoaderTest);//输出false,应为import进来的ClassLoaderTest是用系统加载器装载的
        System.out.println(ClassLoaderTest.class.getClassLoader().getClass()); //输出class sun.misc.Launcher$AppClassLoader

    }

    /**
     * 测试类的实例的类型转换
     * 运行时会抛出 ClassCastException异常,因为obj的类是自定义的类加载器装载的
     * @throws Exception
     */
    @Test(expected = ClassCastException.class)
    public void testInstanceCast() throws Exception {
        Object obj = getInstanceByUserDefinedClassLoader("com.jvm.classloader.ClassLoaderTest");
        ClassLoaderTest classLoaderTest = (ClassLoaderTest) obj;
    }

    /**
     * 测试两个类的成员变量的值,类成员变量的值没有累加,说明本身类就是独立的
     * <p>
     * 测试原理:如果是相同的类,则类成员变量两次执行静态方法会打印出2.如果不是相同的类,则两次都打印出1
     * </p>
     * @throws Exception
     */
    @Test
    public void testStaticVariableOfClass() throws Exception {
        Object obj = getInstanceByUserDefinedClassLoader("com.jvm.classloader.DataHolder");//current count value is 1

        Class.forName("com.jvm.classloader.DataHolder").newInstance();//current count value is 1

        //验证得到输出相同的值
    }

}