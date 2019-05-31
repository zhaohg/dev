package com.zhaohg.chapter4;

import com.sun.btrace.BTraceUtils;
import com.sun.btrace.annotations.BTrace;
import com.sun.btrace.annotations.OnMethod;
import com.sun.btrace.annotations.ProbeClassName;
import com.sun.btrace.annotations.ProbeMethodName;

@BTrace
public class PrintSame {

    /**
     * 拦截同名方法
     * @param pcn
     * @param pmn
     * @param name
     */
    @OnMethod(
            clazz = "com.zhaohg.chapter4.Ch4Controller",
            method = "same"
    )
    public static void anyRead(@ProbeClassName String pcn, @ProbeMethodName String pmn, String name) {
        BTraceUtils.println(pcn + "," + pmn + "," + name);
        BTraceUtils.println();
    }
}
