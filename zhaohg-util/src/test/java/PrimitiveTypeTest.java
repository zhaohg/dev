/**
 * Created by zhaohg on 2017/2/4.
 */
public class PrimitiveTypeTest {
    public static void main(String[] args) {
        // byte
        System.out.println("基本类型：byte 二进制位数：" + Byte.SIZE);
        System.out.println("包装类：java.lang.Byte");
        System.out.println("最小值：Byte.MIN_VALUE = " + Byte.MIN_VALUE);
        System.out.println("最大值：Byte.MAX_VALUE = " + Byte.MAX_VALUE);
        System.out.println();
        
        // short
        System.out.println("基本类型：short 二进制位数：" + Short.SIZE);
        System.out.println("包装类：java.lang.Short");
        System.out.println("最小值：Short.MIN_VALUE = " + Short.MIN_VALUE);
        System.out.println("最大值：Short.MAX_VALUE = " + Short.MAX_VALUE);
        System.out.println();
        
        // int
        System.out.println("基本类型：int 二进制位数：" + Integer.SIZE);
        System.out.println("包装类：java.lang.Integer");
        System.out.println("最小值：Integer.MIN_VALUE = " + Integer.MIN_VALUE);
        System.out.println("最大值：Integer.MAX_VALUE = " + Integer.MAX_VALUE);
        System.out.println();
        
        // long
        System.out.println("基本类型：long 二进制位数：" + Long.SIZE);
        System.out.println("包装类：java.lang.Long");
        System.out.println("最小值：Long.MIN_VALUE = " + Long.MIN_VALUE);
        System.out.println("最大值：Long.MAX_VALUE = " + Long.MAX_VALUE);
        System.out.println();
        
        // float
        System.out.println("基本类型：float 二进制位数：" + Float.SIZE);
        System.out.println("包装类：java.lang.Float");
        System.out.println("最小值：Float.MIN_VALUE = " + Float.MIN_VALUE);
        System.out.println("最大值：Float.MAX_VALUE = " + Float.MAX_VALUE);
        System.out.println();
        
        // double
        System.out.println("基本类型：double 二进制位数：" + Double.SIZE);
        System.out.println("包装类：java.lang.Double");
        System.out.println("最小值：Double.MIN_VALUE = " + Double.MIN_VALUE);
        System.out.println("最大值：Double.MAX_VALUE = " + Double.MAX_VALUE);
        System.out.println();
        
        // char
        System.out.println("基本类型：char 二进制位数：" + Character.SIZE);
        System.out.println("包装类：java.lang.Character");
        // 以数值形式而不是字符形式将Character.MIN_VALUE输出到控制台
        System.out.println("最小值：Character.MIN_VALUE = " + (int) Character.MIN_VALUE);
        // 以数值形式而不是字符形式将Character.MAX_VALUE输出到控制台
        System.out.println("最大值：Character.MAX_VALUE = " + (int) Character.MAX_VALUE);
        
        
        int i = 128;
        byte b = (byte) i;//转换过程中可能导致溢出或损失精度
        System.out.println("b = " + b);
        
        int a = (int) 23.7;
        int ab = (int) -45.89f;//浮点数到整数的转换是通过舍弃小数得到，而不是四舍五入
        
        char c1 = 'a';//定义一个char类型
        int i1 = c1;//char自动类型转换为int
        System.out.println("char自动类型转换为int后的值等于" + i1);
        char c2 = 'A';//定义一个char类型
        int i2 = c2 + 1;//char 类型和 int 类型计算
        System.out.println("char类型和int计算后的值等于" + i2);
        
        
    }
}